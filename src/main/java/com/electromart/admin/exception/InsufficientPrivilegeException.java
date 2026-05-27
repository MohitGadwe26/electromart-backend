package com.electromart.admin.exception;

public class InsufficientPrivilegeException extends RuntimeException {
    public InsufficientPrivilegeException(String message) {
        super(message);
    }
}