package com.webSocket.simpleChat.model;

import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class Notification {
    @NotNull
    @ColumnDefault("false")
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
