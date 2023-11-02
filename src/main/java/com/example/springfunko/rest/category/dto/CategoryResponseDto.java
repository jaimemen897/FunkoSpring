package com.example.springfunko.rest.category.dto;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record CategoryResponseDto(
        @Length(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        @NotNull(message = "El nombre no puede ser nulo")
        String nombre,
        @NotNull(message = "El estado no puede ser nulo")
        Boolean isDeleted
) {
}
