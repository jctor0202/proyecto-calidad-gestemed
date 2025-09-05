package com.calidad.gestemed.domain;

// domain/PartConsumption.java

// Clase para modelar el consumo de las piezas

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PartConsumption {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) private MaintenanceOrder orderRef;
    @ManyToOne(optional=false) private Part part;
    private Integer quantity;
}
