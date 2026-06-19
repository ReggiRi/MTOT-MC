package com.mtot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mtot.api.KeyCombination;
import com.mtot.api.Modifier;
import com.mtot.core.BindingRegistry;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Конфигурация мода — сериализация/десериализация привязок в JSON.
 *
 * <p>Формат файла: {@code config/mtot.json}.
 * Использует GSON для преобразования между {@link BindingRegistry} и JSON.
 *
 * @rationale Отдельная модель ConfigData изолирует формат файла от внутреннего
 *     представления. Это позволяет мигрировать формат без изменения ядра.
 *
 * @complexity Time: O(n), Memory: O(n), где n — число привязок
 */
public class MTOTConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Модель JSON-файла конфигурации.
     */
    public static class ConfigData {
        int version = 1;
        Map<String, BindingEntry> bindings = new HashMap<>();
        Map<String, BindingEntry> fallback = new HashMap<>();
    }

    /**
     * Запись привязки в JSON.
     */
    public static class BindingEntry {
        int key;
        List<String> modifiers;

        BindingEntry() {}

        BindingEntry(KeyCombination combo) {
            this.key = combo.getKeyCode();
            this.modifiers = combo.getModifiers().stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        }
    }

    private MTOTConfig() {}

    /**
     * Сериализует реестр в JSON-строку.
     *
     * @param registry реестр привязок
     * @return JSON-строка
     */
    public static String toJson(BindingRegistry registry) {
        ConfigData data = new ConfigData();
        for (var entry : registry.getAllBindings().entrySet()) {
            data.bindings.put(entry.getKey(), new BindingEntry(entry.getValue()));
        }
        for (var entry : registry.getAllFallbacks().entrySet()) {
            data.fallback.put(entry.getKey(), new BindingEntry(entry.getValue()));
        }
        return GSON.toJson(data);
    }

    /**
     * Десериализует JSON-строку в реестр.
     *
     * @param json     JSON-строка
     * @param registry реестр для заполнения
     */
    public static void fromJson(String json, BindingRegistry registry) {
        ConfigData data = GSON.fromJson(json, ConfigData.class);
        for (var entry : data.bindings.entrySet()) {
            if (registry.hasAction(entry.getKey())) {
                registry.bind(entry.getKey(), toKeyCombination(entry.getValue()));
            }
        }
    }

    private static KeyCombination toKeyCombination(BindingEntry entry) {
        List<Modifier> mods = entry.modifiers == null
            ? Collections.emptyList()
            : entry.modifiers.stream()
                .map(Modifier::valueOf)
                .collect(Collectors.toList());
        return KeyCombination.of(entry.key, mods.toArray(new Modifier[0]));
    }
}
