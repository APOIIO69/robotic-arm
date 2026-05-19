# RoboArm — Design Language System
**Версия:** 1.0 (MVP)  
**Дата:** 16.05.2026  
**Статус:** Draft для утверждения

---

## 1. Введение

DLS описывает визуальный язык мобильного приложения RoboArm. Документ является единственным источником истины для дизайнеров и разработчиков при реализации интерфейса.

---

## 2. Типографика

### Шрифтовые гарнитуры

| Роль | Семейство | Применение |
|---|---|---|
| Основной | **Manrope** | Все UI-тексты: заголовки, подписи, кнопки, метки |
| Числовой | **JetBrains Mono** | Значения датчиков, показания осей, технические числа |

### Шкала размеров

| Токен | Размер | Вес | Применение |
|---|---|---|---|
| `title-lg` | 15px / 700 | Bold | Заголовок экрана (навигационная панель) |
| `title-md` | 13px / 600 | SemiBold | Название карточки, имя робота |
| `label` | 12px / 600 | SemiBold | Метки секций (uppercase + letter-spacing 0.08em) |
| `body` | 13px / 400 | Regular | Основной текст, описания |
| `body-sm` | 12px / 400 | Regular | Вторичный текст, подписи |
| `caption` | 11px / 500 | Medium | Статусы, единицы измерения |
| `micro` | 10px / 400–600 | Regular/SemiBold | Подписи таббара, временны́е метки |
| `mono-lg` | 22px / 700 | Bold Mono | Значения датчиков |
| `mono-md` | 14px / 700 | Bold Mono | Значения осей |

### Правила
- Числа из датчиков и осей — **только JetBrains Mono**
- Метки секций — uppercase, `letter-spacing: 0.08em`, цвет `textTertiary`
- `text-wrap: pretty` на многострочных подписях

---

## 3. Цветовая система

### Акцентные и статусные цвета (инвариантны для обеих тем)

| Токен | HEX (dark) | HEX (light) | Применение |
|---|---|---|---|
| `blue` | `#4F87FF` | `#3568E5` | Активные элементы, CTA, прогресс |
| `blueDim` | `rgba(79,135,255,0.14)` | `rgba(53,104,229,0.12)` | Фон выбранных/активных элементов |
| `critical` | `#FF3B30` | `#D42B20` | Критические ошибки |
| `criticalDim` | `rgba(255,59,48,0.14)` | `rgba(212,43,32,0.10)` | Фон карточек/баннеров critical |
| `warning` | `#FF9F0A` | `#B86E00` | Предупреждения |
| `warningDim` | `rgba(255,159,10,0.14)` | `rgba(184,110,0,0.10)` | Фон карточек/баннеров warning |
| `ok` | `#32D74B` | `#1A8535` | Штатная работа |
| `okDim` | `rgba(50,215,75,0.14)` | `rgba(26,133,53,0.10)` | Фон карточек ok |
| `offline` | `#55576A` | `#8E8FA2` | Неактивные элементы, заглушки |

### Тёмная тема (по умолчанию)

| Токен | HEX | Применение |
|---|---|---|
| `bg` | `#0D0E14` | Фон приложения |
| `card` | `#161820` | Карточки, таббар |
| `elevated` | `#1E2028` | Приподнятые элементы, аватары, иконки-кнопки |
| `input` | `#1A1C24` | Поля ввода, поиск |
| `border` | `rgba(255,255,255,0.08)` | Разделители, обводки карточек |
| `borderMid` | `rgba(255,255,255,0.12)` | Акцентные обводки |
| `textPrimary` | `#EEEEF3` | Основной текст |
| `textSecondary` | `#7B7D8E` | Вторичный текст |
| `textTertiary` | `#4D4F62` | Метки, подсказки |

### Светлая тема

| Токен | HEX | Применение |
|---|---|---|
| `bg` | `#F1F2F8` | Фон приложения |
| `card` | `#FFFFFF` | Карточки, таббар |
| `elevated` | `#E8E9F2` | Приподнятые элементы |
| `input` | `#E4E5EE` | Поля ввода |
| `border` | `rgba(0,0,0,0.07)` | Разделители |
| `borderMid` | `rgba(0,0,0,0.13)` | Акцентные обводки |
| `textPrimary` | `#13141C` | Основной текст |
| `textSecondary` | `#585A6E` | Вторичный текст |
| `textTertiary` | `#8D8FA3` | Метки, подсказки |

---

## 4. Отступы и сетка

### Базовая единица: 4px

| Токен | Значение | Применение |
|---|---|---|
| `space-1` | 4px | Минимальный разрыв (иконка + текст) |
| `space-2` | 8px | Зазор между элементами внутри карточки |
| `space-3` | 10px | Gap между карточками в сетке |
| `space-4` | 12px | Padding внутри компактных карточек |
| `space-5` | 14px | Padding стандартных карточек |
| `space-6` | 16px | Горизонтальные поля экрана (margin) |
| `space-8` | 32px | Крупные отступы, разделители секций |

### Горизонтальные поля
Все основные контентные области: `padding: 0 16px`

### Вертикальные зазоры между карточками
- Список линий / роботов: `gap: 8px`
- Сетка датчиков (2 колонки): `gap: 9px`
- Карточки статистики (2×2): `gap: 10px`

---

## 5. Скругления

| Токен | Значение | Применение |
|---|---|---|
| `radius-sm` | 8px | Фильтры, теги, переключатели |
| `radius-md` | 10px | Кнопки навигации, иконки-кнопки |
| `radius-lg` | 12px | Поля ввода, плитки датчиков |
| `radius-xl` | 14px | Основные карточки (линии, роботы, статистика) |
| `radius-pill` | 20px | StatusPill, filter chips |
| `radius-circle` | 50% | Аватар, StatusDot |

---

## 6. Компоненты

### 6.1 StatusDot
Индикатор статуса — цветная точка.

```
Размер: 6–9px (зависит от контекста)
Цвет: statusColor(status)
Форма: круг (border-radius: 50%)
Варианты: filled (заливка) / outline (только обводка 2px)
```

Состояния: `ok` · `warning` · `critical` · `offline`

---

### 6.2 StatusPill
Чипс с меткой состояния.

```
Padding: 3–4px 8–10px
Border-radius: radius-pill (20px)
Фон: statusDim(status)
Border: 1px solid statusColor + 30% opacity
Точка: 6px круг, statusColor
Текст: 10–11px / 600 / statusColor
```

---

### 6.3 StatCard (Dashboard)
Карточка дашборда быстрого просмотра.

```
Layout: flex column, gap: 6px
Padding: 14px 14px 12px
Border-radius: radius-xl (14px)
Фон: card
Border: 1px solid border

Содержит:
  [0] Заголовок — 13px / 600 / textPrimary
  [1] Подпись  — 11px / 400 / textSecondary
  [2] Значение — 12px / 700 / {акцентный цвет}

Кликабельна: ведёт к отфильтрованному списку
```

---

### 6.4 LineCard
Карточка производственной линии.

```
Layout: flex row, align-items: center, gap: 12px
Padding: 12px 14px
Border-radius: radius-xl

[Иконка-плашка]  40×40px, radius-md, фон: elevated
[Текст]          flex: 1
  Название       13px / 600 / textPrimary (ellipsis overflow)
  Описание       11px / textSecondary или statusColor при аварии
[Индикаторы]     3 StatusDot: critical / warning / ok
```

---

### 6.5 RobotCard
Карточка робота в списке линии.

```
Layout: flex row, align-items: center, gap: 12px
Padding: 14px
Border-radius: radius-xl
Border: акцентная при status ≠ ok (statusColor + 30% opacity)

[Иконка-плашка]  44×44px, radius-md, фон: statusDim
[Текст]          flex: 1
  Имя            13px / 600 / textPrimary
  Модель         11px / textTertiary
  Ошибка/статус  11px / statusColor или ok
[Правая часть]   StatusPill + ChevronRight
```

---

### 6.6 SensorTile
Плитка телеметрии датчика.

```
Layout: flex column, gap: 6px
Padding: 12px 12px 10px
Border-radius: radius-lg (12px)
Фон: criticalDim/warningDim/card (зависит от status)
Border: акцентная при status ≠ ok

[Строка метки]
  SensorIcon (14px) + Название датчика (10px / 500 / textSecondary)
  StatusDot (7px) — выровнен вправо

[Значение]
  Число: 22px / 700 / JetBrains Mono / textPrimary или statusColor
  Единица: 11px / 500 / textTertiary

[Мини-бар] (если есть normalMax)
  Высота: 3px, border-radius: 2px
  Фон: elevated → заливка statusColor до fillPct%
```

Гибкость: каждый датчик задаётся объектом `Sensor`, полный список типов — см. раздел 7.

---

### 6.7 FilterTabs
Горизонтальная панель фильтров.

```
Padding: 0 16px 12px
Gap: 8px

Активный таб:
  Background: blue
  Color: #fff
  Border: none

Неактивный таб:
  Background: transparent
  Color: textSecondary
  Border: 1px solid border
  
Padding: 6px 12px, border-radius: radius-pill
Размер текста: 12px / 500
```

---

### 6.8 SearchBar

```
Фон: input
Border: 1px solid border
Border-radius: radius-lg (12px)
Padding: 9px 12px 9px 34px
Иконка поиска: 14px, left: 11px, цвет: textTertiary
Текст: 13px / textPrimary
Placeholder: textTertiary
```

---

### 6.9 TabBar
Нижняя панель навигации.

```
Фон: card
Border-top: 1px solid border
Padding: 10px 0 4px

Вкладка (flex column, gap: 3px):
  Иконка: 22px
  Подпись: 10px / 400 (неактивн.) / 600 (активн.)
  Цвет активного: blue
  Цвет неактивного: textTertiary
```

---

### 6.10 AxisSlider
Слайдер управления осью робота.

```
Подпись оси: 12px / 600 / textSecondary (слева)
Значение: 14px / 700 / JetBrains Mono / textPrimary (справа)
Единица: 10px / textTertiary

Range input:
  Height: 4px, фон: elevated (или light-range)
  Thumb: 18px / синий / обводка bg
  
Диапазон: min–max из данных оси
Disabled state: opacity 0.4 при аварийной остановке
```

---

### 6.11 ActionButton
Кнопка специального действия робота.

```
Padding: 8px 14px, border-radius: radius-md
Gap: 6px (иконка + текст), 12px / 500

Активное (pressed):
  Фон: blueDim, border: blue+50%, color: blue

Неактивное:
  Фон: elevated, border: border, color: textSecondary
```

---

### 6.12 EmergencyStop
Кнопка аварийной остановки.

```
Width: 100%, padding: 14px, border-radius: radius-xl
Фон: criticalDim (активн.) / elevated (остановлено)
Border: 2px solid critical / border
Color: critical / textSecondary
Текст: 14px / 700 / uppercase / letter-spacing 0.04em
Иконка: ⏹ / ▶ (22px)
```

---

### 6.13 ErrorBanner
Баннер ошибки/предупреждения на экране робота.

```
Padding: 9px 12px, border-radius: radius-md
Фон: statusDim, border: 1px solid statusColor+30%
Gap: 8px (иконка + текст)
Текст: 12px / 500 / statusColor
```

---

### 6.14 SettingsRow
Строка настроек.

```
Padding: 13px 16px
Текст: 13px / textPrimary (деструктивный: critical)
ChevronRight: 14px / textTertiary
Border-top: 1px solid border (кроме первой строки в группе)
Фон: card, border-radius: radius-xl (только на контейнере)
```

---

### 6.15 NavIconButton
Иконка-кнопка в хедере (назад, настройки).

```
Размер: 34×34px, border-radius: radius-md
Фон: elevated, border: 1px solid border
Иконка: 17–18px / textSecondary
```

---

## 7. Система статусов

| Статус | Цвет | Dim-фон | Применение |
|---|---|---|---|
| `ok` | green | okDim | Штатная работа |
| `warning` | orange | warningDim | Требуется проверка |
| `critical` | red | criticalDim | Требуется вмешательство |
| `offline` | grey | transparent | Устройство недоступно |

Статус вычисляется на трёх уровнях:
- **Линия** — по worst-case среди всех роботов
- **Робот** — по worst-case среди всех датчиков
- **Датчик** — по выходу `value` за пределы `[normalMin, normalMax]`

---

## 8. Иконки

Все иконки — inline SVG, stroke-based (strokeWidth ~1.8px).  
Размеры: 14px (мини), 17–18px (кнопки), 20–22px (таббар), 26–28px (карточки).

### Навигационные иконки
`HomeIcon` · `SupportIcon` · `ReportsIcon` · `BackIcon` · `ChevronRight` · `SearchIcon` · `GearIcon` · `BellIcon` · `RobotIcon`

### Иконки датчиков (SensorIcon)
Тип датчика задаётся строкой `type` в объекте Sensor:

| Тип | Применение |
|---|---|
| `temperature` | Температура (°C, °F) |
| `torque` | Крутящий момент (Нм) |
| `current` | Электрический ток (А) |
| `counter` | Счётчики (циклы, штуки) |
| `percent` | Проценты (аптайм, КПД) |
| `pressure` | Давление (бар, кПа) |
| `force` | Усилие (Н) |
| `weight` | Нагрузка, масса (кг) |
| `speed` | Скорость (м/с, м/мин) |
| `vibration` | Вибрация (мм/с) |
| `distance` | Расстояние, точность (мм, м) |
| `power` | Мощность (кВт, Вт) |
| `flow` | Расход (мл/мин, л/мин) |

Список расширяем: новый тип = новая иконка + новый ключ в словаре `SensorIcon`.

---

## 9. Анимации и переходы

| Элемент | Свойство | Длительность | Easing |
|---|---|---|---|
| Смена темы | background, color, border | 350ms | ease |
| Hover/active карточки | border-color, background | 150ms | ease |
| Кнопки действий | all | 200ms | ease |
| Мини-бар датчика | width | 400ms | ease |
| Аварийная остановка | background, border, color | 200ms | ease |
| Слайдер оси | opacity (disabled) | 200ms | ease |

---

## 10. Тёмная и светлая тема — правила переключения

- Переключение через мутацию объекта `C` (`Object.assign(C, THEMES[next])`)
- CSS-класс `body.light-theme` для CSS-only overrides (корпус телефона, range input thumb)
- Все React-компоненты читают цвета из `window.C` — при ре-рендере автоматически получают новые значения
- Акцентные / статусные цвета имеют версии под обе темы (более насыщенные в light для контраста на белом фоне)
- Переход: `transition: background 0.35s, color 0.35s` на body и phone-shell

---

## 11. Принципы

1. **Информация важнее декора.** Никаких декоративных градиентов, теней и эффектов без смысловой нагрузки.
2. **Статус всегда явен.** Любой элемент с состоянием (датчик, робот, линия) показывает цветовой статус без необходимости читать текст.
3. **Числа читаются моментально.** Монопространственный шрифт, крупный размер, никакого смешения с пропорциональным шрифтом.
4. **Гибкость через данные.** Набор датчиков, осей и кнопок управления определяется конфигурацией робота, а не хардкодом UI.
5. **Минимальные цвета.** Два нейтральных фоновых слоя + три статусных цвета + один акцент. Не вводить новые цвета без обоснования.
