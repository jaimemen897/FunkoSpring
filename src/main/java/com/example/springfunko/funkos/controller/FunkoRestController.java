package com.example.springfunko.funkos.controller;

import com.example.springfunko.funkos.dto.FunkoCreateDto;
import com.example.springfunko.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.funkos.models.Funko;
import com.example.springfunko.funkos.services.FunkoServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/funkos")
@RestController
public class FunkoRestController {

    private final FunkoServiceImpl funkoService;

    @Autowired
    public FunkoRestController(FunkoServiceImpl funkoService) {
        this.funkoService = funkoService;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @GetMapping()
    public ResponseEntity<List<Funko>> getFunkos(@RequestParam(required = false) String categoria, @RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(funkoService.findAll(nombre, categoria));
    }

    @GetMapping("/{id}")
    @Min(value = 0, message = "El id no puede ser negativo")
    public ResponseEntity<Funko> getFunkoById(@PathVariable Integer id) {
        return ResponseEntity.ok(funkoService.findById(id));
    }

    @NonNull
    @PostMapping()
    public ResponseEntity<Funko> postFunko(@Valid @RequestBody FunkoCreateDto funko) {
        return ResponseEntity.status(HttpStatus.CREATED).body(funkoService.save(funko));
    }

    @NonNull
    @PutMapping("/{id}")
    public ResponseEntity<Funko> putFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funko) {
        return ResponseEntity.ok(funkoService.update(funko, id));
    }

    @NonNull
    @PatchMapping("/{id}")
    public ResponseEntity<Funko> patchFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funko) {
        return ResponseEntity.ok(funkoService.update(funko, id));
    }

    @Min(value = 0, message = "El id no puede ser negativo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunko(@Valid @PathVariable int id) {
        funkoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}