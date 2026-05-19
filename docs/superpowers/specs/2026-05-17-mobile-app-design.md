# Design Spec — RoboArm Mobile Application

**Date:** 2026-05-17  
**Status:** Draft  
**Topic:** MVI Implementation for Android with Jetpack Compose

---

## 1. Overview
The RoboArm mobile application is a monitoring and control tool for industrial robotic arms. It implements the requirements defined in `ТЗ.md` and the visual language from `DLS.md`.

---

## 2. Technical Stack
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVI (Model-View-Intent) with `StateFlow`
- **Networking:** Ktor Client (with Content Negotiation & Serialization)
- **Dependency Injection:** Koin (lightweight, Kotlin-native)
- **Navigation:** Jetpack Navigation (Type-Safe Kotlin DSL)
- **Concurrency:** Kotlin Coroutines & Flow

---

## 3. Architecture: MVI Pattern

Each screen consists of:
- **`UiState`**: A single data class representing the entire screen state.
- **`Intent`**: A sealed class representing user actions.
- **`Effect`**: A sealed class for one-time events (navigation, toasts).
- **`ViewModel`**: Processes Intents, updates State, and triggers Effects.

---

## 4. UI & Design System

### 4.1 Theme Implementation
- **RoboArmTheme**: A custom Compose theme that provides DLS tokens via `CompositionLocal`.
- **Colors**: Support for Light and Dark themes as defined in `DLS.md`.
- **Typography**: 
  - `Manrope` for primary UI text.
  - `JetBrains Mono` for sensor data and technical values.

### 4.2 Core Components
- `StatusPill`: For displaying machine states (`ok`, `warning`, `critical`).
- `SensorTile`: Dynamic telemetry card with mini-bar visualization.
- `RobotCard / LineCard`: List items for navigation.
- `EmergencyStopButton`: High-visibility control element.

---

## 5. Navigation Structure

- **MainScreen (Scaffold with BottomBar)**
  - `HomeGraph`
    - `DashboardScreen` (Lines List)
    - `LineDetailScreen` (Robots List)
    - `RobotDetailScreen` (Tabs: Telemetry & Control)
  - `SupportScreen` (Placeholder)
  - `ReportsScreen` (Placeholder)
- **Global Screens**
  - `SettingsScreen` (Theme toggle)
  - `NotificationsScreen`

---

## 6. Networking Layer
- **`ApiService`**: Ktor-based interface for backend communication.
- **Data Models**: Kotlin `Serializable` classes matching the backend API contracts.
- **Error Handling**: Mapping HTTP errors to UI-friendly messages in the `UiState`.

---

## 7. Implementation Phases
1. **Foundation:** Theme, Typography, Koin setup, Ktor client configuration.
2. **Components:** Implementation of DLS-specific UI elements.
3. **Home Screen:** Dashboard, Lines list, Navigation to details.
4. **Robot Details:** Real-time telemetry display and control implementation.
5. **Notifications & Settings:** History list and theme switching.
