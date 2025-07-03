# Doctor Appointment System API

## Overview

This project is a backend API for a Doctor Appointment System. It allows patients to find doctors, book appointments, and manage their health-related interactions. Doctors can manage their profiles, branches, services (diseases they treat), and appointments. The system also includes features for user authentication, profile management, and chat functionality between patients and doctors.

## Tech Stack

*   Java
*   Spring Boot (including Spring MVC, Spring Data JPA, Spring Security)
*   Maven (for build and dependency management)
*   PostgreSQL (or other relational database, configured in `application.properties`)
*   JWT (for token-based authentication)
*   Swagger/OpenAPI (for API documentation)
*   MapStruct (for DTO mapping)
*   MinIO (for certificate file storage, if configured)

## Project Structure and Architecture

The project follows a layered architecture pattern:

*   **Controller Layer (`<Entity>Controller`)**: Handles incoming HTTP requests, validates input, and delegates business logic to the Service layer. Implements a corresponding `<Entity>ControllerApi` interface which defines the API contract.
*   **API Interface Layer (`<Entity>ControllerApi`)**: Defines the contract for each controller using standard annotations for request mappings, parameters, and Swagger/OpenAPI documentation.
*   **Service Layer (`<Entity>Service` - Interface)**: Defines the business logic operations for each entity or domain.
*   **Service Implementation Layer (`<Entity>ServiceImpl`)**: Implements the `EntityService` interface, containing the core business logic, transaction management, and coordination between repositories and mappers.
*   **Mapper Layer (`<Entity>Mapper`)**: Uses MapStruct to convert between JPA Entities and View DTOs.
*   **View DTO Layer (`<Entity>View`)**: Data Transfer Objects used for transferring data from the service layer to the controller layer, and sometimes internally within the service layer. These are distinct from Request/Response DTOs used directly in controller signatures.
*   **Request/Response DTO Layer (`model` package within each entity's package)**: These DTOs (`<Entity>Request`, `<Entity>Response`) are used directly in the controller method signatures for request bodies and responses, ensuring a clear API contract.
*   **Repository Layer (`<Entity>Repository`)**: Uses Spring Data JPA for data access and persistence.
*   **Entity Layer (`<Entity>Entity`)**: JPA entities representing the database schema.

This structured approach was implemented to improve separation of concerns, maintainability, and testability of the codebase.

## Setup and Running the Project

### Prerequisites

*   Java JDK (version specified in `pom.xml`, typically 17 or higher)
*   Maven
*   PostgreSQL (or your configured database) running and accessible.
*   MinIO server (if certificate upload functionality is to be used).

### Configuration

1.  **Database**:
    *   Open `src/main/resources/application.properties`.
    *   Configure the database connection properties:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
        spring.datasource.username=your_db_username
        spring.datasource.password=your_db_password
        spring.jpa.hibernate.ddl-auto=update # Or 'validate' for production, 'create-drop' for testing
        spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
        ```
2.  **JWT Secret**:
    *   Set the JWT secret key and expiration times in `application.properties`:
        ```properties
        jwt.secret.key=yourStrongSecretKeyHereKeepItLongAndSecure
        jwt.access.token.expiration=3600000 # 1 hour in milliseconds
        jwt.refresh.token.expiration=604800000 # 7 days in milliseconds
        ```
3.  **MinIO (Optional)**:
    *   If using the certificate upload feature, configure MinIO settings in `application.properties`:
        ```properties
        minio.url=http://localhost:9000
        minio.access.key=your_minio_access_key
        minio.secret.key=your_minio_secret_key
        minio.bucket.name=certificates
        ```

### Build

Navigate to the project root directory in your terminal and run:

```bash
./mvnw clean install
# or on Windows:
# mvnw.cmd clean install
```

### Run

After a successful build, you can run the application using:

```bash
./mvnw spring-boot:run
# or on Windows:
# mvnw.cmd spring-boot:run
```

The application will typically start on port `8080` (or as configured in `application.properties`).

## API Documentation (Swagger UI)

Once the application is running, API documentation is available via Swagger UI.
Navigate to:

`http://localhost:8080/swagger-ui/index.html`

This interface allows you to explore all available API endpoints, view their request/response structures, and test them directly from your browser.

## Available Endpoints

The API provides endpoints for managing:

*   Users (Authentication, Profile)
*   Doctors (Registration, Profile)
*   Patients (Registration, Profile)
*   Addresses
*   Appointments
*   Branches (Doctor's clinics)
*   Certificates (Doctor's qualifications)
*   Chats and Messages
*   Diseases/Services offered by doctors at branches
*   Specializations

Refer to the Swagger UI for detailed endpoint paths, request parameters, and response formats.

## Further Development

*   Implement comprehensive unit and integration tests.
*   Enhance error handling and provide more specific error responses.
*   Expand on existing features and add new functionalities as required.
```
