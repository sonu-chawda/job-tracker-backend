package com.jobtracker.exception;

public class AiException extends RuntimeException {
    private final int status;
    public AiException(String message, int status) { super(message); this.status = status; }
    public int getStatus() { return status; }
}
