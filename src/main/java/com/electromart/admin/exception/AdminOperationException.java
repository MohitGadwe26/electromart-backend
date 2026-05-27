package com.electromart.admin.exception;

public class AdminOperationException extends RuntimeException {
    public AdminOperationException(String message) {
        super(message);
    }
}