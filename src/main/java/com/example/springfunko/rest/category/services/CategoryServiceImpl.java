package com.example.springfunko.rest.category.services;

import com.example.springfunko.rest.category.dto.CategoryResponseDto;
import com.example.springfunko.rest.category.exception.CategoryConflict;
import com.example.springfunko.rest.category.exception.CategoryNotFound;
import com.example.springfunko.rest.category.mappers.CategoryMapper;
import com.example.springfunko.rest.category.models.Categoria;
import com.example.springfunko.rest.category.repositories.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"categorias"})
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper = new CategoryMapper();

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    @Override
    public List<Categoria> findAll(String name) {
        if (name == null || name.isEmpty()) {
            log.info("Buscando todas las categorias");
            return categoryRepository.findAll();
        } else {
            log.info("Buscando categorias por nombre");
            return categoryRepository.findAllByNameContainingIgnoreCase(name).orElseThrow(() -> new CategoryNotFound("Categoria no encontrada"));
        }
    }

    @Override
    @Cacheable
    public Categoria findById(Long id) {
        log.info("Buscando categoria por id");
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFound("Categoria no encontrada"));
    }

    @Override
    @Cacheable
    public Categoria save(CategoryResponseDto categoria) {
        log.info("Guardando categoria");
        Optional<Long> id = categoryRepository.getIdByName(categoria.nombre());

        if (id.isPresent()) {
            throw new CategoryConflict("Categoria ya existe");
        } else {
            Categoria categoriaMapped = categoryMapper.toCategory(categoria);
            return categoryRepository.save(categoriaMapped);
        }
    }

    @Override
    @Cacheable
    public Categoria update(CategoryResponseDto categoria, Long id) {
        log.info("Actualizando categoria");
        Categoria categoryActual = findById(id);
        Categoria categoriaToSave = categoryMapper.toCategory(categoria, categoryActual);
        return categoryRepository.save(categoriaToSave);
    }

    @Override
    @CacheEvict
    @Transactional
    public void deleteById(Long id) {
        log.info("Eliminando categoria");
        this.findById(id);
        if (categoryRepository.existsFunkoById(id)) {
            log.warn("Categoria no eliminada, tiene funkos asociados");
            throw new CategoryConflict("Categoria no eliminada, tiene funkos asociados");
        } else {
            categoryRepository.deleteById(id);
        }
    }
}
