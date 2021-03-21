package com.webSocket.simpleChat.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class Notification {
    @NotNull
    @Column(columnDefinition = "boolean not null default false")
    private boolean emailOffline;

    public Notification() {

    }

    public boolean isEmailOffline() {
        return emailOffline;
    }

    public void setEmailOffline(boolean emailOffline) {
        this.emailOffline = emailOffline;
    }
}
