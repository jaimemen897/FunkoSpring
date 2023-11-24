package com.example.springfunko.rest.funkos.dto;

import com.example.springfunko.rest.category.models.Categoria;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "FunkoResponseDto", description = "Dto para obtener un funko")
public record FunkoResponseDto(
        @Schema(description = "Id del funko", example = "1")
        Long id,
        @Schema(description = "Nombre del funko", example = "Funko 1")
        String nombre,
        @Schema(description = "Precio del funko", example = "10.5")
        Double precio,
        @Schema(description = "Cantidad del funko", example = "10")
        Integer cantidad,
        @Schema(description = "Imagen del funko", example = "https://www.google.com")
        String imagen,
        @Schema(description = "Categoria del funko", example = "Categoria 1")
        Categoria categoria,
        @Schema(description = "Fecha de creacion del funko", example = "2021-01-01")
        LocalDate fechaCreacion,
        @Schema(description = "Fecha de actualizacion del funko", example = "2021-01-01")
        LocalDate fechaActualizacion
) {
}
