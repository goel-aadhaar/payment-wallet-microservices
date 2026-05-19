# Payment Wallet Application

A complete, production-grade Payment Wallet platform built with a **Spring Boot 4 / Java 25 Microservices Backend** and a **Next.js 15 / Tailwind CSS v4 Frontend**.

## 🏗️ Architecture Overview

The system is composed of several independent microservices communicating through a centralized API Gateway.

### Backend Services
- **API Gateway (`port 8080`)**: Central entry point. Handles routing, JWT authentication, and rate limiting (via Bucket4j).
- **User Service (`port 8081`)**: Manages user registration, login, and profile data. Issues JWT tokens.
- **Wallet Service (`port 8088`)**: Core financial engine. Handles balance, credits, debits, and temporary fund holds for safe transactions.
- **Transaction Service (`port 8082`)**: Processes money transfers between users by coordinating with the Wallet Service.
- **Notification Service (`port 8083`)**: Stores and delivers alerts (e.g., successful transfers).
- **Reward Service (`port 8084`)**: Calculates and distributes cashback/points based on transaction activity.

### Frontend Application
- **Next.js App Router**: Located in the `/frontend` directory. Provides a modern, responsive UI with a dashboard, transaction history, and rewards tracker.

## 🛠️ Tech Stack

**Backend:**
- **Language**: Java 25
- **Framework**: Spring Boot 4.0.6
- **Database**: H2 In-Memory Database (per service)
- **Security**: JWT (JSON Web Tokens)
- **API Docs**: Springdoc OpenAPI v3
- **Rate Limiting**: Bucket4j

**Frontend:**
- **Framework**: Next.js 15 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS v4
- **Icons**: Lucide React
- **HTTP Client**: Axios

---

## 🚀 How to Run the Application

### Prerequisites
- **Java 25** (Ensure `JAVA_HOME` is set correctly)
- **Maven** (3.8+)
- **Node.js** (18+)
- **npm** (9+)
- **Docker & Docker Compose** (Optional, but recommended)

### 🐳 Running with Docker Compose (Recommended)

The easiest way to run the entire stack (all microservices, frontend, Kafka, and Zookeeper) is via Docker Compose.

1. Ensure **Docker Desktop** is running on your machine.
2. Open a terminal in the project root and run:
   ```bash
   docker-compose build
   docker-compose up -d
   ```
*(Note: The first build will take a few minutes as Maven downloads dependencies inside the Docker image.)*

Once running, access the frontend at **http://localhost:3000** and the API Gateway at **http://localhost:8080**.

---

### 💻 Running Manually (Without Docker)

### 1. Starting the Backend Microservices

You need to start the API Gateway and the required microservices. Open a terminal for each service, navigate to the project root, and use the Maven Spring Boot plugin.

*Alternatively, you can import the parent `pom.xml` into your IDE (IntelliJ/Eclipse) and run the main application classes directly.*

**Terminal 1: Service Discovery / API Gateway**
```bash
cd api-gateway
mvn spring-boot:run
```

**Terminal 2: User Service**
```bash
cd user-service
mvn spring-boot:run
```

**Terminal 3: Wallet Service**
```bash
cd wallet-service
mvn spring-boot:run
```

**Terminal 4: Transaction Service**
```bash
cd transaction-service
mvn spring-boot:run
```

**Terminal 5: Notification Service**
```bash
cd notification-service
mvn spring-boot:run
```

**Terminal 6: Reward Service**
```bash
cd reward-service
mvn spring-boot:run
```

> **Note**: Wait for all services to initialize. The API Gateway running on port `8080` will automatically route traffic to the respective downstream services.

### 2. Starting the Frontend

Open a new terminal, navigate to the `frontend` folder, install dependencies, and start the development server.

```bash
cd frontend
npm install
npm run dev
```

The frontend will be available at **[http://localhost:3000](http://localhost:3000)**.

---

## 📖 API Documentation & Testing

### Interactive Swagger UI
Because the backend is equipped with `springdoc-openapi`, you can view the fully documented, interactive Swagger UI for any running service directly in your browser:
- **Wallet API:** [http://localhost:8088/swagger-ui.html](http://localhost:8088/swagger-ui.html)
- **User/Auth API:** [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
- **Transaction API:** [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)

### Postman Collection
A complete Postman collection is included in the project root: `payment_wallet.postman_collection.json`.
1. Open Postman and click **Import**.
2. Select the `payment_wallet.postman_collection.json` file.
3. The collection is pre-configured with a `{{jwt_token}}` variable. Simply run the **Login** request, copy the token from the response, and paste it into the Collection Variables to authenticate all subsequent requests.

---

## 💡 Quick Start Guide (Testing the Flow)

1. Navigate to **http://localhost:3000/signup** and create a new account.
2. Log in with your new credentials.
3. On the **Dashboard**, click **Add Funds** to instantly credit your wallet.
4. Open an incognito window, create a *second* user account, and log in.
5. In your first window, go to **Transactions**, click **Send Money**, and enter the second user's ID along with an amount.
6. Check your **Rewards** tab—you may have earned some cashback!
