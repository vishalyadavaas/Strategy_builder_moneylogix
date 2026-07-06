package com.moneylogix.strategybuilder.riskprofile;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public record RiskProfileRequest(
    @NotNull Map<String, Integer> answers
) {}
