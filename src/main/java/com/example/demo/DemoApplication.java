// TODO: Consider implementing a global exception handler (@ControllerAdvice)
// to standardize API error responses (e.g., consistent JSON error structure).
// This would catch exceptions like ResourceNotFoundException, DuplicateResourceException, etc.,
// and map them to appropriate HTTP responses.

// TODO: For user-facing error messages (like those in custom exceptions),
// consider implementing internationalization (I18N) using Spring's MessageSource
// to support multiple languages and keep messages externalized from code.
package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
