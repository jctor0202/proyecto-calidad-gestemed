// service/impl/MovementServiceImpl.java
package com.calidad.gestemed.service.impl;

import com.calidad.gestemed.domain.Asset;
import com.calidad.gestemed.domain.AssetMovement;
import com.calidad.gestemed.repo.AssetMovementRepo;
import com.calidad.gestemed.service.MovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

    private final AssetMovementRepo repo;

    @Value("${movements.retentionDays:90}") // los días de retención de los movimientos de los activos
    // los movimientos que ya han pasado más de 90 días registrados son eliminados
    private int retentionDays;


    //para guardar el movimiento del activo
    @Override
    public void recordMove(Asset asset, String fromLoc, String toLoc, String reason, String performedBy) {
        if (asset == null) return;
        var mov = AssetMovement.builder()
                .asset(asset)
                .movedAt(LocalDateTime.now())
                .fromLocation(fromLoc)
                .toLocation(toLoc)
                .reason(reason)
                .performedBy(performedBy)
                .build();
        repo.save(mov);
    }

    @Override
    public List<AssetMovement> history(Long assetId, LocalDate from, LocalDate to, String locationLike) {
        var fromDt = (from == null) ? null : from.atStartOfDay();
        // se suma 1 día para incluir el día final
        var toDt   = (to   == null) ? null : to.plusDays(1).atStartOfDay();
        var loc = (locationLike == null || locationLike.isBlank()) ? null : locationLike.trim();
        return repo.searchNative(assetId, fromDt, toDt, loc);
    }

    //borrado automático de movimiento con más de 90 días de antiguiedad
    @Scheduled(cron = "0 0 3 * * *")
    public void enforceRetention() {
        int days = Math.max(retentionDays, 90);

        // a la fecha actual se le restan los días de retención definidos en este caso 90 días
        // cutoff representa la fecha y hora hace 90 días
        var cutoff = LocalDateTime.now().minusDays(days);

        // se eliminan los movimientos que tengan fecha anterior a la fecha cutoff
        repo.deleteByMovedAtBefore(cutoff);
    }


}
