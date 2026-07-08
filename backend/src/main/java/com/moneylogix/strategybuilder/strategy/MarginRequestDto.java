package com.moneylogix.strategybuilder.strategy;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MarginRequestDto {
    private List<OptionLegDto> legs;

    @JsonProperty("current_spot")
    private double currentSpot;

    public List<OptionLegDto> getLegs() { return legs; }
    public void setLegs(List<OptionLegDto> legs) { this.legs = legs; }
    public double getCurrentSpot() { return currentSpot; }
    public void setCurrentSpot(double currentSpot) { this.currentSpot = currentSpot; }
}