package com.calidad.gestemed.controller;

// controller/HomeController.java

import com.calidad.gestemed.repo.NotificationRepo;
import com.calidad.gestemed.repo.PartRepo;
import com.calidad.gestemed.service.impl.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// este es el controlador de la página home
@Controller @RequiredArgsConstructor
public class HomeController {

    // Dependencias del controlador
    private final NotificationRepo notificationRepo;
    private final AlertService alerts;
    private final PartRepo partRepo;
    @GetMapping("/")
    public String home(Model m){
        m.addAttribute("notifs", notificationRepo.findAll());
        m.addAttribute("contractAlerts", alerts.contractsExpiringSoon());     // contratos por vencer
        m.addAttribute("lowStock", partRepo.findLowStock());   // refacciones con stock bajo
        return "home";
    }
}
