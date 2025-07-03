package com.example.demo.exception;

// No specific @ResponseStatus here, as the GlobalExceptionHandler will set it
public class ResourceNotFoundException extends RuntimeException {

    private final String errorKey;
    private final Object[] args; // For formatted messages, e.g., "Resource with ID {0} not found"

    public ResourceNotFoundException() {
        super("error_not_found"); // Default message if no key is provided
        this.errorKey = "error_not_found";
        this.args = new Object[0];
    }

    public ResourceNotFoundException(String errorKey) {
        super(errorKey);
        this.errorKey = errorKey;
        this.args = new Object[0];
    }

    public ResourceNotFoundException(String errorKey, Object... args) {
        super(String.format(errorKey, args)); // The message here is more for logging/debugging if not handled by handler
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