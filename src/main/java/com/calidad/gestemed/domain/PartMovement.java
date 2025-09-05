package com.calidad.gestemed.domain;

// Clase para modelar el aumento o disminución del stock de piezas
// domain/PartMovement.java

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PartMovement {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    // Es la pieza que sufrió un aumento o disminución de inventario
    @ManyToOne(optional=false) private Part part;

    // para la variación del inventario
    // si aumenta o disminuye el stock de la pieza
    private Integer delta; // >0 entrada, <0 salida

    private String note;

    private LocalDateTime createdAt;
}
