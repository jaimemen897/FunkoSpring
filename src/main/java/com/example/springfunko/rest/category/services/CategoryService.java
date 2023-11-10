package com.example.springfunko.rest.category.services;

import com.example.springfunko.rest.category.dto.CategoryResponseDto;
import com.example.springfunko.rest.category.models.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CategoryService {
    Page<Categoria> findAll(Optional<String> name, Optional<Boolean> isDeleted, Pageable pageable);

    Categoria findById(Long id);

    Categoria save(CategoryResponseDto categoria);

    Categoria update(CategoryResponseDto categoria, Long id);

    void deleteById(Long id);
}
