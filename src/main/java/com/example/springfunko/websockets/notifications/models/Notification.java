package com.example.springfunko.websockets.notifications.models;

public record Notification<T>(
        String entity,
        Tipo type,
        T data,
        String createdAt
) {

    public enum Tipo {CREATE, UPDATE, DELETE}


}
