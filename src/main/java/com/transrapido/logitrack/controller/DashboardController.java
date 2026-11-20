package com.transrapido.logitrack.controller;

import com.transrapido.logitrack.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// Tabla 36: GET /api/dashboard/resumen (HU-10).
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen")
    public Map<String, Object> resumen() {
        return dashboardService.resumen();
    }
}
