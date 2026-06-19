package com.mtot.api;

/**
 * Статическая точка доступа к {@link IMTOTAPI}.
 *
 * <p>Другие моды получают экземпляр API через:
 * <pre>{@code
 * IMTOTAPI api = MTOTAPI.getInstance();
 * }</pre>
 *
 * @rationale Статический доступ через фасад позволяет избежать передачи
 *     экземпляра через конструкторы. Реализация подставляется при инициализации
 *     мода через {@link #setInstance(IMTOTAPI)}.
 *
 * @complexity Time: O(1), Memory: O(1)
 */
public final class MTOTAPI {

    private static volatile IMTOTAPI instance;

    private MTOTAPI() {
    }

    /**
     * Возвращает текущий экземпляр API.
     *
     * @return экземпляр {@link IMTOTAPI} или {@code null}, если не инициализирован
     */
    public static IMTOTAPI getInstance() {
        return instance;
    }

    /**
     * Устанавливает реализацию API (вызывается при старте мода).
     *
     * @param api реализация {@link IMTOTAPI}
     */
    public static void setInstance(IMTOTAPI api) {
        instance = api;
    }
}
