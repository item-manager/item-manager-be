package com.house.item.exception;

public class NonUniqueLabelNameException extends RuntimeException {
    public NonUniqueLabelNameException() {
    }

    public NonUniqueLabelNameException(String message) {
        super(message);
    }

    public NonUniqueLabelNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonUniqueLabelNameException(Throwable cause) {
        super(cause);
    }

    public NonUniqueLabelNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
