package com.mtot.core;

import com.mtot.api.IMTOTAPI;
import com.mtot.api.KeyCombination;
import com.mtot.api.MTOTAPI;
import java.util.Optional;

/**
 * Синглтон-ядро мода. Композирует {@link BindingRegistry} и {@link KeyStateTracker}.
 *
 * <p>Вызов {@link #tick()} выполняется платформенным кодом каждый клиентский тик
 * (20 раз/сек). При детекте отпускания комбинации выполняется соответствующее действие.
 *
 * @rationale Синглтон обеспечивает единую точку доступа из платформенных обёрток
 *     и API для других модов. Композиция вместо наследования — гибкость тестирования.
 *
 * @complexity tick(): Time: O(n), Memory: O(1), где n — число зарегистрированных привязок
 */
public class MTOTManager implements IMTOTAPI {

    private static final MTOTManager INSTANCE = new MTOTManager();

    private final BindingRegistry registry;
    private final KeyStateTracker tracker;

    MTOTManager() {
        this.registry = new BindingRegistry();
        this.tracker = new KeyStateTracker();
        MTOTAPI.setInstance(this);
    }

    /**
     * Возвращает единственный экземпляр менеджера.
     *
     * @return экземпляр {@link MTOTManager}
     */
    public static MTOTManager getInstance() {
        return INSTANCE;
    }

    /**
     * Обновляет состояние клавиши (вызывается платформенным кодом).
     *
     * @param keyCode GLFW код клавиши
     * @param pressed {@code true} если нажата
     */
    public void setKeyState(int keyCode, boolean pressed) {
        tracker.setKeyState(keyCode, pressed);
    }

    /**
     * Выполняется каждый клиентский тик. Проверяет все привязки и запускает
     * действия для отпущенных комбинаций.
     */
    public void tick() {
        for (var entry : registry.getAllBindings().entrySet()) {
            if (tracker.wasPressed(entry.getValue())) {
                registry.getAction(entry.getKey())
                    .ifPresent(Runnable::run);
            }
        }
    }

    @Override
    public void register(String id, KeyCombination combo, Runnable action) {
        registry.register(id, combo, action);
    }

    @Override
    public void registerAction(String id, Runnable action) {
        registry.registerAction(id, action);
    }

    @Override
    public void bind(String id, KeyCombination combo) {
        registry.bind(id, combo);
    }

    @Override
    public Optional<KeyCombination> getBinding(String id) {
        return registry.getBinding(id);
    }

    /**
     * Сбрасывает все привязки к значениям по умолчанию.
     */
    public void resetToDefaults() {
        registry.resetToDefaults();
    }
}
