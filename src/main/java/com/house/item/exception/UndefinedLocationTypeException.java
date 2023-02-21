package com.house.item.exception;

public class UndefinedLocationTypeException extends RuntimeException {
    public UndefinedLocationTypeException() {
    }

    public UndefinedLocationTypeException(String message) {
        super(message);
    }

    public UndefinedLocationTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UndefinedLocationTypeException(Throwable cause) {
        super(cause);
    }

    public UndefinedLocationTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
