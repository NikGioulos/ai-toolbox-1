package dev.nikosg.poc.aitoolbox1.tooling.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FunctionSchemaGeneratorService implements SchemaGeneratorService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> generateForClass(Class<?> clazz) {
        return generateSchemaForType(clazz, clazz);
    }

    private Map<String, Object> generateSchemaForType(Class<?> type, Type genericType) {
        if (type == String.class) {
            return Map.of("type", "string");
        }
        if (type == UUID.class) {
            return Map.of("type", "string", "format", "uuid");
        }
        if (type == int.class || type == Integer.class || type == long.class || type == Long.class) {
            return Map.of("type", "integer");
        }
        if (type == boolean.class || type == Boolean.class) {
            return Map.of("type", "boolean");
        }
        if (type == double.class || type == Double.class || type == BigDecimal.class) {
            return Map.of("type", "number");
        }
        if (type == LocalDate.class) {
            return Map.of("type", "string", "format", "date");
        }
        if (type == LocalDateTime.class || type == Instant.class || type == ZonedDateTime.class) {
            return Map.of("type", "string", "format", "date-time");
        }
        if (type.isEnum()) {
            List<String> enumValues = Arrays.stream(type.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.toList());
            return Map.of("type", "string", "enum", enumValues);
        }
        if (Collection.class.isAssignableFrom(type)) {
            return Map.of(
                    "type", "array",
                    "items", handleCollectionsType(genericType)
            );
        }
        if (type.isArray()) {
            return Map.of(
                    "type", "array",
                    "items", generateSchemaForType(type.getComponentType(), type.getComponentType())
            );
        }

        // POJO object
        Map<String, Object> properties = new LinkedHashMap<>();
        List<String> required = new ArrayList<>();

        for (Field field : type.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (field.isAnnotationPresent(JsonBackReference.class)) continue;

            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            properties.put(fieldName, generateSchemaForType(fieldType, field.getGenericType()));
            required.add(fieldName);
        }

        return Map.of(
                "type", "object",
                "properties", properties,
                "required", required
        );
    }

    private Map<String, Object> handleCollectionsType(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;
            Type[] typeArgs = pType.getActualTypeArguments();
            if (typeArgs != null && typeArgs.length == 1) {
                Type itemType = typeArgs[0];
                if (itemType instanceof Class<?>) {
                    return generateSchemaForType((Class<?>) itemType, itemType);
                } else if (itemType instanceof ParameterizedType) {
                    Type raw = ((ParameterizedType) itemType).getRawType();
                    if (raw instanceof Class<?>) {
                        return generateSchemaForType((Class<?>) raw, itemType);
                    }
                }
            }
        } else {
            // Default to generic object if no type information.
            return Map.of("type", "object");
        }

        return Map.of("type", "string"); //fallback, should never reach this line
    }

    public String generateJsonSchemaAsString(Class<?> clazz) throws Exception {
        Map<String, Object> schema = generateForClass(clazz);
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
    }
}

