# Bajaj Finserv Java Qualifier

This repository contains a Spring Boot application developed for the Bajaj Finserv Java Qualifier test. The project demonstrates a robust backend architecture using Java 17, Spring Boot 3, and an H2 in-memory database. It is designed to be modular, maintainable, and ready for both development and production deployment.

## Table of Contents
- [Features](#features)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [API Overview](#api-overview)
- [Database](#database)
- [Testing](#testing)
- [Contributing](#contributing)
- [License](#license)

## Features
- **Spring Boot 3.2.0**: Modern, production-ready framework for building Java applications.
- **Java 17**: Leverages the latest LTS version of Java for improved performance and language features.
- **RESTful APIs**: Exposes endpoints for client interaction using Spring Web and WebFlux for both synchronous and reactive programming.
- **DTO Layer**: Clean separation of data transfer objects (`SolutionRequest`, `WebhookRequest`, `WebhookResponse`) for request/response modeling.
- **Service Layer**: Business logic encapsulated in services (`SqlQueryService`, `WebhookService`) for maintainability and testability.
- **Runner Support**: Includes an `ApplicationRunner` for executing code at startup, useful for initialization or demo data.
- **H2 In-Memory Database**: Fast, zero-setup database for development and testing. No external DB required.
- **Spring Data JPA**: Simplifies data access and ORM with repository support.
- **Configurable Properties**: Centralized configuration via `application.properties`.
- **Logging**: Integrated with Spring Boot’s logging starter for easy debugging and monitoring.
- **Executable JAR**: Easily build and run as a standalone JAR file.
- **Unit Testing**: Ready for testing with Spring Boot’s test starter and sample test structure.

## Project Structure
```
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/com/bajajfinserv/qualifier/
│   │   │   ├── BajajFinservQualifierApplication.java   # Main entry point
│   │   │   ├── dto/                                   # Data Transfer Objects
│   │   │   ├── runner/                                # Application runner
│   │   │   └── service/                               # Business logic
│   │   └── resources/application.properties           # App configuration
│   └── test/
│       └── java/com/bajajfinserv/qualifier/service/   # Unit tests
└── target/
```

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

### Build the Project
```
mvn clean package -DskipTests
```

### Run the Application
```
mvn spring-boot:run
```
Or run the packaged JAR:
```
java -jar target/java-qualifier-1.0.0.jar
```

### Configuration
- All configuration is managed in `src/main/resources/application.properties`.
- You can adjust server port, database settings, and other properties as needed.

## API Overview
- The application exposes RESTful endpoints for various operations (see service and DTO classes for details).
- Example endpoints (actual URLs depend on your controller/service implementation):
	- `POST /api/solution` — Accepts a `SolutionRequest` and returns a result.
	- `POST /api/webhook` — Handles webhook requests and returns a `WebhookResponse`.
- All endpoints use JSON for request and response bodies.
- Error handling and validation are managed via Spring Boot’s exception handling mechanisms.

## Database
- Uses H2 in-memory database by default (no setup required).
- Data is volatile and resets on each application restart.
- H2 Console (if enabled): `http://localhost:8080/h2-console`
- Database schema and data can be initialized via JPA entities or SQL scripts.

## Testing
- Unit and integration tests are located under `src/test/java`.
- To run tests:
```
mvn test
```
- Uses Spring Boot’s testing framework for easy mocking and context loading.

## Contributing
Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

## License
This project is for assessment and demonstration purposes only. Not for production use.
