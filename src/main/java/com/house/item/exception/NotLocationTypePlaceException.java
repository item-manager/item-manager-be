package com.house.item.exception;

public class NotLocationTypePlaceException extends RuntimeException {
    public NotLocationTypePlaceException() {
    }

    public NotLocationTypePlaceException(String message) {
        super(message);
    }

    public NotLocationTypePlaceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotLocationTypePlaceException(Throwable cause) {
        super(cause);
    }

    public NotLocationTypePlaceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
