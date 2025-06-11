package dev.nikosg.poc.aitoolbox1.tooling.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.Tool;
import dev.nikosg.poc.aitoolbox1.tooling.annotations.ToolParam;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolDef;
import dev.nikosg.poc.aitoolbox1.tooling.dto.ToolFunctionDef;
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

    public MethodLevelToolRegistry(ListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        for (String beanName : beanFactory.getBeanDefinitionNames()) {
            Object bean = beanFactory.getBean(beanName);
            for (Method method : bean.getClass().getMethods()) {
                if (method.isAnnotationPresent(Tool.class)) {
                    Tool tool = method.getAnnotation(Tool.class);
                    String toolName = tool.name();
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
    public List<ToolDef> getToolSchemas() {
        List<ToolDef> schemas = new ArrayList<>();

        for (Map.Entry<String, Method> entry : toolMethods.entrySet()) {
            String name = entry.getKey();
            Method method = entry.getValue();
            Tool tool = method.getAnnotation(Tool.class);

            Map<String, Object> properties = new LinkedHashMap<>();
            Parameter[] parameters = method.getParameters();

            for (Parameter param : parameters) {
                ToolParam paramAnno = param.getAnnotation(ToolParam.class);
                String paramName = paramAnno.value();
                Map<String, Object> schema = Map.of("type", "string"); // For simplicity
                properties.put(paramName, schema);
            }

            Map<String, Object> parametersSchema = Map.of(
                    "type", "object",
                    "properties", properties,
                    "required", properties.keySet()
            );


            schemas.add(new ToolDef(new ToolFunctionDef(tool.name(), tool.description(), parametersSchema)));
        }

        return schemas;
    }

    @Override
    public String execute(String toolName, String jsonArgs) throws Exception {
        Method method = toolMethods.get(toolName);
        Object bean = beanInstances.get(toolName);
        if (method == null || bean == null) throw new RuntimeException("Unknown tool: " + toolName);

        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        JsonNode root = new ObjectMapper().readTree(jsonArgs);

        for (int i = 0; i < parameters.length; i++) {
            String name = parameters[i].getAnnotation(ToolParam.class).value();
            args[i] = root.get(name).asText(); // Simplified: assumes all are strings
        }

        Object result = method.invoke(bean, args);
        return result != null ? result.toString() : "";
    }
}
