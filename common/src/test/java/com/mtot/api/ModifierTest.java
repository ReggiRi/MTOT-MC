package com.mtot.api;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Тесты для {@link Modifier}.
 *
 * @complexity Time: O(1), Memory: O(1)
 */
class ModifierTest {

    @Test
    void shouldHaveCorrectValues() {
        assertEquals(3, Modifier.values().length);
        assertNotNull(Modifier.valueOf("SHIFT"));
        assertNotNull(Modifier.valueOf("CONTROL"));
        assertNotNull(Modifier.valueOf("ALT"));
    }

    @Test
    void shouldReturnCorrectDisplayNames() {
        assertEquals("Shift", Modifier.SHIFT.getDisplayName());
        assertEquals("Ctrl", Modifier.CONTROL.getDisplayName());
        assertEquals("Alt", Modifier.ALT.getDisplayName());
    }

    @Test
    void shouldReturnCorrectGlfwMods() {
        assertEquals(0x0001, Modifier.SHIFT.getGlfwMod());
        assertEquals(0x0002, Modifier.CONTROL.getGlfwMod());
        assertEquals(0x0004, Modifier.ALT.getGlfwMod());
    }

    @Test
    void shouldGlfwModsBeUnique() {
        assertNotEquals(
            Modifier.SHIFT.getGlfwMod(),
            Modifier.CONTROL.getGlfwMod()
        );
        assertNotEquals(
            Modifier.CONTROL.getGlfwMod(),
            Modifier.ALT.getGlfwMod()
        );
    }
}
