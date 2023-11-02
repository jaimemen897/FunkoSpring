package com.example.springfunko.category.mappers;

import com.example.springfunko.rest.category.dto.CategoryResponseDto;
import com.example.springfunko.rest.category.mappers.CategoryMapper;
import com.example.springfunko.rest.category.models.Categoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryMapperTest {
    private final CategoryMapper categoryMapper = new CategoryMapper();

    @Test
    void toCategory() {
        Long id = 1L;
        CategoryResponseDto categoryCreateDto = new CategoryResponseDto("Disney", false);

        var res = categoryMapper.toCategory(categoryCreateDto);

        assertAll(
                () -> assertEquals(categoryCreateDto.nombre(), res.getName()),
                () -> assertEquals(categoryCreateDto.isDeleted(), res.getIsDeleted())
        );
    }

    @Test
    void testToCategory() {
        Long id = 1L;
        CategoryResponseDto categoryCreateDto = new CategoryResponseDto("Disney", false);

        Categoria categoria = Categoria.builder()
                .id(id)
                .name("Disney")
                .build();

        var res = categoryMapper.toCategory(categoryCreateDto, categoria);

        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(categoryCreateDto.nombre(), res.getName()),
                () -> assertEquals(categoryCreateDto.isDeleted(), res.getIsDeleted()));
    }
}