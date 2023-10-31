package com.example.springfunko.category.services;

import com.example.springfunko.category.dto.CategoryResponseDto;
import com.example.springfunko.category.exception.CategoryNotFound;
import com.example.springfunko.category.mappers.CategoryMapper;
import com.example.springfunko.category.models.Categoria;
import com.example.springfunko.category.repositories.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public List<Categoria> findAll() {
        log.info("Buscando todas las categorias");
        return categoryRepository.findAll();
    }

    @Override
    public List<Categoria> findByName(String name) {
        log.info("Buscando categorias por nombre");
        return categoryRepository.findByName(name).orElseThrow(() -> new CategoryNotFound("Categoria no encontrada"));
    }

    @Override
    public Categoria findById(Long id) {
        log.info("Buscando categoria por id");
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFound("Categoria no encontrada"));
    }

    @Override
    public Categoria save(CategoryResponseDto categoria) {
        log.info("Guardando categoria");
        Categoria categoriaMapped = categoryMapper.toCategory(categoria);
        return categoryRepository.save(categoriaMapped);
    }

    @Override
    public Categoria update(CategoryResponseDto categoria, Long id) {
        log.info("Actualizando categoria");
        Categoria categoryActual = findById(id);
        return categoryRepository.save(categoryActual);
    }

    @Override
    public void deleteById(Long id) {
        log.info("Eliminando categoria");
        categoryRepository.deleteById(id);
    }
}
