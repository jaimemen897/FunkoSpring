package com.example.springfunko.websockets.notifications.dto;

import com.example.springfunko.rest.category.models.Categoria;

import java.time.LocalDate;

public record FunkoNotificationResponse(
        Long id,
        String nombre,
        double precio,
        int cantidad,
        String imagen,
        LocalDate fechaCreacion,
        LocalDate fechaActualizacion,
        Categoria categoria
) {
}
