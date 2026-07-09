package com.moneylogix.strategybuilder.strategy;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import com.moneylogix.strategybuilder.ml.MlDtos.RecommendationRequest;
import com.moneylogix.strategybuilder.ml.MlDtos.RecommendationResponse;
import com.moneylogix.strategybuilder.riskprofile.RiskProfileService;

@RestController
@RequestMapping("/api/strategy")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class StrategyController {

    private final RestClient mlRestClient;
    private final RiskProfileService riskProfileService;

    public StrategyController(RestClient mlRestClient, RiskProfileService riskProfileService) {
        this.mlRestClient = mlRestClient;
        this.riskProfileService = riskProfileService;
    }

    @GetMapping("/recommend")
    public ResponseEntity<RecommendationResponse> recommend(
            @RequestParam(defaultValue = "NIFTY") String symbol
    ) {
        return riskProfileService.getProfile()
                .map(profile -> {
                    RecommendationRequest req = new RecommendationRequest(
                            profile.userId().toString(),
                            profile.riskBand().name(),
                            symbol
                    );
                    RecommendationResponse response = mlRestClient.post()
                            .uri("/api/recommend")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(req)
                            .retrieve()
                            .body(RecommendationResponse.class);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}