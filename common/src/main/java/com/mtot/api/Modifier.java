package com.mtot.api;

/**
 * Модификаторы клавиш для комбинаций.
 *
 * <p>Соответствуют GLFW модификаторам и используются совместно с {@link KeyCombination}.
 *
 * @rationale Используется enum вместо int констант для типобезопасности
 *     и читаемости API. Каждое значение хранит GLFW mod-флаг для прямой
 *     передачи в GLFW функции.
 *
 * @complexity Time: O(1), Memory: O(1)
 */
public enum Modifier {

    SHIFT("Shift", 0x0001),
    CONTROL("Ctrl", 0x0002),
    ALT("Alt", 0x0004);

    private final String displayName;
    private final int glfwMod;

    Modifier(String displayName, int glfwMod) {
        this.displayName = displayName;
        this.glfwMod = glfwMod;
    }

    /**
     * Возвращает отображаемое имя для GUI.
     *
     * @return человекочитаемое название модификатора
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Возвращает GLFW mod-флаг для этого модификатора.
     *
     * @return значение GLFW_MOD_* (0x0001, 0x0002, 0x0004)
     */
    public int getGlfwMod() {
        return glfwMod;
    }
}
