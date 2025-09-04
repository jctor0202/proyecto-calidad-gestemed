package com.calidad.gestemed.service.impl;

import com.calidad.gestemed.domain.Contract;
import com.calidad.gestemed.repo.ContractRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/*
    Servicio para las alertas
 */

@Service
@RequiredArgsConstructor
public class AlertService {

    //dependencias
    private final ContractRepo contractRepo;
    private final JavaMailSender mailSender; // No esta implementado todavia

    // para los contratos que expiran pronto
    public List<Contract> contractsExpiringSoon() {
        LocalDate today = LocalDate.now();
        // Trae todos y filtra con su propio alertDays
        return contractRepo.findAll().stream()
                .filter(c -> c.getEndDate()!=null && !c.getEndDate().isBefore(today))
                .filter(c -> {
                    int alert = c.getAlertDays()==null?0:c.getAlertDays();
                    return !c.getEndDate().isAfter(today.plusDays(alert));
                })
                .toList();
    }


    // para enivar por correo los contratos que están por vencer
    public void sendExpiryEmails(List<Contract> list) {
        try {

            for (Contract c : list) {


                var msg = new org.springframework.mail.SimpleMailMessage();
                msg.setTo("demo@local"); // se debe tomar del cliente.
                msg.setSubject("Contrato por vencer: " + c.getCode());
                msg.setText("El contrato " + c.getCode() + " vence el " + c.getEndDate());
                mailSender.send(msg);

            }

        } catch (Exception e) {
            System.out.println("[ALERT] Emails no enviados (sin SMTP): " + e.getMessage());
        }
    }
}

