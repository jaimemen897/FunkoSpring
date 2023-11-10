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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@CacheConfig(cacheNames = {"categorias"})
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }


    @Override
    public Page<Categoria> findAll(Optional<String> name, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todas las categorias");
        Specification<Categoria> specNombre = (root, criteriaQuery, criteriaBuilder) ->
                name.map(value -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + value.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Categoria> specIsDeleted = (root, criteriaQuery, criteriaBuilder) ->
                isDeleted.map(value -> criteriaBuilder.equal(root.get("isDeleted"), value))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<Categoria> spec = Specification.where(specNombre).and(specIsDeleted);
        return categoryRepository.findAll(spec, pageable);
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
