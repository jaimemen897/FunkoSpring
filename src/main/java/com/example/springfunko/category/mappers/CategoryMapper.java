package com.example.springfunko.category.mappers;

import com.example.springfunko.category.dto.CategoryResponseDto;
import com.example.springfunko.category.models.Categoria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CategoryMapper {
    public Categoria toCategory(CategoryResponseDto categoria) {
        return new Categoria(
                null,
                categoria.nombre(),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    public Categoria toCategory(CategoryResponseDto dto, Categoria categoria) {
        return new Categoria(
                categoria.getId(),
                dto.nombre() != null ? dto.nombre() : categoria.getName(),
                categoria.getCreatedAt(),
                LocalDateTime.now()
        );
    }
}
