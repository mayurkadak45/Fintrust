package com.fintrust.customer.service;

import com.fintrust.common.exception.ResourceNotFoundException;
import com.fintrust.customer.dto.BeneficiaryDto;
import com.fintrust.customer.entity.Beneficiary;
import com.fintrust.customer.entity.Customer;
import com.fintrust.customer.repository.BeneficiaryRepository;
import com.fintrust.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final CustomerRepository customerRepository;

    public List<BeneficiaryDto> getBeneficiaries(String username) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return beneficiaryRepository.findByCustomerId(customer.getId()).stream()
                .map(b -> BeneficiaryDto.builder()
                        .id(b.getId())
                        .accountNo(b.getAccountNo())
                        .ifsc(b.getIfsc())
                        .beneficiaryName(b.getBeneficiaryName())
                        .status(b.getStatus().name())
                        .build())
                .toList();
    }

    public BeneficiaryDto addBeneficiary(String username, BeneficiaryDto dto) {
        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Beneficiary beneficiary = Beneficiary.builder()
                .customer(customer)
                .accountNo(dto.getAccountNo())
                .ifsc(dto.getIfsc())
                .beneficiaryName(dto.getBeneficiaryName())
                .build();

        beneficiary = beneficiaryRepository.save(beneficiary);

        return BeneficiaryDto.builder()
                .id(beneficiary.getId())
                .accountNo(beneficiary.getAccountNo())
                .ifsc(beneficiary.getIfsc())
                .beneficiaryName(beneficiary.getBeneficiaryName())
                .status(beneficiary.getStatus().name())
                .build();
    }

    public void deleteBeneficiary(Long id) {
        Beneficiary beneficiary = beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Beneficiary not found"));
        beneficiaryRepository.delete(beneficiary);
    }
}
