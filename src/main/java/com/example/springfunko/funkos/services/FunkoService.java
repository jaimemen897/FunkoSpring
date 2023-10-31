package com.example.springfunko.funkos.services;

import com.example.springfunko.funkos.dto.FunkoCreateDto;
import com.example.springfunko.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.funkos.models.Funko;

import java.util.List;

public interface FunkoService {
    List<Funko> findAll(String nombre, String categoria);

    Funko findById(long id);

    Funko save(FunkoCreateDto funko);

    Funko update(FunkoUpdateDto funko, Long id);

    void deleteById(long id);
}
