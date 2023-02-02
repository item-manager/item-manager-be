package com.house.item.exception;

public class NonExistentLabelException extends RuntimeException {
    public NonExistentLabelException() {
    }

    public NonExistentLabelException(String message) {
        super(message);
    }

    public NonExistentLabelException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentLabelException(Throwable cause) {
        super(cause);
    }

    public NonExistentLabelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
