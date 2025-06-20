package dev.nikosg.poc.aitoolbox1.backend.service;

import dev.nikosg.poc.aitoolbox1.backend.domain.ExchangeRate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class AmountService {
    public BigDecimal convertAmount(ExchangeRate exchangeRate) {
        Map<String, BigDecimal> rates = Map.of(
                "EUR", new BigDecimal(1),
                "USD", new BigDecimal("0.9"),
                "CHF", new BigDecimal("1.1"),
                "GBP", new BigDecimal("1.2")
        );
        if (exchangeRate.getFromCurrency().equals(exchangeRate.getToCurrency())) {
            return exchangeRate.getFromAmount();
        }
        BigDecimal rateFrom = rates.get(exchangeRate.getFromCurrency());
        BigDecimal amountInBaseCurrency = exchangeRate.getFromAmount().multiply(rateFrom);
        BigDecimal rateTo = rates.get(exchangeRate.getToCurrency());
        return amountInBaseCurrency.divide(rateTo, 2, RoundingMode.HALF_DOWN);
    }

    public List<BigDecimal> convertAmounts(List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream().map(this::convertAmount).toList();
    }
}
