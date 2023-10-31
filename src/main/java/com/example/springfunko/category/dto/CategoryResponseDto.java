package com.example.springfunko.category.dto;

import org.hibernate.validator.constraints.Length;

public record CategoryResponseDto(
        @Length(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
        String nombre,
        Boolean isDeleted
) {
}
