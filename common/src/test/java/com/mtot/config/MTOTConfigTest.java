package com.mtot.config;

import static org.junit.jupiter.api.Assertions.*;

import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import com.mtot.core.BindingRegistry;
import org.junit.jupiter.api.Test;

class MTOTConfigTest {

    @Test
    void shouldSerializeBindings() {
        BindingRegistry registry = new BindingRegistry();
        registry.register("test:action",
            KeyCombination.of(69, Modifier.CONTROL), () -> {}
        );
        String json = MTOTConfig.toJson(registry);
        assertTrue(json.contains("test:action"));
        assertTrue(json.contains("69"));
        assertTrue(json.contains("CONTROL"));
    }

    @Test
    void shouldDeserializeBindings() {
        String json = """
            {"version":1,"bindings":{"test:action":{"key":69,"modifiers":["CONTROL"]}},"fallback":{}}
            """;
        BindingRegistry registry = new BindingRegistry();
        registry.registerAction("test:action", () -> {});
        MTOTConfig.fromJson(json, registry);
        assertTrue(registry.getBinding("test:action").isPresent());
        assertEquals(69, registry.getBinding("test:action").get().getKeyCode());
    }

    @Test
    void shouldRoundTrip() {
        BindingRegistry original = new BindingRegistry();
        original.register("test:action",
            KeyCombination.of(69, Modifier.CONTROL, Modifier.SHIFT), () -> {}
        );
        original.register("test:other",
            KeyCombination.of(257, Modifier.ALT), () -> {}
        );
        String json = MTOTConfig.toJson(original);
        BindingRegistry restored = new BindingRegistry();
        restored.registerAction("test:action", () -> {});
        restored.registerAction("test:other", () -> {});
        MTOTConfig.fromJson(json, restored);
        assertTrue(restored.getBinding("test:action").isPresent());
        assertTrue(restored.getBinding("test:other").isPresent());
    }
}
