package com.example.demo.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;
import java.util.Locale;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final MessageSource messageSource;

    @Autowired
    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getLocalizedMessage(String key, Object[] args, String defaultMessage) {
        Locale locale = LocaleContextHolder.getLocale(); // Get locale resolved by LocaleResolver
        return messageSource.getMessage(key, args, defaultMessage, locale);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {

        String message = getLocalizedMessage(ex.getErrorKey(), ex.getArgs(), "Resource not found.");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                message,
                request.getRequestURI());
        logger.error("ResourceNotFoundException: Key: {}, Args: {}, Translated: {}, Path: {}",
                ex.getErrorKey(), Arrays.toString(ex.getArgs()), message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInputException(
            InvalidInputException ex, HttpServletRequest request) {

        String message = getLocalizedMessage(ex.getErrorKey(), ex.getArgs(), "Invalid input provided.");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getRequestURI());
        logger.error("InvalidInputException: Key: {}, Args: {}, Translated: {}, Path: {}",
                ex.getErrorKey(), Arrays.toString(ex.getArgs()), message, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(IllegalArgumentException.class) // Handles generic IllegalArgumentExceptions
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {

        // For generic IllegalArgumentException, we might not have a specific key from the exception itself.
        // We'll use a general "error.bad.request" key.
        // The original exception message (ex.getMessage()) can be logged for details but might not be suitable for user display.
        String message = getLocalizedMessage("error.bad.request", null, "Bad request.");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                message,
                request.getRequestURI());
        // Log original message for backend debugging
        logger.error("IllegalArgumentException: Original Message: {}, Translated: {}, Path: {}", ex.getMessage(), message, request.getRequestURI(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class) // Catch-all for other unhandled exceptions
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, HttpServletRequest request) {

        String message = getLocalizedMessage("error.internal.server", null, "An unexpected internal server error occurred.");

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                message,
                request.getRequestURI());
        logger.error("Unhandled Exception: {}, Path: {}", ex.getMessage(), request.getRequestURI(), ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
