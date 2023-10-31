package com.example.springfunko.category.services;

import com.example.springfunko.category.dto.CategoryResponseDto;
import com.example.springfunko.category.models.Categoria;

import java.util.List;

public interface CategoryService {
    List<Categoria> findAll();

    List<Categoria> findByName(String name);

    Categoria findById(Long id);

    Categoria save(CategoryResponseDto categoria);

    Categoria update(CategoryResponseDto categoria, Long id);

    void deleteById(Long id);
}
