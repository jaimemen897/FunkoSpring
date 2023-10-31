package com.example.springfunko.category.repositories;

import com.example.springfunko.category.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Categoria, Long> {
    Optional<List<Categoria>> findByName(String name);
}
