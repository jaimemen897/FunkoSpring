package com.example.springfunko.rest.category.repositories;

import com.example.springfunko.rest.category.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Categoria, Long>, JpaSpecificationExecutor<Categoria> {
    Optional<List<Categoria>> findAllByNameContainingIgnoreCase(String name);

    Optional<Categoria> findByNameContainingIgnoreCase(String name);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Funko p WHERE p.categoria.id = :id")
    Boolean existsFunkoById(Long id);

    @Query("SELECT p.id FROM Categoria p WHERE p.name = :name")
    Optional<Long> getIdByName(String name);
}
