package com.house.item.exception;

public class NonExistentRoomException extends RuntimeException {
    public NonExistentRoomException() {
    }

    public NonExistentRoomException(String message) {
        super(message);
    }

    public NonExistentRoomException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonExistentRoomException(Throwable cause) {
        super(cause);
    }

    public NonExistentRoomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
