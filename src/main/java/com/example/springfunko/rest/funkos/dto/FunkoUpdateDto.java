package com.example.springfunko.rest.funkos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;

@Schema(name = "FunkoUpdateDto", description = "Dto para actualizar un funko")
public record FunkoUpdateDto(
        @Schema(description = "Nombre del funko", example = "Funko 1")
        String nombre,
        @Min(value = 0, message = "El precio no puede ser negativo")
        @Schema(description = "Precio del funko", example = "10.5")
        Double precio,
        @Schema(description = "Cantidad del funko", example = "10")
        @Min(value = 0, message = "la cantidad no puede ser negativo")
        Integer cantidad,
        @Schema(description = "Imagen del funko", example = "https://www.google.com")
        String imagen,
        @Schema(description = "Categoria del funko", example = "Categoria 1")
        String categoria
) {
}
