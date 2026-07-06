package com.moneylogix.strategybuilder.riskprofile;

import java.time.Instant;
import java.util.UUID;

public record RiskProfileResponse(
    UUID id,
    UUID userId,
    RiskBand riskBand,
    Integer score,
    Instant createdAt
) {}
