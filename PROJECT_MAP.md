# PROJECT MAP — More Than One Touch (MTOT)

## Структура

```
MTOT/
├── common/                          # Общий код (95% логики)
│   ├── build.gradle                 # Java 21, JUnit 5, GSON
│       └── src/
│           ├── main/java/com/mtot/
│           │   ├── api/                 # Публичное API
│           │   │   ├── IMTOTAPI.java    # Интерфейс API
│           │   │   ├── KeyCombination.java  # Неизменяемая комбинация клавиш
│           │   │   ├── Modifier.java    # Enum: SHIFT, CONTROL, ALT
│           │   │   └── MTOTAPI.java     # Статическая точка доступа
│           │   ├── core/                # Ядро
│           │   │   ├── BindingRegistry.java  # Реестр действий и привязок
│           │   │   ├── KeyStateTracker.java  # Отслеживание нажатий
│           │   │   └── MTOTManager.java     # Синглтон + tick()
│           │   ├── config/              # Конфигурация
│           │   │   └── MTOTConfig.java  # JSON (де)сериализация
│           │   ├── gui/                 # GUI
│           │   │   ├── MTOTControlsScreen.java  # Экран настроек
│           │   │   └── BindingEntryWidget.java  # Виджет строки привязки
│           │   └── actions/             # Встроенные действия
│           │       └── BuiltinActions.java  # Shift+Enter, Ctrl+L, Ctrl+R
│           ├── main/resources/assets/mtot/lang/
│           │   ├── en_us.json           # Английская локализация
│           │   └── ru_ru.json           # Русская локализация
│           └── test/java/com/mtot/
│           ├── api/                 # ModifierTest, KeyCombinationTest, MTOTAPITest
│           ├── core/                # BindingRegistryTest, KeyStateTrackerTest, MTOTManagerTest
│           ├── config/              # MTOTConfigTest
│           └── actions/             # BuiltinActionsTest
├── fabric/                          # Fabric обёртка
│   ├── build.gradle                 # Fabric Loom
│   └── src/main/
│       ├── java/com/mtot/fabric/
│       │   ├── MTOTFabricClient.java    # ClientModInitializer
│       │   └── FabricKeyManager.java    # GLFW polling
│       └── resources/fabric.mod.json
├── neoforge/                        # NeoForge обёртка
│   ├── build.gradle                 # NeoForge userdev
│   └── src/main/
│       ├── java/com/mtot/neoforge/
│       │   ├── MTOTNeoForgeClient.java  # @Mod + EventBus
│       │   └── NeoForgeKeyManager.java  # GLFW polling
│       └── resources/META-INF/neoforge.mods.toml
├── forge/                           # Forge обёртка (1.20.1 LTS)
│   ├── build.gradle                 # ForgeGradle
│   └── src/main/
│       ├── java/com/mtot/forge/
│       │   ├── MTOTForgeClient.java     # @Mod + EventBus
│       │   └── ForgeKeyManager.java     # GLFW polling
│       └── resources/META-INF/mods.toml
├── build.gradle                     # Корневой билд
├── settings.gradle                  # Multi-module (common + fabric)
├── gradle.properties                # Версии зависимостей
├── gradlew / gradlew.bat           # Gradle wrapper 8.7
├── PROJECT_MAP.md                   # Этот файл
├── .directory.md                    # Документация корня
├── .gitignore
├── MTOT-doc.md                      # Техническая спецификация
└── 2R-skill-2.0.md                  # Стандарт разработки
```

## Статистика

- **Всего классов:** 14 (production) + 8 (test)
- **Всего тестов:** 42, все зелёные
- **Строк кода (common):** ~650 (production) + ~500 (test)

## Граф зависимостей

```
IMTOTAPI ← MTOTAPI (статический доступ)
    ↑
MTOTManager (синглтон, implements IMTOTAPI)
    ├── BindingRegistry  — действия + привязки
    └── KeyStateTracker  — состояния клавиш + release detection
            ↑
MTOTConfig (сериализация BindingRegistry ↔ JSON)
            ↑
BuiltinActions (регистрирует 3 действия по умолчанию)

GUI
    ├── MTOTControlsScreen  ← BindingEntryWidget
    └── оба используют KeyCombination, Modifier, IMTOTAPI

Локализация
    └── assets/mtot/lang/{en_us,ru_ru}.json

Платформы → MTOTManager.tick()
    ├── Fabric   → ClientTickEvents.END_CLIENT_TICK
    ├── NeoForge → ClientTickEvent.Post
    └── Forge    → ClientTickEvent.Post
```

## Индекс изменений

- `common/api/` → проверить `common/core/`
- `common/core/` → проверить `common/config/`, `actions/`, все платформы
- `common/config/` → проверить `common/core/`
- `common/actions/` → проверить `common/core/`
- `fabric/` → проверить только Fabric
- `neoforge/` → проверить только NeoForge
- `forge/` → проверить только Forge
- `build.gradle` / `settings.gradle` → проверить все модули
