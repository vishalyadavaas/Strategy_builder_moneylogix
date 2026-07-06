package com.moneylogix.strategybuilder.riskprofile;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiskProfileService {

    private final RiskProfileRepository repository;

    // Demo user UUID — matches the seed in V1 migration
    private static final UUID DEMO_USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    public RiskProfileResponse saveProfile(RiskProfileRequest request) {
        int score = calculateScore(request.answers());
        RiskBand band = toBand(score);

        RiskProfile profile = RiskProfile.builder()
                .id(UUID.randomUUID())
                .userId(DEMO_USER_ID)
                .riskBand(band)
                .score(score)
                .answers(request.answers())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        repository.save(profile);
        return toResponse(profile);
    }

    public Optional<RiskProfileResponse> getProfile() {
        return repository
                .findTopByUserIdOrderByCreatedAtDesc(DEMO_USER_ID)
                .map(this::toResponse);
    }

    /**
     * Weighted scoring:
     * Q1 loss_tolerance → weight 3 (strongest predictor)
     * Q2 drawdown_reaction → weight 3
     * Q3 investment_horizon → weight 2
     * Q4 income_stability → weight 2
     * Q5 prior_experience → weight 1
     * Q6 goal → weight 1
     * Each answer is 1-4. Max score = (3+3+2+2+1+1) * 4 = 48
     */
    private int calculateScore(Map<String, Integer> answers) {
        Map<String, Integer> weights = Map.of(
                "loss_tolerance", 3,
                "drawdown_reaction", 3,
                "investment_horizon", 2,
                "income_stability", 2,
                "prior_experience", 1,
                "goal", 1);

        return weights.entrySet().stream()
                .mapToInt(e -> {
                    int answer = answers.getOrDefault(e.getKey(), 1);
                    return e.getValue() * answer;
                })
                .sum();
    }

    private RiskBand toBand(int score) {
        if (score <= 20)
            return RiskBand.CONSERVATIVE;
        if (score <= 36)
            return RiskBand.MODERATE;
        return RiskBand.AGGRESSIVE;
    }

    private RiskProfileResponse toResponse(RiskProfile p) {
        return new RiskProfileResponse(
                p.getId(), p.getUserId(),
                p.getRiskBand(), p.getScore(), p.getCreatedAt());
    }
}