package com.example.springfunko.rest.funkos.repositories;

import com.example.springfunko.rest.funkos.models.Funko;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FunkoRepository extends JpaRepository<Funko, Long>, JpaSpecificationExecutor<Funko> {
    List<Funko> findAllByNombre(String nombre);

    List<Funko> findAllByCategoriaName(String categoria);

    List<Funko> findAllByNombreAndCategoriaName(String nombre, String categoria);
}
