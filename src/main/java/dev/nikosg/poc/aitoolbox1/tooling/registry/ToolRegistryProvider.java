package dev.nikosg.poc.aitoolbox1.tooling.registry;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ToolRegistryProvider {

    private final List<ToolRegistry> registries;

    public ToolRegistryProvider(List<ToolRegistry> registries) {
        this.registries = registries;
    }

    public ToolRegistry provide(ToolRegistryType type) {
        return registries.stream()
                .filter(r -> r.getType().equals(type))
                .findFirst()
                .orElseThrow();
    }
}
