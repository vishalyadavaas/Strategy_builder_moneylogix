package com.moneylogix.strategybuilder.strategy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PayoffResponseDto {
    @JsonProperty("spot_prices")
    private List<Double> spotPrices;

    private List<Double> payoff;
    private List<Double> breakevens;

    @JsonProperty("max_profit")
    private Object maxProfit;

    @JsonProperty("max_loss")
    private Object maxLoss;

    public List<Double> getSpotPrices() { return spotPrices; }
    public void setSpotPrices(List<Double> spotPrices) { this.spotPrices = spotPrices; }
    public List<Double> getPayoff() { return payoff; }
    public void setPayoff(List<Double> payoff) { this.payoff = payoff; }
    public List<Double> getBreakevens() { return breakevens; }
    public void setBreakevens(List<Double> breakevens) { this.breakevens = breakevens; }
    public Object getMaxProfit() { return maxProfit; }
    public void setMaxProfit(Object maxProfit) { this.maxProfit = maxProfit; }
    public Object getMaxLoss() { return maxLoss; }
    public void setMaxLoss(Object maxLoss) { this.maxLoss = maxLoss; }
}