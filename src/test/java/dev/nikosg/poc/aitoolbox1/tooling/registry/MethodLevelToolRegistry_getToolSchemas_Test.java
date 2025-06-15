package dev.nikosg.poc.aitoolbox1.tooling.registry;

import com.openai.core.JsonArray;
import com.openai.core.JsonObject;
import com.openai.models.FunctionDefinition;
import com.openai.models.FunctionParameters;
import com.openai.models.chat.completions.ChatCompletionTool;
import dev.nikosg.poc.aitoolbox1.backend.service.AmountService;
import dev.nikosg.poc.aitoolbox1.backend.service.MyService;
import dev.nikosg.poc.aitoolbox1.tooling.schema.FunctionSchemaGeneratorService;
import dev.nikosg.poc.aitoolbox1.tooling.schema.SchemaGeneratorService;
import dev.nikosg.poc.aitoolbox1.tooling.tools.AmountTools;
import dev.nikosg.poc.aitoolbox1.tooling.tools.MyTools;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ListableBeanFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MethodLevelToolRegistry_getToolSchemas_Test {
    @Mock
    ListableBeanFactory beanFactory;
    @Spy
    SchemaGeneratorService schemaGeneratorService = new FunctionSchemaGeneratorService();
    @InjectMocks
    MethodLevelToolRegistry methodLevelToolRegistry;

    @Test
    void shouldReturnFunctionDefinition_whenMethodToolRequiresOneStringParameter() {
        // given
        mockBeanMyTools();
        methodLevelToolRegistry.onApplicationReady();

        // when
        List<ChatCompletionTool> toolSchemas = methodLevelToolRegistry.getToolSchemas();

        // then
        verify(schemaGeneratorService).generateForClass(String.class);
        FunctionParameters functionParameters = getFunctionParameters(toolSchemas, "get_weather");
        assertRequired(functionParameters, "location");
        JsonObject properties = (JsonObject) functionParameters._additionalProperties().get("properties");
        assertThat(properties).isNotNull();

        JsonObject location = (JsonObject) properties.values().get("location");
        assertThat(location).isNotNull();
        assertThat(location.values().get("description").asStringOrThrow()).isEqualTo("city or country");
        assertTypeIs(location, "string");
    }

    @Test
    void shouldReturnFunctionDefinition_whenMethodToolRequiresNoParameter() {
        // given
        mockBeanMyTools();
        methodLevelToolRegistry.onApplicationReady();

        // when
        List<ChatCompletionTool> toolSchemas = methodLevelToolRegistry.getToolSchemas();

        // then
        FunctionParameters functionParameters = getFunctionParameters(toolSchemas, "get_time");
        JsonObject properties = (JsonObject) functionParameters._additionalProperties().get("properties");
        assertThat(properties.values()).isEmpty();
    }

    @Test
    void shouldReturnFunctionDefinition_whenMethodToolRequiresDtoParameter() {
        // given
        mockBeanAmountTools();
        methodLevelToolRegistry.onApplicationReady();

        // when
        List<ChatCompletionTool> toolSchemas = methodLevelToolRegistry.getToolSchemas();

        // then
        FunctionParameters functionParameters = getFunctionParameters(toolSchemas, "convert_amount");
        assertRequired(functionParameters, "exchangeRate");
        JsonObject properties = (JsonObject) functionParameters._additionalProperties().get("properties");
        assertThat(properties).isNotNull();

        JsonObject exchangeRate = (JsonObject) properties.values().get("exchangeRate");
        assertThat(exchangeRate).isNotNull();
        assertThat(exchangeRate.values().get("description").asStringOrThrow()).isEqualTo("Convert Amount Request Data");
        assertTypeIs(exchangeRate, "object");
        assertRequired((JsonArray) exchangeRate.values().get("required"), "fromCurrency", "toCurrency", "fromAmount");
        JsonObject exchangeRateProps = (JsonObject) exchangeRate.values().get("properties");
        assertTypeIs((JsonObject) exchangeRateProps.values().get("fromCurrency"), "string");
        assertTypeIs((JsonObject) exchangeRateProps.values().get("toCurrency"), "string");
        assertTypeIs((JsonObject) exchangeRateProps.values().get("fromAmount"), "number");
    }

    private static void assertRequired(FunctionParameters functionParameters, String... params) {
        JsonArray required = (JsonArray) functionParameters._additionalProperties().get("required");
        assertRequired(required, params);
    }

    private static void assertRequired(JsonArray required, String... params) {
        assertThat(required).isNotNull();
        assertThat(required.values()).hasSize(params.length);
        for (int i = 0; i < params.length; i++) {
            assertThat(required.values().get(i).asStringOrThrow()).isEqualTo(params[i]);
        }
    }

    private static void assertTypeIs(JsonObject jsonObject, String expectedType) {
        assertThat(jsonObject.values().get("type").asStringOrThrow()).isEqualTo(expectedType);
    }

    private void mockBeanMyTools() {
        MyTools bean = new MyTools(new MyService());
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"myTools"});
        when(beanFactory.getBean("myTools")).thenReturn(bean);
    }

    private void mockBeanAmountTools() {
        AmountTools bean = new AmountTools(new AmountService());
        when(beanFactory.getBeanDefinitionNames()).thenReturn(new String[]{"amountTools"});
        when(beanFactory.getBean("amountTools")).thenReturn(bean);
    }

    @NotNull
    private static FunctionParameters getFunctionParameters(List<ChatCompletionTool> toolSchemas, String expectedFunctionName) {
        assertNotNull(toolSchemas);
        FunctionDefinition functionDefinition = toolSchemas.stream().map(ChatCompletionTool::function)
                .filter(f -> f.name().equals(expectedFunctionName))
                .findFirst()
                .orElseThrow();
        return functionDefinition.parameters().orElseThrow();
    }


}