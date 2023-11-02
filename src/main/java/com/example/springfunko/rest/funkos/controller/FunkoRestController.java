package com.example.springfunko.rest.funkos.controller;

import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.models.Funko;
import com.example.springfunko.rest.funkos.services.FunkoServiceImpl;
import com.example.springfunko.rest.storage.services.StorageService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/funkos")
@RestController
@Slf4j
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
        ex.getBindingResult().getAllErrors().forEach(error -> {
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
    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Funko> patchFunko(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(funkoService.updateImage(id, file));
    }

    @Min(value = 0, message = "El id no puede ser negativo")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunko(@Valid @PathVariable int id) {
        funkoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}