package com.example.aifaceauthentication.exception;

public class FaceLoginException extends RuntimeException {
    public FaceLoginException(String message) {
        super(message);
    }

    public FaceLoginException(String message, Throwable cause) {
        super(message, cause);
    }
}
