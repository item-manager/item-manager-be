package com.house.item.exception;

public class NonExistentPlaceException extends RuntimeException {
    public NonExistentPlaceException() {
    }

    public NonExistentPlaceException(String message) {
        super(message);
    }

    public NonExistentPlaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentPlaceException(Throwable cause) {
        super(cause);
    }

    public NonExistentPlaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
