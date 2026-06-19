package com.mtot.actions;

import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import com.mtot.core.BindingRegistry;

/**
 * Встроенные действия мода.
 *
 * <p>Регистрирует три действия по умолчанию:
 * <ul>
 *   <li>{@code mtot:insert_newline} — Shift + Enter, вставить \n в чат</li>
 *   <li>{@code mtot:clear_chat} — Ctrl + L, очистить историю чата</li>
 *   <li>{@code mtot:repeat_last} — Ctrl + R, повторить последнее сообщение</li>
 * </ul>
 *
 * @rationale Вынесены в отдельный класс для изоляции конфигурации умолчаний.
 *     Платформенный код передаёт конкретные реализации runnable при старте.
 *
 * @complexity Time: O(1), Memory: O(1)
 */
public final class BuiltinActions {

    public static final String INSERT_NEWLINE = "mtot:insert_newline";
    public static final String CLEAR_CHAT = "mtot:clear_chat";
    public static final String REPEAT_LAST = "mtot:repeat_last";

    private static final KeyCombination SHIFT_ENTER = KeyCombination.of(257, Modifier.SHIFT);
    private static final KeyCombination CTRL_L = KeyCombination.of(76, Modifier.CONTROL);
    private static final KeyCombination CTRL_R = KeyCombination.of(82, Modifier.CONTROL);

    private BuiltinActions() {}

    /**
     * Регистрирует встроенные действия в реестре.
     *
     * @param registry      реестр привязок
     * @param insertNewline действие для вставки новой строки
     * @param clearChat     действие для очистки чата
     * @param repeatLast    действие для повтора последнего сообщения
     */
    public static void init(BindingRegistry registry,
                            Runnable insertNewline,
                            Runnable clearChat,
                            Runnable repeatLast) {
        registry.register(INSERT_NEWLINE, SHIFT_ENTER, insertNewline);
        registry.register(CLEAR_CHAT, CTRL_L, clearChat);
        registry.register(REPEAT_LAST, CTRL_R, repeatLast);
    }
}
