package com.house.item.exception;

public class NonExistentItemQuantityLogException extends RuntimeException {
    public NonExistentItemQuantityLogException() {
    }

    public NonExistentItemQuantityLogException(String message) {
        super(message);
    }

    public NonExistentItemQuantityLogException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentItemQuantityLogException(Throwable cause) {
        super(cause);
    }

    public NonExistentItemQuantityLogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
