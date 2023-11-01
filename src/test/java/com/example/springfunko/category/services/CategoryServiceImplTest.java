package com.example.springfunko.category.services;

import com.example.springfunko.category.dto.CategoryResponseDto;
import com.example.springfunko.category.exception.CategoryConflict;
import com.example.springfunko.category.exception.CategoryNotFound;
import com.example.springfunko.category.mappers.CategoryMapper;
import com.example.springfunko.category.models.Categoria;
import com.example.springfunko.category.repositories.CategoryRepository;
import com.example.springfunko.funkos.dto.FunkoCreateDto;
import com.example.springfunko.funkos.dto.FunkoResponseDto;
import com.example.springfunko.funkos.models.Funko;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    private final Categoria categoria = Categoria.builder().name("Disney").build();
    private final Categoria categoria2 = Categoria.builder().name("DC").build();

    private final CategoryResponseDto categoryResponseDto1 = new CategoryResponseDto("Disney", false);
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Captor
    private ArgumentCaptor<Categoria> categoryArgumentCaptor;


    @Test
    void findAll() {
        List<Categoria> expectedCategories = List.of(categoria, categoria2);

        when(categoryRepository.findAll()).thenReturn(expectedCategories);
        List<Categoria> actualCategories = categoryService.findAll(null);
        assertAll(
                () -> assertEquals(expectedCategories, actualCategories),
                () -> assertEquals(expectedCategories.size(), actualCategories.size())
        );
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void findAllByName() {
        List<Categoria> expectedCategories = List.of(categoria);
        when(categoryRepository.findAllByNameContainingIgnoreCase("Disney")).thenReturn(Optional.of(expectedCategories));
        List<Categoria> actualCategories = categoryService.findAll("Disney");
        assertAll(
                () -> assertEquals(expectedCategories, actualCategories),
                () -> assertEquals(expectedCategories.size(), actualCategories.size())
        );
        verify(categoryRepository, times(1)).findAllByNameContainingIgnoreCase("Disney");
    }


    @Test
    void findById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoria));
        Categoria actualCategory = categoryService.findById(1L);
        assertAll(
                () -> assertEquals(categoria, actualCategory),
                () -> assertEquals(categoria.getName(), actualCategory.getName())
        );
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void findByIdNotFound() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());
        var res = assertThrows(CategoryNotFound.class, () -> categoryService.findById(1L));
        assertEquals("Categoria no encontrada", res.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
    }

    @Test
    void save() {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto("Disney", false);
        Categoria expectedCategory = Categoria.builder().name("Disney").build();

        when(categoryRepository.save(any(Categoria.class))).thenReturn(expectedCategory);

        Categoria actualCategory = categoryService.save(categoryResponseDto);

        assertEquals(expectedCategory, actualCategory);

        verify(categoryRepository, times(1)).save(categoryArgumentCaptor.capture());
    }

    @Test
    void saveAlreadyExist(){
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto("Disney", false);

        when(categoryRepository.getIdByName("Disney")).thenReturn(Optional.of(1L));

        var res = assertThrows(CategoryConflict.class, () -> categoryService.save(categoryResponseDto));
        assertEquals("Categoria ya existe", res.getMessage());

        verify(categoryRepository, times(1)).getIdByName("Disney");
    }

    @Test
    void update() {
        Categoria expectedCategory = Categoria.builder().name("Disney").build();
        Categoria categoryToUpdate = Categoria.builder().name("Disney").build();
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto("Disney", false);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoryToUpdate));
        when(categoryRepository.save(any(Categoria.class))).thenReturn(expectedCategory);

        Categoria actualCategory = categoryService.update(categoryResponseDto, 1L);

        assertEquals(expectedCategory, actualCategory);

        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(categoryArgumentCaptor.capture());
    }

    @Test
    void deleteById() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoria));
        categoryService.deleteById(1L);
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteByIdContainsFunkos(){
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(categoryRepository.existsFunkoById(1L)).thenReturn(true);
        var res = assertThrows(CategoryConflict.class, () -> categoryService.deleteById(1L));
        assertEquals("Categoria no eliminada, tiene funkos asociados", res.getMessage());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).existsFunkoById(1L);
    }
}