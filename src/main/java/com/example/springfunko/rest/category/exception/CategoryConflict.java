package com.example.springfunko.rest.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryConflict extends CategoryException {

    public CategoryConflict(String message) {
        super(message);
    }
}
