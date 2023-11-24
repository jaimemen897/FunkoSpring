package com.example.springfunko.rest.funkos.controller;

import com.example.springfunko.rest.funkos.dto.FunkoCreateDto;
import com.example.springfunko.rest.funkos.dto.FunkoResponseDto;
import com.example.springfunko.rest.funkos.dto.FunkoUpdateDto;
import com.example.springfunko.rest.funkos.services.FunkoServiceImpl;
import com.example.springfunko.utils.pagination.PageResponse;
import com.example.springfunko.utils.pagination.PaginationLinksUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RequestMapping("/api/funkos")
@RestController
@Slf4j
@PreAuthorize("hasRole('USER')")
public class FunkoRestController {

    private final FunkoServiceImpl funkoService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public FunkoRestController(FunkoServiceImpl funkoService, PaginationLinksUtils paginationLinksUtils) {
        this.funkoService = funkoService;
        this.paginationLinksUtils = paginationLinksUtils;
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
    @Operation(summary = "Obtener una lista paginada de Funkos", description = "Obtener una lista paginada de Funkos", tags = {"funkos"})
    @Parameters({
            @Parameter(name = "nombre", description = "Nombre del funko", example = "Batman"),
            @Parameter(name = "categoria", description = "Categoria del funko", example = "DC"),
            @Parameter(name = "precioMax", description = "Precio maximo del funko", example = "100.0"),
            @Parameter(name = "page", description = "Numero de pagina", example = "0"),
            @Parameter(name = "size", description = "Numero de elementos por pagina", example = "10"),
            @Parameter(name = "sortBy", description = "Campo por el que se ordena", example = "id"),
            @Parameter(name = "direction", description = "Direccion de la ordenacion", example = "asc")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de Funkos paginada"),
            @ApiResponse(responseCode = "400", description = "Error en la peticion")
    })
    public ResponseEntity<PageResponse<FunkoResponseDto>> getFunkos(
            @RequestParam(required = false) Optional<String> nombre,
            @RequestParam(required = false) Optional<String> categoria,
            @RequestParam(required = false) Optional<Double> precioMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<FunkoResponseDto> pageResult = funkoService.findAll(nombre, categoria, precioMax, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un funko por su id", description = "Obtener un funko por su id", tags = {"funkos"})
    @Parameters({
            @Parameter(name = "id", description = "Id del funko", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funko encontrado"),
            @ApiResponse(responseCode = "400", description = "Error en la peticion"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado")
    })
    @Min(value = 0, message = "El id no puede ser negativo")
    public ResponseEntity<FunkoResponseDto> getFunkoById(@PathVariable Integer id) {
        return ResponseEntity.ok(funkoService.findById(id));
    }

    @NonNull
    @PostMapping()
    @Operation(summary = "Crear un funko", description = "Crear un funko", tags = {"funkos"})
    @Parameters({
            @Parameter(name = "funko", description = "Funko a crear", example = "{'nombre': 'Batman', 'categoria': 'DC', 'precio': 100.0}")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funko creado"),
            @ApiResponse(responseCode = "400", description = "Error en la peticion")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunkoResponseDto> postFunko(@Valid @RequestBody FunkoCreateDto funko) {
        return ResponseEntity.status(HttpStatus.CREATED).body(funkoService.save(funko));
    }

    @NonNull
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un funko", description = "Actualizar un funko", tags = {"funkos"})
    @Parameters({
            @Parameter(name = "id", description = "Id del funko", example = "1"),
            @Parameter(name = "funko", description = "Funko a actualizar", example = "{'nombre': 'Batman', 'categoria': 'DC', 'precio': 100.0}")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funko actualizado"),
            @ApiResponse(responseCode = "400", description = "Error en la peticion"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunkoResponseDto> putFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funko) {
        return ResponseEntity.ok(funkoService.update(funko, id));
    }

    @NonNull
    @PatchMapping("/{id}")
    @Operation(summary = "Actualizar un funko", description = "Actualizar un funko", tags = {"funkos"})
    @Parameters({
            @Parameter(name = "id", description = "Id del funko", example = "1"),
            @Parameter(name = "funko", description = "Funko a actualizar", example = "{'nombre': 'Batman', 'categoria': 'DC', 'precio': 100.0}")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funko actualizado"),
            @ApiResponse(responseCode = "400", description = "Error en la peticion"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunkoResponseDto> patchFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateDto funko) {
        return ResponseEntity.ok(funkoService.update(funko, id));
    }

    @NonNull
    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Actualizar la imagen de un funko", description = "Actualizar la imagen de un funko", tags = {"funkos"})
    @Parameters({
            @Parameter(name = "id", description = "Id del funko", example = "1"),
            @Parameter(name = "file", description = "Imagen del funko", example = "funko.jpg")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funko actualizado"),
            @ApiResponse(responseCode = "400", description = "Error en la peticion"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunkoResponseDto> patchFunko(@PathVariable Long id, @RequestPart("file") MultipartFile file) {
        if (!Objects.requireNonNull(file.getContentType()).startsWith("image/")) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(funkoService.updateImage(id, file));
    }

    @Min(value = 0, message = "El id no puede ser negativo")
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un funko", description = "Eliminar un funko", tags = {"funkos"})
    @Parameters({
            @Parameter(name = "id", description = "Id del funko", example = "1")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Funko eliminado"),
            @ApiResponse(responseCode = "400", description = "Error en la peticion"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFunko(@Valid @PathVariable int id) {
        funkoService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}