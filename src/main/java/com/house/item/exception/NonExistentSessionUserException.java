package com.house.item.exception;

public class NonExistentSessionUserException extends RuntimeException {
    public NonExistentSessionUserException() {
    }

    public NonExistentSessionUserException(String message) {
        super(message);
    }

    public NonExistentSessionUserException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentSessionUserException(Throwable cause) {
        super(cause);
    }

    public NonExistentSessionUserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
