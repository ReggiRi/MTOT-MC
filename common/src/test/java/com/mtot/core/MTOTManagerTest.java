package com.mtot.core;

import static org.junit.jupiter.api.Assertions.*;

import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MTOTManagerTest {

    private MTOTManager manager;
    private final KeyCombination ctrlE = KeyCombination.of(69, Modifier.CONTROL);

    @BeforeEach
    void setUp() {
        manager = new MTOTManager();
    }

    @Test
    void shouldRegisterAction() {
        manager.registerAction("test:action", () -> {});
        assertTrue(manager.getBinding("test:action").isEmpty());
    }

    @Test
    void shouldRegisterWithBinding() {
        manager.register("test:action", ctrlE, () -> {});
        assertTrue(manager.getBinding("test:action").isPresent());
    }

    @Test
    void shouldExecuteActionOnRelease() {
        boolean[] executed = {false};
        manager.register("test:action", ctrlE, () -> executed[0] = true);
        manager.setKeyState(69, true);
        manager.setKeyState(341, true);
        manager.tick();
        assertFalse(executed[0]);
        manager.setKeyState(69, false);
        manager.tick();
        assertTrue(executed[0]);
    }

    @Test
    void shouldNotExecuteWhenKeysNotPressed() {
        boolean[] executed = {false};
        manager.register("test:action", ctrlE, () -> executed[0] = true);
        manager.tick();
        assertFalse(executed[0]);
    }

    @Test
    void shouldBindAndExecute() {
        boolean[] executed = {false};
        manager.registerAction("test:action", () -> executed[0] = true);
        manager.bind("test:action", ctrlE);
        manager.setKeyState(69, true);
        manager.setKeyState(341, true);
        manager.tick();
        manager.setKeyState(69, false);
        manager.tick();
        assertTrue(executed[0]);
    }
}
