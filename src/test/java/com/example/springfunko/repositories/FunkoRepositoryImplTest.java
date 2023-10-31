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
    private final Categoria categoria1 = Categoria.builder().id(null).name("Disney").build();
    private final Categoria categoria2 = Categoria.builder().id(null).name("Serie").build();

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
    void getAll() {
        List<Funko> funkos = funkoRepository.findAll();
        assertAll(
                () -> assertEquals(2, funkos.size()),
                () -> assertFalse(funkos.isEmpty()),
                () -> assertTrue(funkos.size() >= 2)
        );
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
        List<Funko> funkos = funkoRepository.findAllByCategoriaName("Disney");
        assertAll(
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funko1.getId(), funkos.get(0).getId()),
                () -> assertEquals("Disney", funkos.get(0).getCategoria().getName())
        );
    }

    @Test
    void getAllByNombreAndCategoria() {
        List<Funko> funkos = funkoRepository.findAllByNombreAndCategoriaName("nombre4", "Disney");
        assertAll(
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funko1.getId(), funkos.get(0).getId()),
                () -> assertEquals("nombre4", funkos.get(0).getNombre()),
                () -> assertEquals("Disney", funkos.get(0).getCategoria().getName())
        );
    }

    @Test
    void getById() {
        Long id = funkoRepository.findTopByOrderByIdDesc();
        Optional<Funko> funko = funkoRepository.findById(id);
        assertAll(
                () -> assertNotNull(funko),
                () -> assertTrue(funko.isPresent()),
                () -> assertEquals(id, funko.get().getId())
        );
    }

    @Test
    void post() {
        Funko funko = Funko.builder()
                .nombre("Paco")
                .precio(12.00)
                .cantidad(1)
                .imagen("imagen")
                .categoria(categoria1)
                .build();
        funkoRepository.save(funko);
        Long id = funkoRepository.findTopByOrderByIdDesc();
        assertAll(
                () -> assertEquals(3, funkoRepository.findAll().size()),
                () -> assertEquals(funko.getId(), funkoRepository.findById(id).get().getId())
        );
    }

    @Test
    void put() {
        Funko funko = Funko.builder()
                .nombre("Paco")
                .precio(12.00)
                .cantidad(1)
                .imagen("imagen")
                .categoria(categoria1)
                .build();
        Funko savedFunko = funkoRepository.save(funko);
        var all = funkoRepository.findAll();

        assertAll(
                () -> assertTrue(all.size() >= 2),
                () -> assertEquals(funko, savedFunko),
                () -> assertTrue(funkoRepository.findById(savedFunko.getId()).isPresent()),
                () -> assertEquals(funko.getId(), savedFunko.getId()),
                () -> assertEquals(funko.getNombre(), savedFunko.getNombre()),
                () -> assertEquals(funko.getPrecio(), savedFunko.getPrecio()),
                () -> assertEquals(funko.getCantidad(), savedFunko.getCantidad()),
                () -> assertEquals(funko.getImagen(), savedFunko.getImagen()),
                () -> assertEquals(funko.getCategoria(), savedFunko.getCategoria())
        );
    }

    @Test
    void deleteById() {
        Long id = funkoRepository.findTopByOrderByIdDesc() - 1;
        funkoRepository.deleteById(id);
        assertAll(
                () -> assertEquals(1, funkoRepository.findAll().size()),
                () -> assertNull(funkoRepository.findById(id).orElse(null))
        );
    }
}