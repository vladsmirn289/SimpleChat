package com.webSocket.simpleChat.jackson;

public class StringDTO {
    private String message;

    protected StringDTO() {

    }

    public StringDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
