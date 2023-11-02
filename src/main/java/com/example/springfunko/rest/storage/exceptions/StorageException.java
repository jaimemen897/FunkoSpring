package com.example.springfunko.rest.storage.exceptions;

import java.io.Serial;

public abstract class StorageException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 43876691117560211L;

    protected StorageException(String message) {
        super(message);
    }
}
