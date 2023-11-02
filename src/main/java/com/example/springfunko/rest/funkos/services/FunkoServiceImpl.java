package com.example.springfunko.rest.funkos.services;

import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.exception.FunkoNotFound;
import com.example.springfunko.rest.funkos.mapper.FunkoMapper;
import com.example.springfunko.rest.funkos.models.Funko;
import com.example.springfunko.rest.funkos.repositories.FunkoRepository;
import com.example.springfunko.rest.storage.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CacheConfig(cacheNames = {"funkos"})
@Service
public class FunkoServiceImpl implements FunkoService {

    private final FunkoRepository funkoRepository;
    private final FunkoMapper funkoMapper = new FunkoMapper();
    private final StorageService storageService;

    @Autowired
    public FunkoServiceImpl(FunkoRepository funkoRepository, StorageService storageService) {
        this.funkoRepository = funkoRepository;
        this.storageService = storageService;
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

    @CachePut(key = "#id")
    public Funko updateImage(Long id, MultipartFile file) {

        if (!file.isEmpty()) {
            String imagen = storageService.store(file);
            String urlImagen = storageService.getUrl(imagen);

            Funko funko = findById(id);
            funko.setImagen(urlImagen);
            return funkoRepository.save(funko);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado la imagen");
        }
    }

    @Override
    @CacheEvict(key = "#id")
    public void deleteById(long id) {
        funkoRepository.deleteById(id);
    }
}
