package com.example.springfunko.rest.funkos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record FunkoCreateDto(
        @NotBlank(message = "El nombre no puede estar vacío")
        @Length(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String nombre,
        @Min(value = 0, message = "El precio no puede ser negativo")
        Double precio,
        @Min(value = 0, message = "la cantidad no puede ser negativo")
        Integer cantidad,
        @NotEmpty(message = "La imagen no puede estar vacia")
        String imagen,
        @NotEmpty(message = "La categoria no puede estar vacia")
        String categoria
) {
}
