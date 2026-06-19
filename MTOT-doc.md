# More Than One Touch (MTOT)
## Техническая спецификация v1.0

---

## 1. Введение

### 1.1. Описание проекта
**More Than One Touch (MTOT)** — клиентский мод для Minecraft, реализующий систему **многоклавишных комбинаций** (горячих клавиш с модификаторами). Мод предоставляет API для других разработчиков и интуитивный GUI для конечных пользователей.

### 1.2. Ключевые особенности
- Поддержка комбинаций с модификаторами (`Shift`, `Ctrl`, `Alt`)
- Кастомный GUI для настройки привязок
- API для интеграции с другими модами
- Сохранение настроек в конфигурационный файл
- Поддержка всех актуальных лоадеров

### 1.3. Целевая аудитория
- **Игроки:** желающие использовать сложные комбинации клавиш
- **Разработчики модов:** нуждающиеся в системе горячих клавиш

---

## 2. Поддерживаемые платформы

### 2.1. Лоадеры и версии

| Лоадер | Версии Minecraft | Статус |
|--------|------------------|--------|
| **NeoForge** | 1.20.4, 1.21.1, 1.21.4 | ✅ Основной |
| **Forge** | 1.20.1 | ✅ LTS |
| **Fabric** | 1.20.1, 1.21.1, 1.21.4 | ✅ Основной |
| **Quilt** | 1.20.1+ | ⚠️ Через Fabric-совместимость |

### 2.2. Требования к среде
- **Java:** 17 (1.20.1) / 21 (1.21.4+)
- **ОС:** Windows, macOS, Linux
- **Клиент:** Только клиентская часть (не требует серверной установки)

---

## 3. Функциональные требования

### 3.1. Ядро системы (Core)

#### 3.1.1. Регистрация комбинаций

```java
public interface IMTOTAPI {
    /**
     * Регистрирует действие с привязкой к комбинации клавиш
     * @param id Уникальный идентификатор действия (например, "mymod:action")
     * @param combo Комбинация клавиш
     * @param action Действие, выполняемое при нажатии
     */
    void register(String id, KeyCombination combo, Runnable action);
    
    /**
     * Регистрирует действие без привязки (пользователь назначит сам)
     */
    void registerAction(String id, Runnable action);
    
    /**
     * Привязывает комбинацию к уже зарегистрированному действию
     */
    void bind(String id, KeyCombination combo);
    
    /**
     * Получает текущую привязку для действия
     */
    Optional<KeyCombination> getBinding(String id);
}
```

#### 3.1.2. Структура комбинации

```java
public class KeyCombination {
    private final int keyCode;           // GLFW.GLFW_KEY_*
    private final Set<Modifier> modifiers; // SHIFT, CONTROL, ALT
    
    // equals(), hashCode(), toString()
}

public enum Modifier {
    SHIFT(GLFW.GLFW_MOD_SHIFT),
    CONTROL(GLFW.GLFW_MOD_CONTROL),
    ALT(GLFW.GLFW_MOD_ALT);
}
```

#### 3.1.3. Событие нажатия
- Срабатывает **по отпусканию клавиши** (release event)
- Аналогично ванильному `KeyBinding`
- Проверка состояния 1 раз в клиентский тик (20 раз/сек)

### 3.2. Пользовательский интерфейс

#### 3.2.1. Вкладка в настройках управления
- Расположение: `Options → Controls → More Than One Touch`
- Отображение всех зарегистрированных действий

#### 3.2.2. Список привязок

| Действие | Комбинация | Кнопка |
|----------|-----------|--------|
| `mtot:insert_newline` | `Shift + Enter` | [Изменить] |
| `mymod:action` | `Ctrl + E` | [Изменить] |
| `othermod:do_something` | `Alt + X` | [Изменить] |

#### 3.2.3. Изменение комбинации
- Нажать кнопку `[Изменить]` → режим ожидания ввода
- Игрок нажимает желаемую комбинацию
- Проверка на конфликты с другими комбинациями
- Подтверждение или отмена

#### 3.2.4. Визуальная обратная связь
- Подсветка активной комбинации
- Индикация конфликтов (красный цвет)
- Подтверждение сохранения

### 3.3. Конфигурация

#### 3.3.1. Формат файла: `config/mtot.json`

```json
{
  "version": 1,
  "bindings": {
    "mtot:insert_newline": {
      "key": 257,
      "modifiers": ["SHIFT"]
    },
    "mymod:action": {
      "key": 69,
      "modifiers": ["CONTROL"]
    }
  },
  "fallback": {
    "mtot:insert_newline": {
      "key": 257,
      "modifiers": ["SHIFT"]
    }
  }
}
```

#### 3.3.2. Параметры конфигурации
- `version`: Версия формата (для миграции)
- `bindings`: Текущие привязки пользователя
- `fallback`: Значения по умолчанию (при сбросе)

### 3.4. Встроенные действия (по умолчанию)

| ID | Комбинация | Действие |
|----|------------|----------|
| `mtot:insert_newline` | `Shift + Enter` | Вставить `\n` в поле ввода чата |
| `mtot:clear_chat` | `Ctrl + L` | Очистить историю чата (локально) |
| `mtot:repeat_last` | `Ctrl + R` | Повторить последнее сообщение |

---

## 4. Техническая архитектура

### 4.1. Структура проекта (Multi-Loader)

```
MTOT/
├── common/                         # Общий код (95% логики)
│   ├── src/main/java/com/mtot/
│   │   ├── api/                    # Публичное API
│   │   │   ├── IMTOTAPI.java
│   │   │   ├── KeyCombination.java
│   │   │   └── Modifier.java
│   │   ├── core/                   # Ядро
│   │   │   ├── MTOTManager.java    # Синглтон
│   │   │   ├── BindingRegistry.java
│   │   │   └── KeyStateTracker.java
│   │   ├── gui/                    # UI (общий)
│   │   │   ├── MTOTControlsScreen.java
│   │   │   └── BindingEntryWidget.java
│   │   ├── config/                 # Конфигурация
│   │   │   └── MTOTConfig.java
│   │   └── actions/                # Встроенные действия
│   │       └── BuiltinActions.java
│   └── resources/
│       └── mtot-common.accesswidener
│
├── fabric/                         # Fabric реализация
│   ├── src/main/java/com/mtot/fabric/
│   │   ├── MTOTFabricClient.java
│   │   └── FabricKeyManager.java
│   └── src/main/resources/
│       └── fabric.mod.json
│
├── neoforge/                       # NeoForge реализация
│   ├── src/main/java/com/mtot/neoforge/
│   │   ├── MTOTNeoForgeClient.java
│   │   └── NeoForgeKeyManager.java
│   └── src/main/resources/
│       └── META-INF/neoforge.mods.toml
│
├── forge/                          # Forge 1.20.1 реализация
│   ├── src/main/java/com/mtot/forge/
│   │   ├── MTOTForgeClient.java
│   │   └── ForgeKeyManager.java
│   └── src/main/resources/
│       └── META-INF/mods.toml
│
├── build.gradle                    # Корневой билд
├── settings.gradle
└── gradle.properties
```

### 4.2. Ключевые компоненты

#### 4.2.1. MTOTManager (Синглтон)

```java
public class MTOTManager {
    private static final MTOTManager INSTANCE = new MTOTManager();
    private final Map<String, Runnable> actions = new HashMap<>();
    private final Map<String, KeyCombination> bindings = new HashMap<>();
    private final Map<KeyCombination, Boolean> previousStates = new HashMap<>();
    
    public static MTOTManager getInstance() { return INSTANCE; }
    
    public void registerAction(String id, Runnable action) { ... }
    public void bind(String id, KeyCombination combo) { ... }
    public void tick() { ... } // Проверка состояний
}
```

#### 4.2.2. KeyStateTracker (Отслеживание)

```java
public class KeyStateTracker {
    private final Long2BooleanMap currentState = new Long2BooleanOpenHashMap();
    
    public boolean isPressed(KeyCombination combo, long window) {
        // Проверка всех клавиш в комбинации
    }
    
    public boolean wasPressed(KeyCombination combo) {
        // Переход из нажатого в отпущенное
    }
}
```

### 4.3. Платформенный код (Минимальный)

#### 4.3.1. Fabric

```java
@Mod("mtot")
public class MTOTFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> 
            MTOTManager.getInstance().tick()
        );
    }
}
```

#### 4.3.2. NeoForge

```java
@Mod("mtot")
public class MTOTNeoForgeClient {
    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        MTOTManager.getInstance().tick();
    }
}
```

### 4.4. Зависимости

| Зависимость | Версия | Назначение |
|-------------|--------|------------|
| **Fabric API** | 0.100.0+ | Fabric-специфичный код |
| **NeoForge** | 20.4.100+ | NeoForge-специфичный код |
| **Forge** | 47.0.0+ | Forge-специфичный код |
| **GSON** | 2.10.1 | Конфигурация (JSON) |
| **SLF4J** | 2.0.0+ | Логирование |

---

## 5. API документация

### 5.1. Получение доступа к API

```java
// В твоём моде
public class MyMod {
    public void onInitialize() {
        IMTOTAPI api = MTOTAPI.getInstance();
        
        // Регистрируем действие без привязки
        api.registerAction("mymod:do_something", () -> {
            // Твой код
        });
        
        // Привязываем комбинацию
        KeyCombination combo = KeyCombination.of(
            GLFW.GLFW_KEY_E,
            Modifier.CONTROL
        );
        api.bind("mymod:do_something", combo);
    }
}
```

### 5.2. Создание комбинаций

```java
// Простая комбинация
KeyCombination shiftEnter = KeyCombination.of(
    GLFW.GLFW_KEY_ENTER,
    Modifier.SHIFT
);

// Сложная комбинация (Ctrl + Shift + E)
KeyCombination ctrlShiftE = KeyCombination.of(
    GLFW.GLFW_KEY_E,
    Modifier.CONTROL,
    Modifier.SHIFT
);

// Без модификаторов
KeyCombination justF = KeyCombination.of(GLFW.GLFW_KEY_F);
```

### 5.3. Проверка наличия мода

```java
if (ModLoader.isModLoaded("mtot")) {
    IMTOTAPI api = MTOTAPI.getInstance();
    // Используем API
} else {
    // Fallback логика
}
```

---

## 6. Нефункциональные требования

### 6.1. Производительность
- Проверка состояния клавиш: ≤ 1 мс на тик
- Отсутствие утечек памяти (WeakHashMap для кэшей)
- GUI рендеринг: 60 FPS без просадок

### 6.2. Совместимость
- **Конфликтов с ванильным KeyBinding нет**
- Работает с OptiFine, Sodium, Iris
- Не влияет на сетевой трафик (клиент-сайд)

### 6.3. Юзабилити
- Интуитивный GUI (аналогично ванильным настройкам)
- Подсказки при наведении (tooltips)
- Визуальное подтверждение сохранения

### 6.4. Безопасность
- Не требует прав администратора
- Не выполняет системные команды
- Не отправляет данные на внешние серверы

---

## 7. Руководство пользователя

### 7.1. Установка
1. Скачать .jar для своего лоадера
2. Поместить в папку `mods`
3. Запустить игру

### 7.2. Использование

#### Открытие настроек
`Options → Controls → More Than One Touch`

#### Добавление комбинации
1. Нажать кнопку `[Изменить]` рядом с действием
2. Нажать нужную комбинацию клавиш
3. Подтвердить или отменить

#### Сброс к значениям по умолчанию
Нажать кнопку `[Reset to Defaults]` внизу экрана

### 7.3. Встроенные комбинации

| Комбинация | Действие |
|------------|----------|
| `Shift + Enter` | Новая строка в чате |
| `Ctrl + L` | Очистить историю чата |
| `Ctrl + R` | Повторить последнее сообщение |

---

## 8. Разработка

### 8.1. Сборка проекта

```bash
# Клонирование
git clone https://github.com/yourusername/mtot.git
cd mtot

# Сборка всех версий
./gradlew build

# Сборка конкретного лоадера
./gradlew :fabric:build
./gradlew :neoforge:build
./gradlew :forge:build
```

### 8.2. Тестирование

```bash
# Запуск клиента Fabric
./gradlew :fabric:runClient

# Запуск клиента NeoForge
./gradlew :neoforge:runClient
```

### 8.3. CI/CD (GitHub Actions)
- Автоматическая сборка для всех лоадеров
- Тесты на каждой версии Minecraft
- Публикация релизов на CurseForge и Modrinth

---

## 9. Тест-план

### 9.1. Функциональные тесты

| ID | Тест | Ожидаемый результат |
|----|------|---------------------|
| T1 | Зарегистрировать комбинацию через API | Комбинация сохраняется |
| T2 | Нажать комбинацию | Действие выполняется |
| T3 | Нажать комбинацию с конфликтом | Предупреждение пользователю |
| T4 | Изменить комбинацию в GUI | Новая комбинация работает |
| T5 | Сохранить конфиг | Файл создается/обновляется |
| T6 | Загрузить конфиг при старте | Привязки восстанавливаются |
| T7 | Сбросить настройки | Возврат к fallback значениям |

### 9.2. Тесты совместимости

| ID | Тест | Ожидаемый результат |
|----|------|---------------------|
| C1 | Работа с ванильным KeyBinding | Без конфликтов |
| C2 | Работа с Sodium/OptiFine | Нет визуальных багов |
| C3 | Работа на Windows/macOS/Linux | Одинаковое поведение |
| C4 | Работа на разных версиях Java | Без ошибок |

### 9.3. Тесты производительности

| ID | Тест | Ожидаемый результат |
|----|------|---------------------|
| P1 | 100 зарегистрированных комбинаций | FPS ≥ 60 |
| P2 | 1000 комбинаций | FPS ≥ 30 |
| P3 | Частая смена комбинаций | Нет утечек памяти |

---

## 10. График разработки

### 10.1. Этапы

| Этап | Задачи | Время |
|------|--------|-------|
| **Phase 1** | Проектирование API, структура проекта | 2 дня |
| **Phase 2** | Ядро (регистрация, отслеживание клавиш) | 3 дня |
| **Phase 3** | Конфигурация (сохранение/загрузка) | 2 дня |
| **Phase 4** | GUI (настройки, список привязок) | 4 дня |
| **Phase 5** | Платформенные обёртки (Fabric, NeoForge, Forge) | 3 дня |
| **Phase 6** | Встроенные действия (`Shift+Enter` и др.) | 1 день |
| **Phase 7** | Тестирование, отладка | 3 дня |
| **Phase 8** | Документация, релиз | 2 дня |

**Итого:** 20 дней (≈ 4 недели)

---

## 11. Релиз и распространение

### 11.1. Платформы публикации
- **CurseForge**
- **Modrinth**
- **GitHub Releases**

### 11.2. Метаданные
- **Название:** More Than One Touch
- **Сокращение:** MTOT
- **Иконка:** Клавиатура с тремя пальцами (или абстрактный ключ)
- **Цвет:** #FF6B35 (оранжевый)

### 11.3. Лицензия
**MIT License** — открытый исходный код, разрешающий использование в любых проектах.

---

## 12. Future Roadmap (v1.1+)

- **Macro Recorder:** Запись последовательностей клавиш
- **Profile System:** Несколько профилей настроек
- **Game-Specific Bindings:** Разные комбинации для разных игровых режимов
- **Mouse Support:** Клавиши мыши (левая, правая, боковые)
- **Double-Tap Support:** Комбинации типа `Shift + Shift` (двойное нажатие)

---

## 13. Приложение A: GLFW Key Codes

| Код | Клавиша |
|-----|---------|
| 257 | Enter |
| 256 | Escape |
| 32 | Space |
| 262 | Right Shift |
| 340 | Left Shift |
| 341 | Left Control |
| 342 | Left Alt |
| 65-90 | A-Z |
| 48-57 | 0-9 |
| 258 | Tab |

Полный список: [GLFW Key Codes](https://www.glfw.org/docs/3.3/group__keys.html)

---

## 14. Приложение B: Пример интеграции

### Пример мода, использующего MTOT

```java
@Mod("examplemod")
public class ExampleMod {
    public ExampleMod() {
        // Проверяем наличие MTOT
        if (Platform.isModLoaded("mtot")) {
            IMTOTAPI api = MTOTAPI.getInstance();
            
            // Регистрируем действие
            api.registerAction("examplemod:teleport_home", () -> {
                Minecraft.getInstance().player.sendChatMessage("/home");
            });
            
            // Привязываем комбинацию (если пользователь не изменил)
            KeyCombination homeCombo = KeyCombination.of(
                GLFW.GLFW_KEY_H,
                Modifier.CONTROL,
                Modifier.SHIFT
            );
            api.bindDefault("examplemod:teleport_home", homeCombo);
        } else {
            // Fallback: используем обычную клавишу
            KeyBinding homeKey = new KeyBinding(
                "key.examplemod.home",
                GLFW.GLFW_KEY_H,
                "key.categories.example"
            );
            ClientRegistry.registerKeyBinding(homeKey);
        }
    }
}
```

---

## 15. Заключение

**More Than One Touch** предоставляет современную, гибкую и надёжную систему горячих клавиш для Minecraft. Мод решает фундаментальное ограничение ванильного `KeyBinding`, открывая новые возможности для игроков и разработчиков.

**Ключевые преимущества:**
- ✅ Поддержка комбинаций с модификаторами
- ✅ Интуитивный GUI
- ✅ Открытое API
- ✅ Поддержка всех актуальных лоадеров
- ✅ Высокая производительность
- ✅ Клиент-сайд (не требует серверной установки)

---

**Версия документа:** 1.0  
**Дата:** 19.06.2026  
**Автор:** More Than One Touch Team  
**Статус:** Готов к реализации