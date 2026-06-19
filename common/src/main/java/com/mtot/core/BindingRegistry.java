package com.mtot.core;

import com.mtot.api.KeyCombination;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Реестр действий и их привязок к комбинациям клавиш.
 *
 * <p>Хранит три набора:
 * <ul>
 *   <li>{@code actions} — зарегистрированные действия (Runnable)</li>
 *   <li>{@code bindings} — текущие привязки пользователя</li>
 *   <li>{@code fallbacks} — привязки по умолчанию (для сброса)</li>
 * </ul>
 *
 * @rationale Разделение действий и привязок позволяет регистрировать действия
 *     без привязки (пользователь назначит через GUI) и отдельно управлять ими.
 *
 * @complexity Все операции: Time: O(1), Memory: O(1)
 */
public class BindingRegistry {

    private final Map<String, Runnable> actions = new HashMap<>();
    private final Map<String, KeyCombination> bindings = new HashMap<>();
    private final Map<String, KeyCombination> fallbacks = new HashMap<>();

    /**
     * Регистрирует действие с привязкой к комбинации.
     *
     * @param id     уникальный идентификатор (формат: "mod:action")
     * @param combo  комбинация клавиш
     * @param action выполняемое действие
     * @throws IllegalArgumentException если id уже зарегистрирован
     */
    public void register(String id, KeyCombination combo, Runnable action) {
        if (actions.containsKey(id)) {
            throw new IllegalArgumentException(
                "Action already registered: " + id
            );
        }
        actions.put(id, action);
        bindings.put(id, combo);
        fallbacks.putIfAbsent(id, combo);
    }

    /**
     * Регистрирует действие без привязки.
     *
     * @param id     уникальный идентификатор (формат: "mod:action")
     * @param action выполняемое действие
     * @throws IllegalArgumentException если id уже зарегистрирован
     */
    public void registerAction(String id, Runnable action) {
        if (actions.containsKey(id)) {
            throw new IllegalArgumentException(
                "Action already registered: " + id
            );
        }
        actions.put(id, action);
    }

    /**
     * Привязывает комбинацию к зарегистрированному действию.
     *
     * @param id    идентификатор действия
     * @param combo комбинация клавиш
     * @throws IllegalArgumentException если id не зарегистрирован
     */
    public void bind(String id, KeyCombination combo) {
        if (!actions.containsKey(id)) {
            throw new IllegalArgumentException(
                "Action not registered: " + id
            );
        }
        bindings.put(id, combo);
        fallbacks.putIfAbsent(id, combo);
    }

    /**
     * Возвращает привязку для действия.
     *
     * @param id идентификатор действия
     * @return комбинация или {@code Optional.empty()}, если не назначена
     */
    public Optional<KeyCombination> getBinding(String id) {
        return Optional.ofNullable(bindings.get(id));
    }

    /**
     * Возвращает действие по идентификатору.
     *
     * @param id идентификатор действия
     * @return {@link Runnable} или {@code Optional.empty()}, если не зарегистрировано
     */
    public Optional<Runnable> getAction(String id) {
        return Optional.ofNullable(actions.get(id));
    }

    /**
     * Возвращает {@code true}, если действие зарегистрировано.
     *
     * @param id идентификатор действия
     * @return true если действие зарегистрировано
     */
    public boolean hasAction(String id) {
        return actions.containsKey(id);
    }

    /**
     * Возвращает неизменяемую копию всех текущих привязок.
     *
     * @return мапа id → комбинация
     */
    public Map<String, KeyCombination> getAllBindings() {
        return Collections.unmodifiableMap(bindings);
    }

    /**
     * Сбрасывает все привязки к значениям по умолчанию.
     */
    public void resetToDefaults() {
        bindings.clear();
        bindings.putAll(fallbacks);
    }

    /**
     * Возвращает неизменяемую копию всех зарегистрированных действий.
     *
     * @return мапа id → действие
     */
    public Map<String, Runnable> getAllActions() {
        return Collections.unmodifiableMap(actions);
    }
}
