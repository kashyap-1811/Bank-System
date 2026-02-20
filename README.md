# Bank System

A RESTful Banking System API built with **Spring Boot**, **Spring Security**, and **MySQL**. It supports role-based access control for two roles — `MANAGER` and `CUSTOMER` — and provides endpoints for managing customers, bank accounts, and transactions.

---

## Tech Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 3.4.4 |
| Spring Data JPA | (managed by Spring Boot) |
| Spring Security | (managed by Spring Boot) |
| MySQL | 8+ |
| Maven | 3.x |

---

## Prerequisites

- **Java 21** or higher ([Download](https://www.oracle.com/java/technologies/downloads/))
- **Maven 3.x** ([Download](https://maven.apache.org/download.cgi))
- **MySQL 8+** ([Download](https://dev.mysql.com/downloads/))
- A REST client such as **Postman** or **curl** for testing endpoints

---

## Clone the Repository

```bash
git clone https://github.com/kashyap-1811/Bank-System.git
cd Bank-System
```

---

## Database Setup

1. Start your MySQL server.
2. Create a database named `banking_system`:

```sql
CREATE DATABASE banking_system;
```

3. Open `src/main/resources/application.properties` and update the datasource credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_system
spring.datasource.username=root
spring.datasource.password=your_password
```

Hibernate will automatically create/update the required tables on first run (`spring.jpa.hibernate.ddl-auto=update`).

---

## Running the Application

### Using Maven Wrapper (recommended)

```bash
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

### Using Maven directly

```bash
mvn spring-boot:run
```

The application starts on **`http://localhost:8080`** by default.

---

## Authentication

The API uses **HTTP Basic Authentication**. All requests must include valid credentials (`email` + `password`).

Two roles are supported:

| Role | Description |
|---|---|
| `ROLE_MANAGER` | Full administrative access |
| `ROLE_CUSTOMER` | Access to own customer/account data and transfers |

> **Note:** The first manager account must be inserted directly into the database (password must be BCrypt-encoded).

---

## API Endpoints

### Customer Endpoints (`/customers`)

| # | Method | Endpoint | Role | Description |
|---|---|---|---|---|
| 1 | `POST` | `/customers/add` | MANAGER | Create a new customer |
| 2 | `GET` | `/customers/{customerId}` | CUSTOMER, MANAGER | Get customer details by ID |
| 3 | `PUT` | `/customers/{customerId}` | CUSTOMER | Update own customer profile |
| 4 | `DELETE` | `/customers/{customerId}` | MANAGER | Delete a customer |

**Create / Update Customer — Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "secret123",
  "city": "Mumbai",
  "phoneNo": 9876543210,
  "enabled": true,
  "role": "ROLE_CUSTOMER"
}
```

---

### Bank Account Endpoints (`/accounts`)

| # | Method | Endpoint | Role | Description |
|---|---|---|---|---|
| 5 | `POST` | `/accounts/{customerId}/create/{type}` | MANAGER | Create a bank account (`savings` or `current`) for a customer |
| 6 | `POST` | `/accounts/{accountId}/add-customer/{customerId}` | MANAGER | Link an additional customer to an account |
| 7 | `DELETE` | `/accounts/{accountId}/remove-customer/{customerId}` | MANAGER | Remove a customer from an account |
| 8 | `DELETE` | `/accounts/{accountId}/delete` | MANAGER | Delete a bank account |
| 9 | `GET` | `/accounts/{accountId}` | CUSTOMER, MANAGER | Get account details by ID |
| 10 | `PATCH` | `/accounts/{accountId}/status?isActive=true` | MANAGER | Enable or disable an account |
| 11 | `GET` | `/accounts/customer/{customerId}` | CUSTOMER, MANAGER | Get all accounts for a customer |
| 12 | `GET` | `/accounts/all` | MANAGER | Get all accounts in the system |

**Create Account — Request Body** (with `?extraValue=<value>` query param):

- For a **Savings Account** (`type=savings`), `extraValue` sets the **minimum balance**.
- For a **Current Account** (`type=current`), `extraValue` sets the **overdraft limit**.

```bash
POST /accounts/1/create/savings?extraValue=500
```
```json
{
  "balance": 5000,
  "active": true,
  "openingDate": "2024-01-01"
}
```

---

### Transaction Endpoints (`/transactions`)

| # | Method | Endpoint | Role | Description |
|---|---|---|---|---|
| 13 | `POST` | `/transactions/credit/{accountNumber}?amount=<value>` | MANAGER | Credit an amount to an account |
| 14 | `POST` | `/transactions/debit/{accountNumber}?amount=<value>` | MANAGER | Debit an amount from an account |
| 15 | `POST` | `/transactions/transfer?fromAccount=<id>&toAccount=<id>&amount=<value>` | CUSTOMER, MANAGER | Transfer funds between two accounts |
| 16 | `GET` | `/transactions/all` | MANAGER | Get the 10 most recent transactions |
| 17 | `GET` | `/transactions/account/{accountNumber}` | CUSTOMER, MANAGER | Get the 20 most recent transactions for an account |
| 18 | `GET` | `/transactions/{transactionId}` | CUSTOMER, MANAGER | Get a transaction by ID |

---

## Project Structure

```
Bank-System/
├── src/
│   └── main/
│       ├── java/com/kashyap/BankSystem/
│       │   ├── BankSystemApplication.java       # Application entry point
│       │   ├── config/
│       │   │   └── SecurityConfig.java          # Spring Security & role-based access
│       │   ├── controllers/
│       │   │   ├── CustomerController.java      # Customer REST endpoints
│       │   │   ├── BankAccountController.java   # Account REST endpoints
│       │   │   └── TransactionController.java   # Transaction REST endpoints
│       │   ├── entities/
│       │   │   ├── Customer.java                # Customer entity
│       │   │   ├── BankAccount.java             # Base bank account entity
│       │   │   ├── SavingsAccount.java          # Savings account (min balance)
│       │   │   ├── CurrentAccount.java          # Current account (overdraft limit)
│       │   │   └── Transaction.java             # Transaction entity
│       │   ├── repository/                      # Spring Data JPA repositories
│       │   ├── services/                        # Business logic layer
│       │   └── zexception/
│       │       └── GlobalExceptionHandler.java  # Centralized exception handling
│       └── resources/
│           └── application.properties           # App & DB configuration
└── pom.xml                                      # Maven dependencies
```

---

## License

This project is intended for educational purposes.