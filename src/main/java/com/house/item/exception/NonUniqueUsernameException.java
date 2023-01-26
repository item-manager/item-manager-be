package com.house.item.exception;

public class NonUniqueUsernameException extends RuntimeException {
    public NonUniqueUsernameException() {
    }

    public NonUniqueUsernameException(String message) {
        super(message);
    }

    public NonUniqueUsernameException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonUniqueUsernameException(Throwable cause) {
        super(cause);
    }

    public NonUniqueUsernameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
