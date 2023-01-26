package com.house.item.exception;

public class NonExistentItemException extends RuntimeException {
    public NonExistentItemException() {
    }

    public NonExistentItemException(String message) {
        super(message);
    }

    public NonExistentItemException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentItemException(Throwable cause) {
        super(cause);
    }

    public NonExistentItemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
