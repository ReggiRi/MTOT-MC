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
│   ├── src/main/
│   │   ├── java/com/mtot/fabric/
│   │   │   ├── MTOTFabricClient.java    # ClientModInitializer + BuiltinActions + F6
│   │   │   ├── FabricKeyManager.java    # GLFW polling
│   │   │   ├── MTOTModMenuIntegration.java  # ModMenu integration
│   │   │   └── mixin/ChatScreenMixin.java   # Shift+Enter mixin
│   │   └── resources/
│   │       ├── fabric.mod.json
│   │       ├── mtot.mixins.json
│   │       └── assets/mtot/icon.png
│   └── ...
├── neoforge/                        # NeoForge обёртка
│   ├── build.gradle                 # NeoForge userdev + jar из common
│   └── src/main/
│       ├── java/com/mtot/neoforge/
│       │   ├── MTOTNeoForgeClient.java  # @Mod + BuiltinActions + F6
│       │   └── NeoForgeKeyManager.java  # GLFW polling
│       └── resources/
│           ├── META-INF/neoforge.mods.toml
│           └── mtot.png
├── forge/                           # Forge обёртка (1.20.1 LTS)
│   ├── build.gradle                 # ForgeGradle + official mappings + jar из common
│   └── src/main/
│       ├── java/com/mtot/forge/
│       │   ├── MTOTForgeClient.java     # @Mod + BuiltinActions + F6
│       │   └── ForgeKeyManager.java     # GLFW polling
│       └── resources/
│           ├── META-INF/mods.toml
│           └── mtot.png
├── build.gradle                     # Корневой билд
├── settings.gradle                  # Multi-module (common + fabric)
├── gradle.properties                # Версии зависимостей
├── gradlew / gradlew.bat           # Gradle wrapper 8.7
├── PROJECT_MAP.md                   # Этот файл
├── .directory.md                    # Документация корня
├── API.md                           # Документация API для мододелов
└── .gitignore
```

## Статистика

- **Всего классов:** 17 (production) + 8 (test)
- **Всего тестов:** 42, все зелёные
- **Строк кода (common):** ~670 (production) + ~510 (test)

## Граф зависимостей

```
IMTOTAPI (register, registerAction, bind, getBinding, findConflicts)
    ← MTOTAPI (статический доступ)
    ↑
MTOTManager (синглтон, implements IMTOTAPI)
    ├── BindingRegistry  — действия + привязки + findConflicts
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

Платформы → MTOTManager.tick() + BuiltinActions.init() + F6
    ├── Fabric   → ClientTickEvents.END_CLIENT_TICK + mixin
    ├── NeoForge → ClientTickEvent.Post
    └── Forge    → ClientTickEvent.Post
```

## Индекс изменений

- `common/api/IMTOTAPI.java` → содержит `findConflicts`
- `common/core/MTOTManager.java` → делегирует `findConflicts` в `BindingRegistry`
- `forge/` → BuiltinActions.init() + F6 settings key + official mappings + jar bundle
- `neoforge/` → BuiltinActions.init() + F6 settings key + jar bundle
- `settings.gradle` → common + fabric активны, forge/neoforge отключены до dev-окружения
