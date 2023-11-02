package com.example.springfunko.category.repositories;

import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.category.repositories.CategoryRepository;
import com.example.springfunko.rest.funkos.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {
    private final Categoria categoria = Categoria.builder()
            .id(1L)
            .name("Marvel")
            .build();
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.merge(categoria);
        entityManager.flush();
    }

    @Test
    void findAllByName() {
        Optional<List<Categoria>> categorias = categoryRepository.findAllByNameContainingIgnoreCase("Marvel");
        assertAll(
                () -> assertTrue(categorias.isPresent()),
                () -> assertEquals(1, categorias.get().size())
        );
    }

    @Test
    void existsFunkoById() {
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Funko 1")
                .precio(100.0)
                .cantidad(10)
                .imagen("ruta")
                .fechaCreacion(LocalDate.now())
                .fechaActualizacion(LocalDate.now())
                .categoria(categoria)
                .build();
        entityManager.merge(funko);
        entityManager.flush();
        Boolean exists = categoryRepository.existsFunkoById(1L);
        assertAll(
                () -> assertNotNull(exists),
                () -> assertTrue(exists)
        );
    }


    @Test
    void getIdByName() {
        Optional<Long> id = categoryRepository.getIdByName("Marvel");
        assertAll(
                () -> assertTrue(id.isPresent()),
                () -> assertEquals(1L, id.get())
        );
    }
}