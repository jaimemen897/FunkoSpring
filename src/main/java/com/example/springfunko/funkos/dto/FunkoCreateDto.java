package com.example.springfunko.funkos.dto;

import com.example.springfunko.category.models.Categoria;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record FunkoCreateDto(
        @NotBlank(message = "El nombre no puede estar vacio")
        String nombre,
        @Min(value = 0, message = "El precio no puede ser negativo")
        Double precio,
        @Min(value = 0, message = "la cantidad no puede ser negativo")
        Integer cantidad,
        @NotEmpty
        String imagen,
        @NotEmpty
        Categoria categoria
) {
}
