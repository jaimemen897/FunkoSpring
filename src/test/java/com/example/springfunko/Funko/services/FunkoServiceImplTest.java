package com.example.springfunko.Funko.services;

import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.exception.FunkoNotFound;
import com.example.springfunko.rest.funkos.mapper.FunkoMapper;
import com.example.springfunko.rest.funkos.models.Funko;
import com.example.springfunko.rest.funkos.repositories.FunkoRepository;
import com.example.springfunko.rest.funkos.services.FunkoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FunkoServiceImplTest {
    private final Categoria categoria1 = Categoria.builder().id(null).name("Disney").build();
    private final Categoria categoria2 = Categoria.builder().id(null).name("Serie").build();

    private final Funko funko1 = Funko.builder()
            .id(1L)
            .nombre("nombre4")
            .precio(70.89)
            .cantidad(3)
            .imagen("rutaImagen4")
            .categoria(categoria1)
            .build();

    private final Funko funko2 = Funko.builder()
            .id(2L)
            .nombre("nombre5")
            .precio(54.52)
            .cantidad(1)
            .imagen("rutaImagen5")
            .categoria(categoria2)
            .build();

    @Mock
    private FunkoRepository funkoRepository;
    @Mock
    private FunkoMapper funkoMapper;
    @InjectMocks
    private FunkoServiceImpl funkoServiceImpl;
    @Captor
    private ArgumentCaptor<Funko> funkoArgumentCaptor;

    @Test
    void findAll() {
        List<Funko> expectedFunkos = Arrays.asList(funko1, funko2);
        when(funkoRepository.findAll()).thenReturn(expectedFunkos);
        List<Funko> actualFunkos = funkoServiceImpl.findAll(null, null);
        assertIterableEquals(expectedFunkos, actualFunkos);
        verify(funkoRepository, times(1)).findAll();
    }

    @Test
    void findAllByCategoria(){
        String categoria = "Disney";
        List<Funko> expectedFunkos = List.of(funko1);
        when(funkoRepository.findAllByCategoriaName(categoria)).thenReturn(expectedFunkos);
        List<Funko> actualFunkos = funkoServiceImpl.findAll(null, categoria);
        assertIterableEquals(expectedFunkos, actualFunkos);
        verify(funkoRepository, times(1)).findAllByCategoriaName(categoria);
    }

    @Test
    void findAllByNombre(){
        String nombre = "nombre4";
        List<Funko> expectedFunkos = List.of(funko1);
        when(funkoRepository.findAllByNombre(nombre)).thenReturn(expectedFunkos);
        List<Funko> actualFunkos = funkoServiceImpl.findAll(nombre, null);
        assertIterableEquals(expectedFunkos, actualFunkos);
        verify(funkoRepository, times(1)).findAllByNombre(nombre);
    }

    @Test
    void findAllByNombreAndCategoria(){
        String nombre = "nombre4";
        String categoria = "dc";
        List<Funko> expectedFunkos = List.of(funko1);
        when(funkoRepository.findAllByNombreAndCategoriaName(nombre, categoria)).thenReturn(expectedFunkos);
        List<Funko> actualFunkos = funkoServiceImpl.findAll(nombre, categoria);
        assertIterableEquals(expectedFunkos, actualFunkos);
        verify(funkoRepository, times(1)).findAllByNombreAndCategoriaName(nombre, categoria);
    }

    @Test
    void findById() {
        Long id = 1L;
        Funko expectedFunko = funko1;
        when(funkoRepository.findById(id)).thenReturn(Optional.ofNullable(expectedFunko));
        Funko actualFunko = funkoServiceImpl.findById(id);
        assertEquals(expectedFunko, actualFunko);
        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void findByIdNoExiste(){
        Long id = 1L;
        when(funkoRepository.findById(id)).thenReturn(Optional.empty());
        var res = assertThrows(FunkoNotFound.class, () -> funkoServiceImpl.findById(id));
        assertEquals("Funko no encontrado", res.getMessage());
        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void save() {
        FunkoCreateDto funkoCreateDto = FunkoCreateDto.builder()
                .nombre("nombre4")
                .precio(70.89)
                .cantidad(3)
                .imagen("rutaImagen4")
                .categoria(categoria1)
                .build();
        Funko expectedFunko = Funko.builder()
                .id(1L)
                .nombre("nombre4")
                .precio(70.89)
                .cantidad(3)
                .imagen("rutaImagen4")
                .categoria(categoria2)
                .build();

        when(funkoRepository.save(any(Funko.class))).thenReturn(expectedFunko);

        Funko actualFunko = funkoServiceImpl.save(funkoCreateDto);

        assertEquals(expectedFunko, actualFunko);

        verify(funkoRepository, times(1)).save(funkoArgumentCaptor.capture());
    }

    @Test
    void update() {
        long id = 1L;
        FunkoUpdateDto funkoUpdateDto = new FunkoUpdateDto(
                "nombre4",
                70.89,
                3,
                "rutaImagen4",
                categoria1
        );
        Funko existingFunko = funko1;

        when(funkoRepository.findById(id)).thenReturn(Optional.of(existingFunko));
        when(funkoRepository.save(any(Funko.class))).thenReturn(existingFunko);

        Funko actualFunko = funkoServiceImpl.update(funkoUpdateDto, id);

        assertEquals(existingFunko, actualFunko);

        verify(funkoRepository, times(1)).findById(id);
        verify(funkoRepository, times(1)).save(any(Funko.class));
    }

    @Test
    void updateNoExiste() {
        Long id = 1L;
        FunkoUpdateDto funkoUpdateDto = new FunkoUpdateDto(
                "nombre4",
                70.89,
                3,
                "rutaImagen4",
                categoria1
        );

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        var res = assertThrows(FunkoNotFound.class, () -> funkoServiceImpl.update(funkoUpdateDto, id));

        assertEquals("Funko no encontrado", res.getMessage());
        verify(funkoRepository, times(1)).findById(id);
    }

    @Test
    void deleteById() {
        long id = 1L;
        Funko expectedFunko = funko1;

        funkoServiceImpl.deleteById(id);

        verify(funkoRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteByIdNoExiste() {
        Long id = 1L;

        when(funkoRepository.findById(id)).thenReturn(Optional.empty());

        var res = assertThrows(FunkoNotFound.class, () -> funkoServiceImpl.findById(id));
        assertEquals("Funko no encontrado", res.getMessage());

        verify(funkoRepository, times(0)).deleteById(id);
    }
}