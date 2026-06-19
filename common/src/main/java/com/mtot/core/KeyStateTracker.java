package com.mtot.core;

import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Отслеживает состояния клавиш и детектирует нажатия комбинаций.
 *
 * <p>Платформенный код вызывает {@link #setKeyState(int, boolean)} при каждом
 * событии клавиши. {@link #wasPressed(KeyCombination)} детектирует момент
 * отпускания комбинации (release event) — аналог ванильного KeyBinding.
 *
 * @rationale Храним поникеальные состояния и вычисляем комбинации на лету,
 *     чтобы не дублировать данные. Модификаторы маппятся на обе клавиши
 *     (левую и правую) для корректной работы с разными раскладками.
 *
 * @complexity Time: O(1) на клавишу, O(m) на комбинацию (m — число модификаторов)
 */
public class KeyStateTracker {

    private static final Map<Modifier, int[]> MODIFIER_KEY_CODES = new EnumMap<>(Modifier.class);
    static {
        MODIFIER_KEY_CODES.put(Modifier.SHIFT, new int[]{340, 344});
        MODIFIER_KEY_CODES.put(Modifier.CONTROL, new int[]{341, 345});
        MODIFIER_KEY_CODES.put(Modifier.ALT, new int[]{342, 346});
    }

    private final Map<Integer, Boolean> currentKeyStates = new HashMap<>();
    private final Map<KeyCombination, Boolean> previousComboStates = new HashMap<>();

    /**
     * Обновляет состояние отдельной клавиши.
     *
     * @param keyCode GLFW код клавиши
     * @param pressed {@code true} если нажата
     */
    public void setKeyState(int keyCode, boolean pressed) {
        currentKeyStates.put(keyCode, pressed);
    }

    /**
     * Проверяет, нажата ли комбинация в данный момент.
     *
     * @param combo комбинация клавиш
     * @return {@code true} если все клавиши комбинации нажаты
     */
    public boolean isPressed(KeyCombination combo) {
        if (!currentKeyStates.getOrDefault(combo.getKeyCode(), false)) {
            return false;
        }
        for (Modifier modifier : combo.getModifiers()) {
            if (!isModifierPressed(modifier)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет, была ли комбинация отпущена с прошлого вызова.
     *
     * <p>Детектирует переход pressed → released. Аналог {@code wasPressed()}
     * в ванильном KeyBinding.
     *
     * @param combo комбинация клавиш
     * @return {@code true} если комбинация была нажата в прошлый раз, а теперь нет
     */
    public boolean wasPressed(KeyCombination combo) {
        boolean currentlyPressed = isPressed(combo);
        boolean previouslyPressed = previousComboStates.getOrDefault(combo, false);
        previousComboStates.put(combo, currentlyPressed);
        return previouslyPressed && !currentlyPressed;
    }

    private boolean isModifierPressed(Modifier modifier) {
        int[] keyCodes = MODIFIER_KEY_CODES.get(modifier);
        for (int keyCode : keyCodes) {
            if (currentKeyStates.getOrDefault(keyCode, false)) {
                return true;
            }
        }
        return false;
    }
}
