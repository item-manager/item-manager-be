package com.house.item.exception;

public class SubtractCountExceedItemQuantityException extends RuntimeException {
    public SubtractCountExceedItemQuantityException() {
    }

    public SubtractCountExceedItemQuantityException(String message) {
        super(message);
    }

    public SubtractCountExceedItemQuantityException(String message, Throwable cause) {
        super(message, cause);
    }

    public SubtractCountExceedItemQuantityException(Throwable cause) {
        super(cause);
    }

    public SubtractCountExceedItemQuantityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
