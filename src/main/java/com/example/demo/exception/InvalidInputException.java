package com.example.demo.exception;

import java.util.Arrays;

public class InvalidInputException extends RuntimeException {

    private final String errorKey;
    private final Object[] args;

    public InvalidInputException() {
        super("error.bad.request"); // Default message for logging
        this.errorKey = "error.bad.request"; // Default key, maps to a generic bad request message
        this.args = new Object[0];
    }

    /**
     * Constructs an InvalidInputException with a specific error key.
     * @param errorKey The key for the error message (e.g., "error.validation.field.required").
     */
    public InvalidInputException(String errorKey) {
        super(errorKey); // Message for logging if not handled by GlobalExceptionHandler
        this.errorKey = errorKey;
        this.args = new Object[0];
    }

    /**
     * Constructs an InvalidInputException with a specific error key and arguments for message formatting.
     * @param errorKey The key for the error message (e.g., "error.validation.field.length").
     * @param args Arguments to be inserted into the localized message.
     */
    public InvalidInputException(String errorKey, Object... args) {
        super(errorKey + (args != null && args.length > 0 ? " with args " + Arrays.toString(args) : ""));
        this.errorKey = errorKey;
        this.args = args;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public Object[] getArgs() {
        return args;
    }
}