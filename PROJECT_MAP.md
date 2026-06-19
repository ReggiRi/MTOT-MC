# PROJECT MAP — More Than One Touch (MTOT)

## Структура

```
MTOT/
├── common/          # Общий код (95% логики)
│   └── src/main/java/com/mtot/
│       ├── api/     # Публичное API
│       ├── core/    # Ядро (регистрация, отслеживание)
│       ├── gui/     # Пользовательский интерфейс
│       ├── config/  # Конфигурация (JSON)
│       └── actions/ # Встроенные действия
├── fabric/          # Fabric обёртка
├── neoforge/        # NeoForge обёртка
└── forge/           # Forge обёртка
```

## Граф зависимостей

- `common` — не зависит от платформ
- `fabric` — зависит от `common`
- `neoforge` — зависит от `common`
- `forge` — зависит от `common`

## Индекс изменений

- Изменение `common/api/` → проверить `common/core/`, все платформы
- Изменение `common/core/` → проверить `common/gui/`, `common/config/`, все платформы
- Изменение `common/gui/` → проверить только GUI
- Изменение `common/config/` → проверить `common/core/`
- Изменение `common/actions/` → проверить `common/core/`
- Изменение `fabric/` → проверить только Fabric
- Изменение `neoforge/` → проверить только NeoForge
- Изменение `forge/` → проверить только Forge
