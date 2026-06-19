package com.mtot.api;

import java.util.Optional;

/**
 * Публичное API для регистрации и управления комбинациями клавиш.
 *
 * <p>Другие моды получают доступ через {@link MTOTAPI#getInstance()}.
 *
 * @rationale Интерфейс обеспечивает слабую связанность между API и реализацией.
 *     Другие моды зависят только от интерфейса, реализация может меняться.
 *
 * @complexity Все методы: Time: O(1), Memory: O(1)
 */
public interface IMTOTAPI {

    /**
     * Регистрирует действие с привязкой к комбинации клавиш.
     *
     * @param id     уникальный идентификатор (формат: "mod:action")
     * @param combo  комбинация клавиш
     * @param action действие, выполняемое при нажатии
     * @throws IllegalArgumentException если id уже зарегистрирован
     */
    void register(String id, KeyCombination combo, Runnable action);

    /**
     * Регистрирует действие без привязки (пользователь назначит через GUI).
     *
     * @param id     уникальный идентификатор (формат: "mod:action")
     * @param action действие, выполняемое при нажатии
     * @throws IllegalArgumentException если id уже зарегистрирован
     */
    void registerAction(String id, Runnable action);

    /**
     * Привязывает комбинацию к уже зарегистрированному действию.
     *
     * @param id    идентификатор действия
     * @param combo комбинация клавиш
     * @throws IllegalArgumentException если id не зарегистрирован
     */
    void bind(String id, KeyCombination combo);

    /**
     * Получает текущую привязку для действия.
     *
     * @param id идентификатор действия
     * @return комбинация клавиш или {@code Optional.empty()} если не назначена
     */
    Optional<KeyCombination> getBinding(String id);
}
