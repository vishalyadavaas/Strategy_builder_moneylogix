package com.moneylogix.strategybuilder.strategy;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OptionLegDto {
    @JsonProperty("option_type")
    private String optionType; // "call" or "put"

    private String position;    // "buy" or "sell"
    private double strike;
    private double premium;
    private int quantity = 1;

    public String getOptionType() { return optionType; }
    public void setOptionType(String optionType) { this.optionType = optionType; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public double getStrike() { return strike; }
    public void setStrike(double strike) { this.strike = strike; }
    public double getPremium() { return premium; }
    public void setPremium(double premium) { this.premium = premium; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}