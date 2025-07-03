package com.example.demo.exception;

import com.example.demo.i18n_config.TranslationService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final TranslationService translationService;

    @Autowired
    public GlobalExceptionHandler(TranslationService translationService) {
        this.translationService = translationService;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        String message = translationService.getTranslatedMessage(ex.getErrorKey(), request);
        // If args are present, format the message (simple replacement for now)
        // For more complex formatting, a proper message formatter should be used.
        if (ex.getArgs() != null && ex.getArgs().length > 0) {
            // This is a simplistic formatter. For real apps, use MessageFormat.
            for (int i = 0; i < ex.getArgs().length; i++) {
                message = message.replace("{" + i + "}", String.valueOf(ex.getArgs()[i]));
            }
        }

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                message,
                request.getRequestURI());
        logger.error("ResourceNotFoundException: Key: {}, Translated: {}, Path: {}", ex.getErrorKey(), message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class) // Example for Bad Request
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        // You might want a specific key for IllegalArgumentException, e.g., "error_illegal_argument"
        // For now, using "error_bad_request" as a general bad request message.
        String message = translationService.getTranslatedMessage("error_bad_request", request);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage() != null ? message + ": " + ex.getMessage() : message, // Append original message if available
                request.getRequestURI());
        logger.error("IllegalArgumentException: Translated: {}, Original: {}, Path: {}", message, ex.getMessage(), request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // A general handler for other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        String message = translationService.getTranslatedMessage("error_internal_server", request);
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message, // Avoid exposing internal exception details to the client directly
                request.getRequestURI());
        logger.error("Unhandled Exception: {}, Path: {}", ex.getMessage(), request.getRequestURI(), ex); // Log the full exception for debugging
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
