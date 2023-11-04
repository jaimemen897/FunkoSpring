package com.example.springfunko.rest.funkos.services;

import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoResponseDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.models.Funko;

import java.util.List;

public interface FunkoService {
    List<Funko> findAll(String nombre, String categoria);

    Funko findById(long id);

    FunkoResponseDto save(FunkoCreateDto funko);

    FunkoResponseDto update(FunkoUpdateDto funko, Long id);

    void deleteById(long id);
}
