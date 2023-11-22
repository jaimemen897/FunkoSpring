package com.example.springfunko.rest.orders.models;

import org.hibernate.validator.constraints.Length;

public record Direction(
        @Length(min = 3, message = "The street must be at least 3 characters long")
        String street,
        @Length(min = 1, message = "The number must be at least 1 character long")
        String number,
        @Length(min = 3, message = "The city must be at least 3 characters long")
        String city,
        @Length(min = 3, message = "The state must be at least 3 characters long")
        String state,
        @Length(min = 3, message = "The country must be at least 3 characters long")
        String country,
        @Length(min = 3, message = "The zipCode must be at least 3 characters long")
        String zipCode
) {
}
