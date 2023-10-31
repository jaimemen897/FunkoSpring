package com.example.springfunko.funkos.dto;

import com.example.springfunko.category.models.Categoria;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record FunkoCreateDto(
        @NotBlank(message = "El nombre no puede estar vacio")
        @Length(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String nombre,
        @Min(value = 0, message = "El precio no puede ser negativo")
        Double precio,
        @Min(value = 0, message = "la cantidad no puede ser negativo")
        Integer cantidad,
        @NotEmpty
        String imagen,
        @NotNull
        Categoria categoria
) {
}
