package com.floti.api.error.exception;

public class InvalidPostContentException extends RuntimeException {
    public InvalidPostContentException(String message) {
        super(message);
    }
}
