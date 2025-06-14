package dev.nikosg.poc.aitoolbox1.tooling.schema;

import java.util.Map;

public interface SchemaGeneratorService {
    Map<String, Object> generateForClass(Class<?> clazz);
}
