package com.example.springfunko.funkos.models;

import com.example.springfunko.category.models.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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
    @Column(nullable = false)
    private String nombre;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private double precio;

    @Min(value = 0, message = "la cantidad no puede ser negativo")
    @Column(nullable = false)
    private int cantidad;

    @NotEmpty
    @Column(nullable = false)
    private String imagen;

    @Builder.Default
    @Column(updatable = false, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate fechaCreacion = LocalDate.now();

    @Builder.Default
    @Column(updatable = true, nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDate fechaActualizacion = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categoria categoria;
}
