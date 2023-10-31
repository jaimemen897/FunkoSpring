package com.example.springfunko.category.mappers;

import com.example.springfunko.category.dto.CategoryResponseDto;
import com.example.springfunko.category.models.Categoria;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    public CategoryResponseDto toCategoryDto(Categoria categoria) {
           return new CategoryResponseDto(
                    categoria.getId(),
                    categoria.getName()
            );
    }

    public Categoria toCategory(CategoryResponseDto categoria) {
        return new Categoria(
                categoria.id(),
                categoria.nombre()
        );
    }
}
