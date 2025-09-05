// Clase para modelar una orden de mantenimiento

package com.calidad.gestemed.domain;

// domain/MaintenanceOrder.java

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaintenanceOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    // Un activo puede tener muchas ordenes de mantenimiento asociadas pero una orden de mantenimiento solo está asociada a un único activo
    // Por eso la relacion es ManyToOne, es deci, muchas ordenes de mantenimiento asociadas a un activo
    @ManyToOne(optional=false) private Asset asset;

    @Enumerated(EnumType.STRING)
    private MaintType type; // PREVENTIVO, CORRECTIVO

    private LocalDate scheduledDate;

    private String responsible;

    @Column(length=2000) private String tasks;
    @Enumerated(EnumType.STRING) private MaintStatus status; // PENDIENTE, EN_CURSO, FINALIZADO

    // Cierre del mantenimiento
    @Column(length=4000) private String photoPaths; // varias fotos (MVP)
    private String signaturePath; // firma PNG
    private LocalDateTime closedAt;
}
