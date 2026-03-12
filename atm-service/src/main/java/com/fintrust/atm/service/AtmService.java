package com.fintrust.atm.service;

import com.fintrust.atm.client.AccountServiceClient;
import com.fintrust.atm.client.AuthServiceClient;
import com.fintrust.atm.client.CustomerNotifyClient;
import com.fintrust.atm.client.TransactionServiceClient;
import com.fintrust.atm.dto.*;
import com.fintrust.atm.entity.Card;
import com.fintrust.atm.entity.CardStatus;
import com.fintrust.atm.repository.CardRepository;
import com.fintrust.common.dto.ApiResponse;
import com.fintrust.common.exception.BadRequestException;
import com.fintrust.common.exception.ResourceNotFoundException;
import com.fintrust.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AtmService {

    private static final Logger log = LoggerFactory.getLogger(AtmService.class);
    private static final int MAX_INCORRECT_ATTEMPTS = 3;
    private static final long SESSION_TTL_MS = 5 * 60 * 1000;

    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;
    private final TransactionServiceClient transactionServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final AuthServiceClient authServiceClient;
    private final CustomerNotifyClient customerNotifyClient;
    private final String jwtSecret;

    public AtmService(CardRepository cardRepository,
                      PasswordEncoder passwordEncoder,
                      TransactionServiceClient transactionServiceClient,
                      AccountServiceClient accountServiceClient,
                      AuthServiceClient authServiceClient,
                      CustomerNotifyClient customerNotifyClient,
                      @Value("${jwt.secret:MySecretKeyForFinTrustBankThatIsLongEnoughForHS256Algorithm}") String jwtSecret) {
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
        this.transactionServiceClient = transactionServiceClient;
        this.accountServiceClient = accountServiceClient;
        this.authServiceClient = authServiceClient;
        this.customerNotifyClient = customerNotifyClient;
        this.jwtSecret = jwtSecret;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private String createSessionToken(String cardNumber, String accountNo, boolean pinVerified) {
        return Jwts.builder()
                .subject(cardNumber)
                .claim("accountNo", accountNo)
                .claim("pinVerified", pinVerified)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + SESSION_TTL_MS))
                .signWith(getSigningKey())
                .compact();
    }

    private Claims parseSessionToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Session expired or invalid");
        }
    }

    private AtmSessionDto getVerifiedSession(String sessionToken) {
        Claims claims = parseSessionToken(sessionToken);
        if (!Boolean.TRUE.equals(claims.get("pinVerified", Boolean.class))) {
            throw new UnauthorizedException("PIN not verified. Please validate PIN first.");
        }
        return AtmSessionDto.builder()
                .sessionToken(sessionToken)
                .cardNumber(claims.getSubject())
                .accountNo(claims.get("accountNo", String.class))
                .pinVerified(true)
                .build();
    }

    public AtmSessionDto validateCard(CardValidationRequest request) {
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException("Card is blocked. Please contact your bank.");
        }
        if (card.getStatus() == CardStatus.EXPIRED || card.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BadRequestException("Card has expired");
        }

        String token = createSessionToken(card.getCardNumber(), card.getAccountNo(), false);
        log.info("ATM session created for card: {}", maskCardNumber(card.getCardNumber()));

        return AtmSessionDto.builder()
                .sessionToken(token)
                .cardNumber(maskCardNumber(card.getCardNumber()))
                .accountNo(null)
                .pinVerified(false)
                .build();
    }

    @Transactional
    public AtmSessionDto validatePin(PinValidationRequest request) {
        Claims claims = parseSessionToken(request.getSessionToken());
        String cardNumber = claims.getSubject();

        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (!passwordEncoder.matches(request.getPin(), card.getPinHash())) {
            card.setIncorrectAttempts(card.getIncorrectAttempts() + 1);
            if (card.getIncorrectAttempts() >= MAX_INCORRECT_ATTEMPTS) {
                card.setStatus(CardStatus.BLOCKED);
                log.warn("Card blocked: {}", maskCardNumber(cardNumber));
            }
            cardRepository.save(card);
            throw new UnauthorizedException("Invalid PIN. Attempts remaining: " +
                    (MAX_INCORRECT_ATTEMPTS - card.getIncorrectAttempts()));
        }

        card.setIncorrectAttempts(0);
        cardRepository.save(card);

        String newToken = createSessionToken(cardNumber, card.getAccountNo(), true);
        log.info("PIN verified for card: {}", maskCardNumber(cardNumber));

        return AtmSessionDto.builder()
                .sessionToken(newToken)
                .cardNumber(maskCardNumber(cardNumber))
                .accountNo(card.getAccountNo())
                .pinVerified(true)
                .build();
    }

    public Map<String, Object> getBalance(String sessionToken) {
        AtmSessionDto session = getVerifiedSession(sessionToken);
        ApiResponse<Map<String, Object>> response = accountServiceClient.getBalance(session.getAccountNo());
        log.info("Balance inquiry for account: {}", session.getAccountNo());
        return response.getData();
    }

    @Transactional
    public Map<String, Object> withdraw(WithdrawRequest request) {
        AtmSessionDto session = getVerifiedSession(request.getSessionToken());
        Card card = cardRepository.findByCardNumber(session.getCardNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        BigDecimal newDailyTotal = card.getDailyWithdrawn().add(request.getAmount());
        if (newDailyTotal.compareTo(card.getDailyLimit()) > 0) {
            throw new BadRequestException("Daily withdrawal limit exceeded. Remaining: " +
                    card.getDailyLimit().subtract(card.getDailyWithdrawn()));
        }

        Map<String, Object> txnRequest = Map.of(
                "accountNo", session.getAccountNo(),
                "amount", request.getAmount(),
                "type", "WITHDRAWAL");
        ApiResponse<Map<String, Object>> txnResponse = transactionServiceClient.createAtmTransaction(txnRequest);

        card.setDailyWithdrawn(newDailyTotal);
        cardRepository.save(card);

        log.info("ATM withdrawal: {} from account {}", request.getAmount(), session.getAccountNo());
        return txnResponse.getData();
    }

    @Transactional
    public Map<String, Object> deposit(DepositRequest request) {
        AtmSessionDto session = getVerifiedSession(request.getSessionToken());

        Map<String, Object> txnRequest = Map.of(
                "accountNo", session.getAccountNo(),
                "amount", request.getAmount(),
                "type", "DEPOSIT");
        ApiResponse<Map<String, Object>> txnResponse = transactionServiceClient.createAtmTransaction(txnRequest);

        log.info("ATM deposit: {} to account {}", request.getAmount(), session.getAccountNo());
        return txnResponse.getData();
    }

    public List<Map<String, Object>> getMiniStatement(String sessionToken) {
        AtmSessionDto session = getVerifiedSession(sessionToken);
        ApiResponse<List<Map<String, Object>>> response =
                transactionServiceClient.getMiniStatement(session.getAccountNo());
        log.info("Mini statement retrieved for account: {}", session.getAccountNo());
        return response.getData();
    }

    public void endSession(String sessionToken) {
        log.info("ATM session ended (JWT is stateless — client discards token)");
    }

    public List<Card> listCards(CardStatus status) {
        if (status == null) return cardRepository.findAll();
        return cardRepository.findAll().stream().filter(c -> c.getStatus() == status).toList();
    }

    @Transactional
    public void setCardStatus(String cardNumber, CardStatus status) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus(status);
        cardRepository.save(card);
    }

    @Transactional
    public void unblockCard(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setStatus(CardStatus.ACTIVE);
        card.setIncorrectAttempts(0);
        cardRepository.save(card);
    }

    @Transactional
    public void resetAttempts(String cardNumber) {
        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));
        card.setIncorrectAttempts(0);
        cardRepository.save(card);
    }

    @Transactional
    public Map<String, Object> issueCard(String accountNo) {
        Card existing = cardRepository.findByAccountNo(accountNo).orElse(null);
        if (existing != null) {
            return Map.of(
                    "cardNumber", existing.getCardNumber(),
                    "expiryDate", existing.getExpiryDate().toString(),
                    "pin", null
            );
        }

        String cardNumber = generateUniqueCardNumber();
        String pin = String.format("%04d", new Random().nextInt(10000));

        Card card = Card.builder()
                .cardNumber(cardNumber)
                .accountNo(accountNo)
                .pinHash(passwordEncoder.encode(pin))
                .expiryDate(LocalDate.now().plusYears(5))
                .status(CardStatus.ACTIVE)
                .build();
        cardRepository.save(card);

        log.info("Issued new card {} for account {}", maskCardNumber(cardNumber), accountNo);
        return Map.of(
                "cardNumber", cardNumber,
                "expiryDate", card.getExpiryDate().toString(),
                "pin", pin
        );
    }

    private String generateUniqueCardNumber() {
        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            String num = "42" + String.format("%010d", Math.abs(r.nextLong()) % 1_000_000_0000L);
            if (cardRepository.findByCardNumber(num).isEmpty()) return num;
        }
        throw new BadRequestException("Unable to generate unique card number");
    }

    @Transactional
    public void changePin(ChangePinRequest request) {
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Card not found"));

        if (card.getStatus() == CardStatus.BLOCKED) {
            throw new BadRequestException("Card is blocked. Please contact admin to unblock.");
        }
        // Always use the account number actually linked to the card to avoid user input mismatch issues
        String linkedAccountNo = card.getAccountNo();

        ApiResponse<Boolean> accountValid = accountServiceClient.validateAccount(linkedAccountNo);
        if (accountValid.getData() == null || !accountValid.getData()) {
            throw new BadRequestException("Invalid account number");
        }

        try {
            authServiceClient.login(Map.of(
                    "username", request.getUsername(),
                    "password", request.getPassword()
            ));
        } catch (Exception ex) {
            throw new UnauthorizedException("Invalid username/password");
        }

        card.setPinHash(passwordEncoder.encode(request.getNewPin()));
        card.setIncorrectAttempts(0);
        cardRepository.save(card);
        log.info("ATM PIN changed for card: {}", maskCardNumber(card.getCardNumber()));

        try {
            customerNotifyClient.pinChanged(Map.of(
                    "accountNo", linkedAccountNo,
                    "cardMasked", maskCardNumber(card.getCardNumber())
            ));
        } catch (Exception ex) {
            log.warn("PIN-changed email notify failed: {}", ex.getMessage());
        }
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 8) return "****";
        return cardNumber.substring(0, 4) + "****" + cardNumber.substring(cardNumber.length() - 4);
    }
}
