package dev.nikosg.poc.aitoolbox1.backend.domain;

import java.math.BigDecimal;

public class ExchangeRate {
    private String fromCurrency;
    private String toCurrency;
    private BigDecimal fromAmount;

    public ExchangeRate(String fromCurrency, String toCurrency, BigDecimal fromAmount) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.fromAmount = fromAmount;
    }



    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public BigDecimal getFromAmount() {
        return fromAmount;
    }
}
