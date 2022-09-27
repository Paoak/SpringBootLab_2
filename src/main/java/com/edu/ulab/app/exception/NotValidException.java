package com.edu.ulab.app.exception;

public class NotValidException extends RuntimeException {
    public NotValidException(String message) {
        super(message);
    }
}