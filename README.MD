# Octopus Backend (Octopus-BE)

The Backend infrastructure for the **Octopus** ecosystem, built on a robust **Microservices** architecture using **Spring Boot**, **Spring Cloud**, and **NestJs**. This system is engineered for high scalability, real-time communication, and modular workspace management.

---

## 🛠️ Tech Stack

| Category | Technologies |
| :--- | :--- |
| **Language** | Java | TypeScript
| **Framework** | Spring Boot, Spring Cloud, NestJS |
| **Service Discovery** | Netflix Eureka |
| **Gateway** | Spring Cloud Gateway |
| **Databases** | MySQL (Relational), MongoDB (NoSQL), Redis (Cache) |
| **Messaging** | Apache Kafka |
| **DevOps** | Docker, Kubernetes (Helm), GitLab CI |

---

## 🏗️ System Architecture

The project is divided into several specialized microservices to ensure isolation and scalability:

### Core Infrastructure
* **`eureka-service`**: Handles service registration and discovery.
* **`gateway`**: Acts as the centralized API entry point with routing and security filters.

### Business Logic Services
* **`auth-service`**: Manages user authentication, registration, and JWT issuance.
* **`workspace-service`**: Core logic for managing workspaces, boards, and tasks.
* **`message-service`**: Handles real-time messaging and chat persistence via MongoDB.
* **`mail-service`**: Asynchronous email delivery service.
* **`storage-service`**: Dedicated service for managing file uploads and assets.

### Shared Modules
* **`dto-models`**: Centralized Data Transfer Objects.
* **`auth-utils` / `exception-utils`**: Internal shared libraries for security and error handling.

---

## 🚀 Getting Started

### Prerequisites
* **JDK 11** or higher
* **Maven 3.6+**
* **Docker** & **Docker Compose**

### Setup & Run

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/hoangdh2001/octopus-be.git](https://github.com/hoangdh2001/octopus-be.git)
    cd octopus-be
    ```

2.  **Build the entire project:**
    ```bash
    mvn clean install -DskipTests
    ```

3.  **Spin up the infrastructure (Databases, Kafka, Redis):**
    ```bash
    docker-compose up -d
    ```

4.  **Access the dashboards:**
    * **Eureka Dashboard**: `http://localhost:8761`
    * **API Gateway**: `http://localhost:8080`

---

## 🚢 Deployment

The system is production-ready with **Kubernetes** support using **Helm**:

* **Helm Charts**: Located in `deployment/helm/` for automated orchestration.
* **GitOps**: Integrated with GitLab Agent for seamless CI/CD pipelines.
* **Ingress**: Pre-configured for Nginx Ingress Controller.

### Sample Helm Deployment:
```bash
helm upgrade --install octopus-app deployment/helm/app/ \
  --namespace octopus-backend \
  --set ingress.domainSuffix=yourdomain.com
