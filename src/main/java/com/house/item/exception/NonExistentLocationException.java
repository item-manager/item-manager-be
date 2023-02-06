package com.house.item.exception;

public class NonExistentLocationException extends RuntimeException {
    public NonExistentLocationException() {
    }

    public NonExistentLocationException(String message) {
        super(message);
    }

    public NonExistentLocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentLocationException(Throwable cause) {
        super(cause);
    }

    public NonExistentLocationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
