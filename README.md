# FinTrust Bank - Digital Banking System

FinTrust Bank is a full-stack digital banking application built on a microservices architecture. It provides a comprehensive suite of banking features, including account management, secure fund transfers, and a virtual ATM simulation.

## 🚀 Architecture Overview

The system is designed using a microservices pattern to ensure scalability and independent deployment.


### Backend Technologies
* **Java 21 & Spring Boot 3.2.3**
* **Spring Cloud Gateway**: Centralized routing and security filter.
* **JWT (JSON Web Tokens)**: Stateless authentication across all services.
* **Spring Data JPA**: For database persistence.
* **SpringDoc OpenAPI**: Interactive API documentation (Swagger).

### Frontend Technologies
* **React 18**: Component-based UI.
* **Vite**: Fast build tool and development server.
* **Tailwind CSS**: Utility-first CSS framework for responsive design.
* **Zustand**: Lightweight state management.
* **Axios**: For handling API communications.

---

## 🏗️ Project Structure

The project is organized into a parent Maven project with several modules:

- `api-gateway`: Port 8080. Handles routing and JWT validation.
- `auth-service`: Port 8081. User authentication and role management.
- `customer-service`: Port 8082. Account summaries, registration, and KYC.
- `transaction-service`: Port 8083. Fund transfers and transaction history.
- `atm-service`: Port 8084. Virtual ATM simulation (Withdraw/Deposit/PIN).
- `common-lib`: Shared library containing global exceptions and DTOs.
- `frontend`: React application providing the user and admin dashboards.

---

## 🔑 Key Features

### User Features
* **Self-Registration**: New users can apply for accounts and upload documents.
* **Secure Login**: JWT-based authentication with password change capabilities.
* **Dashboard**: View account summaries and real-time balances.
* **Fund Transfer**: Secure money movement between accounts.
* **Virtual ATM**: Simulate card validation, cash withdrawal, and deposits.
* **Transaction History**: View full history or download a mini-statement.

### Admin Features
* **Account Approval**: Review and approve pending account applications.
* **Transaction Monitoring**: Oversee all system-wide transactions.
* **Customer Management**: Search and manage customer profiles.

---

## 🛠️ Getting Started

### Prerequisites
* JDK 21
* Maven 3.8+
* Node.js (v18+)

### Setup Instructions

1.  **Clone the Repository**:
    ```bash
    git clone [https://github.com/shantanulanjewar12/fintrust-banking-system.git](https://github.com/shantanulanjewar12/fintrust-banking-system.git)
    cd fintrust-banking-system
    ```

2.  **Build the Backend**:
    ```bash
    mvn clean install
    ```

3.  **Run the Services**:
    Start each microservice (Auth, Customer, Transaction, ATM, and finally API Gateway) by running the main class in each module or using the Spring Boot Maven plugin:
    ```bash
    mvn spring-boot:run -pl api-gateway
    ```

4.  **Launch the Frontend**:
    ```bash
    cd frontend
    npm install
    npm run dev
    ```

---

## 🛡️ Security
* **API Gateway**: All requests (except login/register) are filtered through a `JwtAuthFilter` to ensure only authenticated users can access resources.
* **Internal Endpoints**: Specific endpoints are marked for internal use only between microservices.

## 📝 API Documentation
Once the services are running, you can access the Swagger UI for each service to explore the endpoints:
* API Gateway: `http://localhost:8080/swagger-ui.html`
