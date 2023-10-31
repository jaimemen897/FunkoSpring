package com.example.springfunko.category.mappers;

import com.example.springfunko.category.dto.CategoryResponseDto;
import com.example.springfunko.category.models.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public Categoria toCategory(CategoryResponseDto categoria) {
        return new Categoria(
                null,
                categoria.nombre()
        );
    }

    public CategoryResponseDto toCategoryDto(Categoria categoria) {
        return new CategoryResponseDto(
                categoria.getName()
        );
    }
}
