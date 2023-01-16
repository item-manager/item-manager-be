package com.house.item.exception;

public class NonUniqueUserIdException extends RuntimeException {
    public NonUniqueUserIdException() {
        super();
    }

    public NonUniqueUserIdException(String message) {
        super(message);
    }

    public NonUniqueUserIdException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonUniqueUserIdException(Throwable cause) {
        super(cause);
    }

    protected NonUniqueUserIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
