package com.example.springfunko.rest.funkos.dto;

import jakarta.validation.constraints.Min;

public record FunkoUpdateDto(
        String nombre,
        @Min(value = 0, message = "El precio no puede ser negativo")
        Double precio,
        @Min(value = 0, message = "la cantidad no puede ser negativo")
        Integer cantidad,
        String imagen,
        String categoria
) {
}
