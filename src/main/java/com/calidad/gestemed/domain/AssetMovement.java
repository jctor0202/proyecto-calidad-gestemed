// domain/AssetMovement.java

// Clase para modelar el movimiento de un activo médico

package com.calidad.gestemed.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="asset_movements",
        indexes = {
                @Index(name="idx_mov_asset", columnList="asset_id"),
                @Index(name="idx_mov_date", columnList="movedAt")
        })
public class AssetMovement {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional=false) private Asset asset;

    private LocalDateTime movedAt;

    @Column(length=200) private String fromLocation;
    @Column(length=200) private String toLocation;


    @Column(name = "from_location_latitude")
    private Double fromLocationLatitude;

    @Column(name = "from_location_Longitude")
    private Double fromLocationLongitude;

    @Column(name = "to_location_latitude")
    private Double toLocationLatitude;

    @Column(name = "to_location_longitude")
    private Double toLocationLongitude;

    @Column(length=100) private String reason;       // p.ej. "Traslado", "Mantenimiento", etc.
    @Column(length=100) private String performedBy;  // username
}
