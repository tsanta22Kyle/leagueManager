package com.fifa_app.league_manager.service.exceptions;

public class ClientException extends Exception {
    public ClientException(String message) {
        super(message);
    }
    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
