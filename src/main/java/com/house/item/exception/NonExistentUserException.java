package com.house.item.exception;

public class NonExistentUserException extends RuntimeException {
    public NonExistentUserException() {
        super();
    }

    public NonExistentUserException(String message) {
        super(message);
    }

    public NonExistentUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentUserException(Throwable cause) {
        super(cause);
    }

    protected NonExistentUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
