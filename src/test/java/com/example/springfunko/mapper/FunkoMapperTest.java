package com.example.springfunko.mapper;

import com.example.springfunko.funkos.dto.FunkoCreateDto;
import com.example.springfunko.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.funkos.mapper.FunkoMapper;
import com.example.springfunko.funkos.models.Funko;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FunkoMapperTest {
    private final FunkoMapper funkoMapper = new FunkoMapper();

    @Test
    void toFunkoTest() {
        Long id = 1L;
        FunkoCreateDto funkoCreateDto = new FunkoCreateDto("nombre", 54.52, 1, "rutaImagen", "disney");

        var res = funkoMapper.toFunko(id, funkoCreateDto);

        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(funkoCreateDto.nombre(), res.getNombre()),
                () -> assertEquals(funkoCreateDto.precio(), res.getPrecio()),
                () -> assertEquals(funkoCreateDto.cantidad(), res.getCantidad()),
                () -> assertEquals(funkoCreateDto.imagen(), res.getImagen()),
                () -> assertEquals(funkoCreateDto.categoria(), res.getCategoria())
        );
    }

    @Test
    void testToFunko() {
        Long id = 1L;
        FunkoUpdateDto funkoCreateDto = new FunkoUpdateDto("nombre", 54.52, 1, "rutaImagen", "disney");

        Funko funko = Funko.builder()
                .id(id)
                .nombre("nombre")
                .precio(54.52)
                .cantidad(1)
                .imagen("rutaImagen")
                .categoria("disney")
                .build();

        var res = funkoMapper.toFunko(funkoCreateDto, funko);

        assertAll(
                () -> assertEquals(id, res.getId()),
                () -> assertEquals(funkoCreateDto.nombre(), res.getNombre()),
                () -> assertEquals(funkoCreateDto.precio(), res.getPrecio()),
                () -> assertEquals(funkoCreateDto.cantidad(), res.getCantidad()),
                () -> assertEquals(funkoCreateDto.imagen(), res.getImagen()),
                () -> assertEquals(funkoCreateDto.categoria(), res.getCategoria()));
    }

    @Test
    void toFunkoResponseDto() {
        Funko funko = Funko.builder()
                .id(1L)
                .nombre("nombre")
                .precio(54.52)
                .cantidad(1)
                .imagen("rutaImagen")
                .categoria("disney")
                .build();

        var res = funkoMapper.toFunkoResponseDto(funko);

        assertAll(
                () -> assertEquals(funko.getId(), res.id()),
                () -> assertEquals(funko.getNombre(), res.nombre()),
                () -> assertEquals(funko.getPrecio(), res.precio()),
                () -> assertEquals(funko.getCantidad(), res.cantidad()),
                () -> assertEquals(funko.getImagen(), res.imagen()),
                () -> assertEquals(funko.getCategoria(), res.categoria()),
                () -> assertEquals(funko.getFechaCreacion(), res.fechaCreacion()),
                () -> assertEquals(funko.getFechaActualizacion(), res.fechaActualizacion()));
    }
}