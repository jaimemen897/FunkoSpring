package com.example.springfunko.rest.orders.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record Client(
        @Length(min = 3, message = "The name must be at least 3 characters long")
        String fullName,

        @Email(message = "The email must be valid")
        String email,

        @NotBlank(message = "The phone number cannot be empty")
        String phoneNumber,

        @NotNull(message = "The direction cannot be empty")
        Direction direction
) {

}
