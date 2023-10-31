package com.example.springfunko.funkos.models;

import com.example.springfunko.category.models.Categoria;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "funko")
public class Funko {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "El nombre no puede estar vacio")
    private String nombre;
    @Min(value = 0, message = "El precio no puede ser negativo")
    private double precio;
    @Min(value = 0, message = "la cantidad no puede ser negativo")
    private int cantidad;
    @NotEmpty
    private String imagen;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categoria categoria;
    @Builder.Default
    private LocalDate fechaCreacion = LocalDate.now();
    @Builder.Default
    private LocalDate fechaActualizacion = LocalDate.now();
}
