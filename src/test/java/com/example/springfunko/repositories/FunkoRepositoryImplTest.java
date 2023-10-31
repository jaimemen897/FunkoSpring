package com.example.springfunko.repositories;

import com.example.springfunko.funkos.models.Funko;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FunkoRepositoryImplTest {
    FunkoRepositoryImpl funkoRepositoryImpl = new FunkoRepositoryImpl();

    private final Funko funko1 = Funko.builder()
            .id(1L)
            .nombre("nombre4")
            .precio(70.89)
            .cantidad(3)
            .imagen("rutaImagen4")
            .categoria("dc")
            .build();

    private final Funko funko2 = Funko.builder()
            .id(2L)
            .nombre("nombre5")
            .precio(54.52)
            .cantidad(1)
            .imagen("rutaImagen5")
            .categoria("disney")
            .build();

    @BeforeEach
    void setUp() {
        funkoRepositoryImpl.funkos.clear();
        funkoRepositoryImpl.funkos.put(1L, funko1);
        funkoRepositoryImpl.funkos.put(2L, funko2);
    }

    @Test
    void getAll() {
        List<Funko> funkos = funkoRepositoryImpl.getAll();
        assertAll(
                () -> assertEquals(2, funkos.size()),
                () -> assertEquals(funko1, funkos.get(0)),
                () -> assertEquals(funko2, funkos.get(1))
        );
    }

    @Test
    void getAllByNombre() {
        List<Funko> funkos = funkoRepositoryImpl.getAllByNombre("nombre4");
        assertAll(
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funko1, funkos.get(0)),
                () -> assertEquals("nombre4", funkos.get(0).getNombre())
        );
    }

    @Test
    void getAllByCategoria() {
        List<Funko> funkos = funkoRepositoryImpl.getAllByCategoria("dc");
        assertAll(
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funko1, funkos.get(0)),
                () -> assertEquals("dc", funkos.get(0).getCategoria())
        );
    }

    @Test
    void getAllByNombreAndCategoria() {
        List<Funko> funkos = funkoRepositoryImpl.getAllByNombreAndCategoria("nombre4", "dc");
        assertAll(
                () -> assertEquals(1, funkos.size()),
                () -> assertEquals(funko1, funkos.get(0)),
                () -> assertEquals("nombre4", funkos.get(0).getNombre()),
                () -> assertEquals("dc", funkos.get(0).getCategoria())
        );
    }

    @Test
    void getById() {
        Funko funko = funkoRepositoryImpl.getById(1L).orElse(null);
        assertAll(
                () -> assertNotNull(funko),
                () -> assertEquals(funko1, funko)
        );
    }

    @Test
    void post() {
        Funko funko = Funko.builder()
                .id(3L)
                .nombre("Paco")
                .precio(12.00)
                .cantidad(1)
                .imagen("imagen")
                .categoria("marvel")
                .build();
        funkoRepositoryImpl.post(funko);
        assertAll(
                () -> assertEquals(3, funkoRepositoryImpl.funkos.size()),
                () -> assertEquals(funko, funkoRepositoryImpl.funkos.get(3L))
        );
    }

    @Test
    void put() {
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("Paco")
                .precio(12.00)
                .cantidad(1)
                .imagen("imagen")
                .categoria("marvel")
                .build();
        funkoRepositoryImpl.put(funko);
        assertAll(
                () -> assertEquals(2, funkoRepositoryImpl.funkos.size()),
                () -> assertEquals(funko, funkoRepositoryImpl.funkos.get(1L))
        );
    }

    @Test
    void deleteById() {
        funkoRepositoryImpl.deleteById(1L);
        assertAll(
                () -> assertEquals(1, funkoRepositoryImpl.funkos.size()),
                () -> assertNull(funkoRepositoryImpl.funkos.get(1L))
        );
    }
}