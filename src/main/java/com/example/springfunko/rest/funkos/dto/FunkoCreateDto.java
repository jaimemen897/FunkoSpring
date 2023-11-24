package com.example.springfunko.rest.funkos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Schema(name = "FunkoCreateDto", description = "Dto para crear un funko")
@Builder
public record FunkoCreateDto(
        @Schema(description = "Nombre del funko", example = "Funko 1")
        @NotBlank(message = "El nombre no puede estar vacío")
        @Length(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String nombre,
        @Schema(description = "Precio del funko", example = "10.5")
        @Min(value = 0, message = "El precio no puede ser negativo")
        Double precio,
        @Schema(description = "Cantidad del funko", example = "10")
        @Min(value = 0, message = "la cantidad no puede ser negativo")
        Integer cantidad,
        @Schema(description = "Imagen del funko", example = "https://www.google.com")
        @NotEmpty(message = "La imagen no puede estar vacia")
        String imagen,
        @Schema(description = "Categoria del funko", example = "Categoria 1")
        @NotEmpty(message = "La categoria no puede estar vacia")
        String categoria
) {
}
