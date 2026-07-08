package com.moneylogix.strategybuilder.strategy;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarginResponseDto {
    @JsonProperty("margin_required")
    private double marginRequired;

    private String method;

    @JsonProperty("is_defined_risk")
    private boolean isDefinedRisk;

    @JsonProperty("max_loss")
    private Object maxLoss;

    public double getMarginRequired() { return marginRequired; }
    public void setMarginRequired(double marginRequired) { this.marginRequired = marginRequired; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public boolean getIsDefinedRisk() { return isDefinedRisk; }
    public void setIsDefinedRisk(boolean isDefinedRisk) { this.isDefinedRisk = isDefinedRisk; }
    public Object getMaxLoss() { return maxLoss; }
    public void setMaxLoss(Object maxLoss) { this.maxLoss = maxLoss; }
}