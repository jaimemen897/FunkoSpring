package com.example.springfunko.category.repositories;

import com.example.springfunko.category.models.Categoria;
import com.example.springfunko.funkos.models.Funko;
import com.example.springfunko.funkos.repositories.FunkoRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CategoryRepositoryTest {
    private final Categoria categoria = Categoria.builder()
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

    /*@Test
    void existsFunkoById() {
        Funko funko1 = Funko.builder()
                .id(1L)
                .nombre("nombre4")
                .precio(70.89)
                .cantidad(3)
                .categoria(categoria)
                .imagen("rutaImagen4")
                .build();
        entityManager.merge(funko1);
        entityManager.flush();
        assertAll(
                () -> assertTrue(categoryRepository.existsFunkoById(categoria.getId())),
                () -> assertFalse(categoryRepository.existsFunkoById(14L))
        );
    }

    @Test
    void getIdByName() {
        Optional<Long> id = categoryRepository.getIdByName("Marvel");
        assertAll(
                () -> assertTrue(id.isPresent()),
                () -> assertEquals(13L, id.get())
        );
    }*/
}