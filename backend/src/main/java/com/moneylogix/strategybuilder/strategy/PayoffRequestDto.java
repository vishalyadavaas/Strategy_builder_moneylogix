package com.moneylogix.strategybuilder.strategy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PayoffRequestDto {
    private List<OptionLegDto> legs;

    @JsonProperty("current_spot")
    private double currentSpot;

    @JsonProperty("spot_range_pct")
    private double spotRangePct = 0.1;

    private int steps = 100;

    public List<OptionLegDto> getLegs() { return legs; }
    public void setLegs(List<OptionLegDto> legs) { this.legs = legs; }
    public double getCurrentSpot() { return currentSpot; }
    public void setCurrentSpot(double currentSpot) { this.currentSpot = currentSpot; }
    public double getSpotRangePct() { return spotRangePct; }
    public void setSpotRangePct(double spotRangePct) { this.spotRangePct = spotRangePct; }
    public int getSteps() { return steps; }
    public void setSteps(int steps) { this.steps = steps; }
}