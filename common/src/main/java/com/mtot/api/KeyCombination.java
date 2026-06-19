package com.mtot.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Неизменяемая комбинация клавиши и модификаторов.
 *
 * <p>Создаётся через статический фабричный метод {@link #of(int, Modifier...)}.
 * Гарантирует корректные {@code equals()} и {@code hashCode()} для использования
 * в качестве ключа мапы.
 *
 * @rationale Использован EnumSet для модификаторов — компактное представление
 *     с гарантированным порядком итерации. Фабричный метод вместо конструктора
 *     для гибкости будущих расширений.
 *
 * @complexity Time: O(1), Memory: O(1)
 */
public final class KeyCombination {

    private final int keyCode;
    private final Set<Modifier> modifiers;

    private KeyCombination(int keyCode, Set<Modifier> modifiers) {
        this.keyCode = keyCode;
        this.modifiers = Collections.unmodifiableSet(
            EnumSet.copyOf(modifiers)
        );
    }

    /**
     * Создаёт комбинацию из кода клавиши и опциональных модификаторов.
     *
     * @param keyCode  GLFW key code (например, {@code GLFW.GLFW_KEY_E})
     * @param modifiers модификаторы (SHIFT, CONTROL, ALT)
     * @return новая комбинация
     */
    public static KeyCombination of(int keyCode, Modifier... modifiers) {
        Set<Modifier> modifierSet = modifiers.length == 0
            ? Collections.emptySet()
            : EnumSet.copyOf(Arrays.asList(modifiers));
        return new KeyCombination(keyCode, modifierSet);
    }

    /**
     * Возвращает GLFW код клавиши.
     *
     * @return код клавиши GLFW_KEY_*
     */
    public int getKeyCode() {
        return keyCode;
    }

    /**
     * Возвращает неизменяемый набор модификаторов.
     *
     * @return модификаторы комбинации
     */
    public Set<Modifier> getModifiers() {
        return modifiers;
    }

    /**
     * Возвращает человекочитаемое представление для GUI.
     *
     * <p>Формат: "Ctrl + Shift + E" (модификаторы в порядке Enum, затем клавиша).
     *
     * @return отображаемое имя комбинации
     */
    public String getDisplayName() {
        String modifierPart = modifiers.stream()
            .map(Modifier::getDisplayName)
            .collect(Collectors.joining(" + "));
        String keyPart =  "Key " + keyCode;
        return modifierPart.isEmpty()
            ? keyPart
            : modifierPart + " + " + keyPart;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof KeyCombination that)) {
            return false;
        }
        return keyCode == that.keyCode && modifiers.equals(that.modifiers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCode, modifiers);
    }

    @Override
    public String toString() {
        return "KeyCombination{keyCode=" + keyCode + ", modifiers=" + modifiers + "}";
    }
}
