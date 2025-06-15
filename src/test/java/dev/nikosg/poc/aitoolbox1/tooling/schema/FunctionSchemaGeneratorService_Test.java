package dev.nikosg.poc.aitoolbox1.tooling.schema;

import dev.nikosg.poc.aitoolbox1.backend.domain.ExchangeRate;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

class FunctionSchemaGeneratorService_Test {

    FunctionSchemaGeneratorService sut = new FunctionSchemaGeneratorService();

    @Test
    void shouldGenerateSchemaForDto() throws Exception {
        String json = sut.generateJsonSchemaAsString(ExchangeRate.class);
        System.out.println(json);
    }

    @Test
    void shouldGenerateSchemaForString() throws Exception {
        String json = sut.generateJsonSchemaAsString(String.class);
        System.out.println(json);
    }

    @Test
    void shouldGenerateSchemaForCollectionOfString() throws Exception {
        List<String> someList = List.of();
        String json = sut.generateJsonSchemaAsString(someList.getClass());
        System.out.println(json);
    }

    @Test
    void shouldGenerateSchemaForCollectionOfDto() throws Exception {
        List<ExchangeRate> someList = List.of(new ExchangeRate("EUR", "USD", BigDecimal.TEN));
        String json = sut.generateJsonSchemaAsString(someList.getClass());
        System.out.println(json);
    }

    @Test
    void shouldGenerateSchemaForArrayOfString() throws Exception {
        String[] names = {};
        String json = sut.generateJsonSchemaAsString(names.getClass());
        System.out.println(json);
    }

    @Test
    void shouldGenerateSchemaForArrayOfDto() throws Exception {
        ExchangeRate[] rates = new ExchangeRate[0];
        String json = sut.generateJsonSchemaAsString(rates.getClass());
        System.out.println(json);
    }

}