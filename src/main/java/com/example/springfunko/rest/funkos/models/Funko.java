package com.example.springfunko.rest.funkos.models;

import com.example.springfunko.rest.category.models.Categoria;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "funko")
public class Funko {
    public static final String IMAGE_DEFAULT = "https://placehold.co/600x400";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacio")
    @Column(nullable = false)
    private String nombre;

    @Min(value = 0, message = "El precio no puede ser negativo")
    @Column(nullable = false)
    private Double precio;

    @Min(value = 0, message = "la cantidad no puede ser negativo")
    @Column(nullable = false)
    private int cantidad;

    @NotEmpty
    @Column(nullable = false)
    @Builder.Default
    private String imagen = IMAGE_DEFAULT;

    @Builder.Default
    @Column(name = "fecha_creacion",updatable = false, nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate fechaCreacion = LocalDate.now();

    @Builder.Default
    @Column(name = "fecha_actualizacion", updatable = true, nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDate fechaActualizacion = LocalDate.now();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Categoria categoria;
}
