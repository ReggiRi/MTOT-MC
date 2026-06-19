package com.mtot.actions;

import static org.junit.jupiter.api.Assertions.*;

import com.mtot.core.BindingRegistry;
import org.junit.jupiter.api.Test;

class BuiltinActionsTest {

    @Test
    void shouldHaveCorrectIds() {
        assertEquals("mtot:insert_newline", BuiltinActions.INSERT_NEWLINE);
        assertEquals("mtot:clear_chat", BuiltinActions.CLEAR_CHAT);
        assertEquals("mtot:repeat_last", BuiltinActions.REPEAT_LAST);
    }

    @Test
    void shouldRegisterAllActions() {
        BindingRegistry registry = new BindingRegistry();
        BuiltinActions.init(registry, () -> {}, () -> {}, () -> {});
        assertTrue(registry.hasAction(BuiltinActions.INSERT_NEWLINE));
        assertTrue(registry.hasAction(BuiltinActions.CLEAR_CHAT));
        assertTrue(registry.hasAction(BuiltinActions.REPEAT_LAST));
    }

    @Test
    void shouldRegisterWithBindings() {
        BindingRegistry registry = new BindingRegistry();
        BuiltinActions.init(registry, () -> {}, () -> {}, () -> {});
        assertTrue(registry.getBinding(BuiltinActions.INSERT_NEWLINE).isPresent());
        assertTrue(registry.getBinding(BuiltinActions.CLEAR_CHAT).isPresent());
        assertTrue(registry.getBinding(BuiltinActions.REPEAT_LAST).isPresent());
    }

    @Test
    void shouldExecuteProvidedActions() {
        boolean[] executed = {false};
        BindingRegistry registry = new BindingRegistry();
        BuiltinActions.init(registry,
            () -> executed[0] = true,
            () -> {},
            () -> {}
        );
        registry.getAction(BuiltinActions.INSERT_NEWLINE).get().run();
        assertTrue(executed[0]);
    }
}
