package com.mtot.api;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Тесты для {@link MTOTAPI}.
 *
 * @complexity Time: O(1), Memory: O(1)
 */
class MTOTAPITest {

    @Test
    void shouldReturnNullBeforeInitialization() {
        MTOTAPI.setInstance(null);
        assertNull(MTOTAPI.getInstance());
    }

    @Test
    void shouldReturnSetInstance() {
        IMTOTAPI mock = new IMTOTAPI() {
            @Override
            public void register(String id, KeyCombination combo, Runnable action) {}

            @Override
            public void registerAction(String id, Runnable action) {}

            @Override
            public void bind(String id, KeyCombination combo) {}

            @Override
            public Optional<KeyCombination> getBinding(String id) {
                return Optional.empty();
            }
        };
        MTOTAPI.setInstance(mock);
        assertSame(mock, MTOTAPI.getInstance());
        MTOTAPI.setInstance(null);
    }
}
