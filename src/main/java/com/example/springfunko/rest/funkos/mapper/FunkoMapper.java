package com.example.springfunko.rest.funkos.mapper;

import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoResponseDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.models.Funko;
import org.springframework.stereotype.Component;

@Component
public class FunkoMapper {
    public Funko toFunko(FunkoCreateDto funkoCreateDto, Categoria categoria) {
        return Funko.builder()
                .nombre(funkoCreateDto.nombre())
                .precio(funkoCreateDto.precio())
                .cantidad(funkoCreateDto.cantidad())
                .imagen(funkoCreateDto.imagen())
                .categoria(categoria)
                .build();
    }

    public Funko toFunko(FunkoUpdateDto funkoUpdateDto, Funko funko, Categoria categoria) {
        return Funko.builder().id(funko.getId())
                .nombre(funkoUpdateDto.nombre() != null ? funkoUpdateDto.nombre() : funko.getNombre())
                .precio(funkoUpdateDto.precio() != null ? funkoUpdateDto.precio() : funko.getPrecio())
                .cantidad(funkoUpdateDto.cantidad() != null ? funkoUpdateDto.cantidad() : funko.getCantidad())
                .imagen(funkoUpdateDto.imagen() != null ? funkoUpdateDto.imagen() : funko.getImagen())
                .categoria(categoria)
                .build();
    }

    public FunkoResponseDto toFunkoResponseDto(Funko funko) {
        return new FunkoResponseDto(
                funko.getId(),
                funko.getNombre(),
                funko.getPrecio(),
                funko.getCantidad(),
                funko.getImagen(),
                funko.getCategoria(),
                funko.getFechaCreacion(),
                funko.getFechaActualizacion()
        );
    }
}