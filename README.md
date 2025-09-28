# RentX Rental Property Management System

RentX is a comprehensive rental property management system built with **Spring Boot** and modern web technologies. The application provides RESTful API endpoints for three core user roles: **Admin**, **Tenant**, and **Landlord**, offering a scalable and cloud-ready solution for property management.

---

## Table of Contents

- [About](#about)
- [Features](#features)
- [User Roles](#user-roles)
- [Technology Stack](#technology-stack)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Contact](#contact)

---

## About

RentX streamlines the process of managing rental properties through a modern REST API architecture. The system allows users to handle property listings, tenant-landlord interactions, payment management, and more—all through secure, scalable web services that can be consumed by web applications, mobile apps, or third-party integrations.

---

## Features

RentX includes the following comprehensive features:

- **RESTful API Architecture**: Modern REST endpoints for all operations
- **JWT-based Authentication**: Secure token-based authentication system
- **Role-based Access Control**: Separate permissions for Admin, Tenant, and Landlord
- **Property Management**: Add, modify, and remove rental property details via API
- **Tenant Management**: Track tenant information, lease agreements, and payment history
- **Landlord Portal**: API endpoints for landlords to manage properties and monitor tenants
- **Admin Tools**: Administrative API for platform oversight, user moderation, and reporting
- **Booking System**: Complete booking workflow with status management
- **Search & Filtering**: Advanced property search with location-based filtering
- **Database Integration**: JPA/Hibernate with support for MySQL and H2
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Cloud-Ready**: Containerizable and deployment-ready for cloud platforms

---

## User Roles

- **Admin**: Full platform management, user moderation, reporting, and analytics
- **Landlord**: Add/manage properties, view tenant details, and track bookings
- **Tenant**: Browse properties, manage rental agreements, and create bookings

---

## Technology Stack

- **Language**: Java 17/21
- **Framework**: Spring Boot 3.3.4 (latest)
- **Security**: Spring Security with JWT 0.12.6
- **Database**: MySQL (production) / H2 (development)
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven 3.8+
- **API Documentation**: OpenAPI/Swagger 2.6.0
- **API Documentation**: OpenAPI 3.0 (Swagger)
- **Build Tool**: Maven
- **Architecture**: RESTful Microservice

---

## API Endpoints

### Public Endpoints
- `GET /api/public/welcome` - Welcome message and API information
- `GET /api/public/health` - Health check endpoint
- `GET /swagger-ui.html` - Interactive API documentation
- `GET /h2-console` - Database console (development mode)

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration (tenant/landlord)
- `GET /api/auth/profile` - Get user profile

### Property Management
- `GET /api/properties/all` - List all properties
- `GET /api/properties/available` - List available properties
- `POST /api/properties/add` - Add new property (landlord)
- `PUT /api/properties/update/{id}` - Update property (landlord)
- `DELETE /api/properties/delete/{id}` - Delete property (landlord)

### User Management
- `GET /api/admin/users` - List all users (admin)
- `POST /api/admin/users/ban/{id}` - Ban user (admin)
- `POST /api/admin/users/unban/{id}` - Unban user (admin)

### Booking System
- `GET /api/tenant/bookings` - View tenant bookings
- `POST /api/tenant/book/{propertyId}` - Book a property
- `GET /api/landlord/bookings` - View property bookings

---

## Getting Started

### Prerequisites
- **Java**: JDK 17 or JDK 21 (recommended)
- **Maven**: 3.8+
- **Database**: MySQL 8.0+ (for production) or H2 (for development/testing)

### Installation & Quick Start

1. **Clone the repository**:
   ```bash
   git clone https://github.com/vishnu9358862212/RentX-rental-website.git
   cd RentX-rental-website
   ```

2. **Build the project**:
   ```bash
   # For JDK 17 (default)
   mvn clean compile
   
   # For JDK 21 
   mvn clean compile -Pjdk21
   ```

3. **Run the application**:
   ```bash
   # Option 1: Using Maven (recommended for development)
   mvn spring-boot:run
   
   # Option 2: Using JAR file (for production)
   mvn clean package
   java -jar target/rentx-rental-system-1.0.0.jar
   
   # Option 3: With custom port
   mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
   ```

The application will start on `http://localhost:8080/api`

### Quick Test & Access Points

- **Main API**: http://localhost:8080/api/
- **API Documentation**: http://localhost:8080/api/swagger-ui.html  
- **H2 Console**: http://localhost:8080/api/h2-console (development only)

```bash
# Test the welcome endpoint
curl http://localhost:8080/api/public/welcome

# Check application health  
curl http://localhost:8080/api/public/health
```

### Terminal Commands Reference

#### Development Commands
```bash
# Clean and compile
mvn clean compile

# Run with live reload
mvn spring-boot:run

# Run tests  
mvn test

# Package for production
mvn clean package
```

#### JDK 21 Commands
```bash
# Build with JDK 21
mvn clean compile -Pjdk21

# Run with JDK 21
mvn spring-boot:run -Pjdk21

# Package with JDK 21
mvn clean package -Pjdk21
```

#### Production Commands
```bash
# Build production JAR
mvn clean package -DskipTests

# Run production JAR
java -jar target/rentx-rental-system-1.0.0.jar

# Run with profile and port
java -jar target/rentx-rental-system-1.0.0.jar --spring.profiles.active=prod --server.port=8080
```

### Troubleshooting

#### Common Issues:
1. **JDK 21 Profile Error**: Ensure JDK 21 is installed to use `-Pjdk21` profile
2. **Port already in use**: Change port with `--server.port=8081`  
3. **Build failures**: Run `mvn clean` first
4. **Database connection**: Check MySQL service or use H2 for testing

#### Useful Commands:
```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

---

## Configuration

### Database Configuration

#### Development (H2)
The application uses H2 in-memory database by default for development:
```properties
spring.datasource.url=jdbc:h2:mem:rentx_db
spring.h2.console.enabled=true
```

#### Production (MySQL)
For production, configure MySQL connection:
```properties
DB_URL=jdbc:mysql://localhost:3306/rental_system
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

### Environment Variables
- `DB_URL` - Database connection URL
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing secret key

---

## API Documentation

### Interactive Documentation
Access the interactive API documentation at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

### Sample API Calls

#### Register a new tenant:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "userType": "TENANT"
  }'
```

#### Login:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123",
    "userType": "TENANT"
  }'
```

---

## Contact

For inquiries, reach out to the project maintainer via [GitHub](https://github.com/vishnu9358862212).

---

*This project demonstrates modern Spring Boot development practices, RESTful API design, security implementation, and cloud-ready architecture. It showcases skills in enterprise Java development, microservices architecture, and full-stack application development.*
