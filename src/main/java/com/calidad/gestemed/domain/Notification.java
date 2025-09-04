package com.calidad.gestemed.domain;

// domain/Notification.java

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String message;

    // si la readFlag está activada entonces se muestra en la sección de notificaciones de la página principal
    private boolean readFlag;
    private LocalDateTime createdAt;
}
