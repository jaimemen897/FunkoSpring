package com.example.springfunko.rest.funkos.services;

import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoResponseDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface FunkoService {
    Page<FunkoResponseDto> findAll(Optional<String> nombre, Optional<String> categoria, Optional<Double> precioMax, Pageable pageable);

    FunkoResponseDto findById(long id);

    FunkoResponseDto save(FunkoCreateDto funko);

    FunkoResponseDto update(FunkoUpdateDto funko, Long id);

    FunkoResponseDto updateImage(Long id, MultipartFile file);

    void deleteById(long id);
}
