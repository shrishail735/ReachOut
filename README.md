# ReachOut - Job Application Tracker

## Overview

ReachOut is a full-stack job application tracking platform that helps users manage and monitor their job applications efficiently.

The application follows a microservices architecture and provides secure authentication, application management, and email notifications for application status updates.

## Features

### Authentication

* User Registration
* User Login
* JWT-based Authentication
* Protected APIs

### Job Application Management

* Create Job Applications
* View All Applications
* Update Application Details
* Delete Applications
* Track Application Status

### Notifications

* Asynchronous email notifications
* RabbitMQ-based event-driven communication
* Automatic email alerts on application status changes

### API Gateway

* Centralized routing
* Single entry point for all backend services
* Simplified client communication

---

## Architecture

```text
React Frontend
       |
       v
API Gateway
       |
       v
Job Service
       |
       v
RabbitMQ
       |
       v
Notification Service
       |
       v
Email Provider
```

---

## Tech Stack

### Frontend

* React
* Vite
* JavaScript
* Axios
* React Router

### Backend

* Spring Boot
* Spring Security
* Spring Data JPA
* JWT Authentication
* Maven

### Messaging

* RabbitMQ
* CloudAMQP

### Database

* MySQL

### Tools & Platforms

* Git
* GitHub
* IntelliJ IDEA
* Postman

---

## Project Structure

```text
ReachOut
│
├── reachout-frontend
│
└── Backend
    │
    ├── api-gateway
    │
    ├── job-service
    │
    └── notification-service
```

---

## Microservices

### API Gateway

Responsibilities:

* Request Routing
* Single Entry Point
* Service Abstraction

Default Port:

```text
8080
```

---

### Job Service

Responsibilities:

* User Authentication
* JWT Generation
* Job Application CRUD Operations
* Event Publishing

Default Port:

```text
8081
```

---

### Notification Service

Responsibilities:

* RabbitMQ Consumer
* Email Notification Delivery

Default Port:

```text
8082
```

---

## RabbitMQ Flow

When an application status changes:

1. Job Service updates application status.
2. Job Service publishes a Notification Event.
3. RabbitMQ stores and routes the event.
4. Notification Service consumes the event.
5. Email notification is sent to the user.

---

## Environment Variables

Create local configuration files or environment variables before running the services.

### Job Service

Required Variables:

```env
DB_URL=jdbc:mysql://localhost:3306/reachout
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

JWT_SECRET=your_jwt_secret

RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
```

### Notification Service

Required Variables:

```env
MAIL_USERNAME=your_email
MAIL_PASSWORD=your_app_password

RABBITMQ_HOST=your_rabbitmq_host
RABBITMQ_USERNAME=your_rabbitmq_username
RABBITMQ_PASSWORD=your_rabbitmq_password
```

---

## Running the Project

### Clone Repository

```bash
git clone https://github.com/your-username/reachout.git
```

```bash
cd reachout
```

---

### Start Backend Services

Start services in the following order:

1. Job Service
2. Notification Service
3. API Gateway

---

### Start Frontend

```bash
cd reachout-frontend
npm install
npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

---

## API Endpoints

### Authentication

```http
POST /api/auth/register
```

```http
POST /api/auth/login
```

### Applications

```http
GET /api/applications
```

```http
POST /api/applications
```

```http
PUT /api/applications/{id}
```

```http
DELETE /api/applications/{id}
```

---

## Security

* JWT-based authentication
* Stateless session management
* Password hashing using BCrypt
* Protected API endpoints

---

## Future Enhancements

* Docker Support
* Kubernetes Deployment
* Service Discovery (Eureka)
* Load Balancing
* Refresh Token Support
* Role-Based Access Control (RBAC)
* CI/CD Pipeline
* Dashboard Analytics

---

## Learning Outcomes

This project helped in understanding:

* Microservices Architecture
* API Gateway Pattern
* Event-Driven Communication
* RabbitMQ Messaging
* JWT Authentication
* Spring Security
* REST API Development
* React Frontend Development
* Asynchronous Processing

---

## Author

Shrishail Kumbhar

Full Stack Developer
