package com.example.springfunko.funkos.repositories;

import com.example.springfunko.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FunkoRepository extends JpaRepository<Funko, Long> {
    List<Funko> findAllByNombre(String nombre);

    List<Funko> findAllByCategoriaName(String categoria);

    List<Funko> findAllByNombreAndCategoriaName(String nombre, String categoria);
}
