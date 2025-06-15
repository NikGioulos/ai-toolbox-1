package dev.nikosg.poc.aitoolbox1.tooling.tools;

import dev.nikosg.poc.aitoolbox1.backend.domain.ExchangeRate;
import dev.nikosg.poc.aitoolbox1.backend.service.AmountService;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.Tool;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class AmountTools {

    private final AmountService amountService;

    public AmountTools(AmountService amountService) {
        this.amountService = amountService;
    }

    @Tool(name = "convert_amount", description = "Accepts amount in source currency and returns converted amount in the target currency")
    public BigDecimal convertAmount(@ToolParam(value = "Convert Amount Request Data") ExchangeRate exchangeRate) {
        return amountService.convertAmount(exchangeRate);
    }

    @Tool(name = "convert_amounts", description = "Accepts an array of amounts in source currency and returns converted amounts in the target currency")
    public List<BigDecimal> convertAmounts(@ToolParam(value = "Convert Amount Request Array") ExchangeRate[] exchangeRates) {
        // my Tool parser does not work well yet with Collection params, so i have defined the param as Array
        List<ExchangeRate> exchangeRatesList = Arrays.stream(exchangeRates).toList();
        return amountService.convertAmounts(exchangeRatesList);
    }
}
