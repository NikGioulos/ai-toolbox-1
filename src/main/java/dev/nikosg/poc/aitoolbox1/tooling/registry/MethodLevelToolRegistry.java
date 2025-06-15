package dev.nikosg.poc.aitoolbox1.tooling.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.openai.core.JsonValue;
import com.openai.models.FunctionDefinition;
import com.openai.models.FunctionParameters;
import com.openai.models.chat.completions.ChatCompletionTool;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.Tool;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.ToolParam;
import dev.nikosg.poc.aitoolbox1.tooling.schema.SchemaGeneratorService;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Component
public class MethodLevelToolRegistry implements ToolRegistry {

    private final Map<String, Method> toolMethods = new HashMap<>();
    private final Map<String, Object> beanInstances = new HashMap<>();
    private final ListableBeanFactory beanFactory;
    private final SchemaGeneratorService schemaGeneratorService;
    private final ObjectMapper mapper;

    public MethodLevelToolRegistry(ListableBeanFactory beanFactory, SchemaGeneratorService schemaGeneratorService) {
        this.beanFactory = beanFactory;
        this.schemaGeneratorService = schemaGeneratorService;
        this.mapper = initMapper();
    }

    private ObjectMapper initMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            if(!beanName.contains("Tool")) continue;
            Object bean = beanFactory.getBean(beanName);
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    Tool tool = method.getAnnotation(Tool.class);
                    String toolName = tool.name() == null ? method.getName() : tool.name().replace(" ", "_");
                    toolMethods.put(toolName, method);
                    beanInstances.put(toolName, bean);
                }
            }
        }
    }

    @Override
    public ToolRegistryType getType() {
        return ToolRegistryType.METHOD;
    }

    @Override
    public List<ChatCompletionTool> getToolSchemas() {
        List<ChatCompletionTool> schemas = new ArrayList<>();

        for (Map.Entry<String, Method> entry : toolMethods.entrySet()) {
            String name = entry.getKey();
            Method method = entry.getValue();
            if(!method.isAnnotationPresent(Tool.class)) {
                continue;
            }
            Tool tool = method.getAnnotation(Tool.class);

            Map<String, Object> properties = new LinkedHashMap<>();
            for (Parameter param : method.getParameters()) {
                ToolParam paramAnno = param.getAnnotation(ToolParam.class);
                Map<String, Object> paramSchema = schemaGeneratorService.generateForClass(param.getType());
                Map<String, Object> schema = new LinkedHashMap<>();
                schema.put("description", paramAnno.value());
                schema.putAll(paramSchema);
                properties.put(param.getName(), schema);
            }

            Map<String, Object> parametersSchema = Map.of(
                    "type", "object",
                    "properties", properties,
                    "required", properties.keySet()
            );

            schemas.add(
                    ChatCompletionTool.builder()
                            .type(JsonValue.from("function"))
                            .function(FunctionDefinition.builder()
                                    .name(name)
                                    .description(tool.description())
                                    .parameters(toFunctionParameters(parametersSchema))
                                    .build())
                            .build()
            );
        }

        return schemas;
    }

    private FunctionParameters toFunctionParameters(Map<String, Object> parameters) {
        FunctionParameters.Builder builder = FunctionParameters.builder();
        parameters.forEach((key, value) -> builder.putAdditionalProperty(key, JsonValue.from(value)));
        return builder.build();
    }

    @Override
    public String execute(String toolName, String argumentsJson) throws Exception {
        Method method = toolMethods.get(toolName);
        Object bean = beanInstances.get(toolName);
        if (method == null || bean == null) throw new RuntimeException("Unknown tool: " + toolName);

        return callMethod(method, bean, argumentsJson);
    }

    String callMethod(Method method, Object bean, String argumentsJson) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        Map<String, Object> jsonMap = mapper.readValue(argumentsJson, Map.class);
        for (int i = 0; i < parameters.length; i++) {
            String paramName = parameters[i].getName(); // Might need parameter name discovery
            Class<?> paramType = parameters[i].getType();
            Object rawValue = jsonMap.get(paramName);
            args[i] = mapper.convertValue(rawValue, paramType);
        }

        Object result = method.invoke(bean, args);
        return result != null ? result.toString() : "";
    }

}
