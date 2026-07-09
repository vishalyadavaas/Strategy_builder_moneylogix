package com.moneylogix.strategybuilder.riskprofile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/risk-profile")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@RequiredArgsConstructor
public class RiskProfileController {

    private final RiskProfileService service;

    @PostMapping
    public ResponseEntity<RiskProfileResponse> save(@Valid @RequestBody RiskProfileRequest request) {
        return ResponseEntity.ok(service.saveProfile(request));
    }

    @GetMapping("/me")
    public ResponseEntity<RiskProfileResponse> get() {
        return service.getProfile().map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}