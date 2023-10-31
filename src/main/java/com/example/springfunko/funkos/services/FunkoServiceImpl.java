package com.example.springfunko.funkos.services;

import com.example.springfunko.funkos.dto.FunkoCreateDto;
import com.example.springfunko.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.funkos.exception.FunkoNotFound;
import com.example.springfunko.funkos.mapper.FunkoMapper;
import com.example.springfunko.funkos.models.Funko;
import com.example.springfunko.funkos.repositories.FunkoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@CacheConfig(cacheNames = {"funkos"})
@Service
public class FunkoServiceImpl implements FunkoService {

    private final FunkoRepository funkoRepository;
    private final FunkoMapper funkoMapper = new FunkoMapper();

    @Autowired
    public FunkoServiceImpl(FunkoRepository funkoRepository) {
        this.funkoRepository = funkoRepository;
    }

    @Override
    public List<Funko> findAll(String nombre, String categoria) {
        if ((nombre == null || nombre.isEmpty()) && (categoria == null || categoria.isEmpty())) {
            return funkoRepository.findAll();
        }
        if ((nombre != null && !nombre.isEmpty()) && (categoria == null || categoria.isEmpty())) {
            return funkoRepository.findAllByNombre(nombre);
        }
        if (nombre == null || nombre.isEmpty()) {
            return funkoRepository.findAllByCategoriaName(categoria);
        }
        return funkoRepository.findAllByNombreAndCategoriaName(nombre, categoria);
    }

    @Override
    @Cacheable
    public Funko findById(long id) {
        return funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFound("Funko no encontrado"));
    }

    @Override
    @CachePut(key = "#result.id")
    public Funko save(FunkoCreateDto funko) {
        Funko funkoMapped = funkoMapper.toFunko(funko);
        return funkoRepository.save(funkoMapped);
    }

    @Override
    @CachePut(key = "#id")
    public Funko update(FunkoUpdateDto funko, Long id) {
        return funkoRepository.save(funkoMapper.toFunko(funko, funkoRepository.findById(id).orElseThrow(
                () -> new FunkoNotFound("Funko no encontrado"))));
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(long id) {
        funkoRepository.deleteById(id);
    }
}
