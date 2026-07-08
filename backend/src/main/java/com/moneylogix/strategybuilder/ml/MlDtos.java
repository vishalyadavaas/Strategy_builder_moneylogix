package com.moneylogix.strategybuilder.ml;

public class MlDtos {

    public record RecommendationRequest(
            String userId,
            String riskBand,
            String symbol
    ) {}

    public record Strategy(
            String name,
            String description,
            Double maxLoss,
            Double maxProfit
    ) {}

    public record RecommendationResponse(
            String userId,
            String symbol,
            String riskBand,
            Strategy recommendedStrategy,
            double confidence
    ) {}
}