package com.example.springfunko.repositories;

import com.example.springfunko.category.models.Categoria;
import com.example.springfunko.funkos.models.Funko;
import com.example.springfunko.funkos.repositories.FunkoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class FunkoRepositoryImplTest {
    private final Categoria categoria1 = Categoria.builder().id(null).name("Cat1").build();
    private final Categoria categoria2 = Categoria.builder().id(null).name("Cat2").build();

    private final Funko funko1 = Funko.builder()
            .nombre("nombre4")
            .precio(70.89)
            .cantidad(3)
            .imagen("rutaImagen4")
            .categoria(categoria1)
            .build();

    private final Funko funko2 = Funko.builder()
            .nombre("nombre5")
            .precio(54.52)
            .cantidad(1)
            .imagen("rutaImagen5")
            .categoria(categoria2)
            .build();

    @Autowired
    private FunkoRepository funkoRepository;
    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        funkoRepository.deleteAll();

        entityManager.merge(categoria1);
        entityManager.merge(categoria2);
        entityManager.flush();
        entityManager.merge(funko1);
        entityManager.merge(funko2);
        entityManager.flush();
    }

    @Test
    void getAllByNombre() {
        List<Funko> funkos = funkoRepository.findAllByNombre("nombre4");
        assertAll(
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funko1.getId(), funkos.get(0).getId()),
                () -> assertEquals("nombre4", funkos.get(0).getNombre())
        );
    }

    @Test
    void getAllByCategoria() {
        List<Funko> funkos = funkoRepository.findAllByCategoriaName("Cat1");
        assertAll(
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funko1.getId(), funkos.get(0).getId()),
                () -> assertEquals("Cat1", funkos.get(0).getCategoria().getName())
        );
    }

    @Test
    void getAllByNombreAndCategoria() {
        List<Funko> funkos = funkoRepository.findAllByNombreAndCategoriaName("nombre4", "Cat1");
        assertAll(
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funko1.getId(), funkos.get(0).getId()),
                () -> assertEquals("nombre4", funkos.get(0).getNombre()),
                () -> assertEquals("Cat1", funkos.get(0).getCategoria().getName())
        );
    }


}