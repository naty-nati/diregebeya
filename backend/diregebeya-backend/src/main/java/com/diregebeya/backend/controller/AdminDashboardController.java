package com.diregebeya.backend.controller;

import com.diregebeya.backend.dto.admin.AdminStatsResponse;
import com.diregebeya.backend.service.AdminStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','STAFF')")
@Tag(name = "Admin - Dashboard", description = "Aggregate statistics across the whole store")
public class AdminDashboardController {

    private final AdminStatsService adminStatsService;

    @Operation(summary = "Get store-wide statistics (users, catalog size, orders, revenue)")
    @GetMapping
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminStatsService.getStats());
    }
}
