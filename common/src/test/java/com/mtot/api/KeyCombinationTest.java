package com.mtot.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Тесты для {@link KeyCombination}.
 *
 * @complexity Time: O(1), Memory: O(1)
 */
class KeyCombinationTest {

    @Test
    void shouldCreateWithoutModifiers() {
        KeyCombination combo = KeyCombination.of(65);
        assertEquals(65, combo.getKeyCode());
        assertTrue(combo.getModifiers().isEmpty());
    }

    @Test
    void shouldCreateWithMultipleModifiers() {
        KeyCombination combo = KeyCombination.of(
            65, Modifier.CONTROL, Modifier.SHIFT
        );
        assertEquals(2, combo.getModifiers().size());
    }

    @Test
    void shouldReturnUnmodifiableModifiers() {
        KeyCombination combo = KeyCombination.of(65, Modifier.CONTROL);
        assertThrows(UnsupportedOperationException.class,
            () -> combo.getModifiers().add(Modifier.SHIFT)
        );
    }

    @Test
    void shouldBeEqualToSameCombination() {
        KeyCombination a = KeyCombination.of(65, Modifier.CONTROL);
        KeyCombination b = KeyCombination.of(65, Modifier.CONTROL);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldNotBeEqualToDifferentKeyCode() {
        assertNotEquals(
            KeyCombination.of(65, Modifier.CONTROL),
            KeyCombination.of(66, Modifier.CONTROL)
        );
    }

    @Test
    void shouldNotBeEqualToDifferentModifiers() {
        assertNotEquals(
            KeyCombination.of(65, Modifier.CONTROL),
            KeyCombination.of(65, Modifier.SHIFT)
        );
    }

    @Test
    void shouldIgnoreModifierOrderForEquality() {
        assertEquals(
            KeyCombination.of(65, Modifier.CONTROL, Modifier.SHIFT),
            KeyCombination.of(65, Modifier.SHIFT, Modifier.CONTROL)
        );
    }

    @Test
    void shouldProduceDisplayName() {
        String display = KeyCombination.of(65, Modifier.CONTROL)
            .getDisplayName();
        assertTrue(display.contains("Ctrl"));
        assertTrue(display.contains("Key 65"));
    }
}
