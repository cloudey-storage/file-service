package com.ilyanin.file_service.exception;

public class EventSerializationException extends RuntimeException{

    public EventSerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventSerializationException(String message) {
        super(message);
    }

}
