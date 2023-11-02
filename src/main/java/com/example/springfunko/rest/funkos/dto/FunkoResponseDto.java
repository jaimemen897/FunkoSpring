package com.example.springfunko.rest.funkos.dto;

import com.example.springfunko.rest.category.models.Categoria;

import java.time.LocalDate;

public record FunkoResponseDto(
        Long id,
        String nombre,
        Double precio,
        Integer cantidad,
        String imagen,
        Categoria categoria,
        LocalDate fechaCreacion,
        LocalDate fechaActualizacion
) {
}
