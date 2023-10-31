package com.example.springfunko.funkos.mapper;

import com.example.springfunko.funkos.dto.FunkoCreateDto;
import com.example.springfunko.funkos.dto.FunkoResponseDto;
import com.example.springfunko.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.funkos.models.Funko;
import org.springframework.stereotype.Component;

@Component
public class FunkoMapper {
    public Funko toFunko(FunkoCreateDto funkoCreateDto) {
        return Funko.builder()
                .nombre(funkoCreateDto.nombre())
                .precio(funkoCreateDto.precio())
                .cantidad(funkoCreateDto.cantidad())
                .imagen(funkoCreateDto.imagen())
                .categoria(funkoCreateDto.categoria())
                .build();
    }

    public Funko toFunko(FunkoUpdateDto funkoUpdateDto, Funko funko) {
        return Funko.builder().id(funko.getId())
                .nombre(funkoUpdateDto.nombre() != null ? funkoUpdateDto.nombre() : funko.getNombre())
                .precio(funkoUpdateDto.precio() != null ? funkoUpdateDto.precio() : funko.getPrecio())
                .cantidad(funkoUpdateDto.cantidad() != null ? funkoUpdateDto.cantidad() : funko.getCantidad())
                .imagen(funkoUpdateDto.imagen() != null ? funkoUpdateDto.imagen() : funko.getImagen())
                .categoria(funkoUpdateDto.categoria() != null ? funkoUpdateDto.categoria() : funko.getCategoria())
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