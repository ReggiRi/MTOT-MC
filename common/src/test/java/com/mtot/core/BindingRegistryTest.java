package com.mtot.core;

import static org.junit.jupiter.api.Assertions.*;

import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BindingRegistryTest {

    private BindingRegistry registry;
    private final KeyCombination combo = KeyCombination.of(65, Modifier.CONTROL);

    @BeforeEach
    void setUp() {
        registry = new BindingRegistry();
    }

    @Test
    void shouldRegisterActionWithBinding() {
        registry.register("test:action", combo, () -> {});
        assertTrue(registry.hasAction("test:action"));
        assertEquals(combo, registry.getBinding("test:action").get());
    }

    @Test
    void shouldRegisterActionWithoutBinding() {
        registry.registerAction("test:action", () -> {});
        assertTrue(registry.getBinding("test:action").isEmpty());
    }

    @Test
    void shouldBindToRegisteredAction() {
        registry.registerAction("test:action", () -> {});
        registry.bind("test:action", combo);
        assertEquals(combo, registry.getBinding("test:action").get());
    }

    @Test
    void shouldThrowOnDuplicateRegistration() {
        registry.registerAction("test:action", () -> {});
        assertThrows(IllegalArgumentException.class,
            () -> registry.registerAction("test:action", () -> {})
        );
    }

    @Test
    void shouldThrowOnBindToUnregistered() {
        assertThrows(IllegalArgumentException.class,
            () -> registry.bind("unknown:action", combo)
        );
    }

    @Test
    void shouldResetToDefaults() {
        registry.register("test:action", combo, () -> {});
        KeyCombination newCombo = KeyCombination.of(66, Modifier.SHIFT);
        registry.bind("test:action", newCombo);
        registry.resetToDefaults();
        assertEquals(combo, registry.getBinding("test:action").get());
    }
}
