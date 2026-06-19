package com.mtot.core;

import static org.junit.jupiter.api.Assertions.*;

import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class KeyStateTrackerTest {

    private KeyStateTracker tracker;
    private KeyCombination ctrlE;
    private KeyCombination shiftEnter;

    @BeforeEach
    void setUp() {
        tracker = new KeyStateTracker();
        ctrlE = KeyCombination.of(69, Modifier.CONTROL);
        shiftEnter = KeyCombination.of(257, Modifier.SHIFT);
    }

    @Test
    void shouldNotBePressedInitially() {
        assertFalse(tracker.isPressed(ctrlE));
    }

    @Test
    void shouldDetectPressedCombo() {
        tracker.setKeyState(69, true);
        tracker.setKeyState(341, true);
        assertTrue(tracker.isPressed(ctrlE));
    }

    @Test
    void shouldNotBePressedWhenKeyUp() {
        tracker.setKeyState(69, true);
        tracker.setKeyState(341, true);
        tracker.setKeyState(69, false);
        assertFalse(tracker.isPressed(ctrlE));
    }

    @Test
    void shouldNotBePressedWhenModifierUp() {
        tracker.setKeyState(69, true);
        assertFalse(tracker.isPressed(ctrlE));
    }

    @Test
    void shouldDetectRightModifier() {
        tracker.setKeyState(69, true);
        tracker.setKeyState(345, true);
        assertTrue(tracker.isPressed(ctrlE));
    }

    @Test
    void shouldDetectRelease() {
        tracker.setKeyState(69, true);
        tracker.setKeyState(341, true);
        assertFalse(tracker.wasPressed(ctrlE));
        tracker.setKeyState(69, false);
        assertTrue(tracker.wasPressed(ctrlE));
    }

    @Test
    void shouldNotRepeatRelease() {
        tracker.setKeyState(69, true);
        tracker.setKeyState(341, true);
        tracker.wasPressed(ctrlE);
        tracker.setKeyState(69, false);
        tracker.wasPressed(ctrlE);
        assertFalse(tracker.wasPressed(ctrlE));
    }

    @Test
    void shouldHandleShiftEnter() {
        tracker.setKeyState(257, true);
        tracker.setKeyState(340, true);
        assertTrue(tracker.isPressed(shiftEnter));
    }
}
