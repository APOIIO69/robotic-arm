# [Mobile UI Components] Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement reusable UI components in Jetpack Compose based on the Design Language System (DLS.md).

**Architecture:** Stateless Compose widgets using the custom `RoboArmTheme` for styling. Components will be categorized into Status, Cards, Lists, and Controls.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3.

---

### Task 1: Status Models and Common Components

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/data/models/Status.kt`
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/components/StatusComponents.kt`

- [ ] **Step 1: Define the Status enum**

```kotlin
package com.apollo.roboarm.data.models

enum class RobotStatus {
    OK, WARNING, CRITICAL, OFFLINE
}
```

- [ ] **Step 2: Implement StatusDot and StatusPill**

```kotlin
// StatusComponents.kt
@Composable
fun StatusDot(status: RobotStatus, modifier: Modifier = Modifier) {
    val color = when(status) {
        RobotStatus.OK -> RoboArmTheme.colors.ok
        RobotStatus.WARNING -> RoboArmTheme.colors.warning
        RobotStatus.CRITICAL -> RoboArmTheme.colors.critical
        RobotStatus.OFFLINE -> Color.Gray
    }
    Box(modifier = modifier.size(8.dp).background(color, CircleShape))
}

@Composable
fun StatusPill(status: RobotStatus, label: String) {
    // Implementation based on DLS 6.2 (Padding, border-radius, dim background)
    Surface(
        color = getStatusDimColor(status),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, getStatusColor(status).copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)) {
            StatusDot(status)
            Text(text = label, color = getStatusColor(status), style = RoboArmTheme.typography.body)
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/data/models/Status.kt mobile/android/app/src/main/java/com/apollo/roboarm/ui/components/StatusComponents.kt
git commit -m "feat: implement StatusDot and StatusPill components"
```

---

### Task 2: Dashboard and Telemetry Cards

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/components/Cards.kt`

- [ ] **Step 1: Implement StatCard (DLS 6.3)**

```kotlin
@Composable
fun StatCard(title: String, subtitle: String, value: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = RoboArmTheme.colors.card),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, RoboArmTheme.colors.border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = RoboArmTheme.typography.titleLg)
            Text(subtitle, style = RoboArmTheme.typography.body)
            Text(value, color = color, style = RoboArmTheme.typography.monoMd)
        }
    }
}
```

- [ ] **Step 2: Implement SensorTile (DLS 6.6)**

```kotlin
@Composable
fun SensorTile(label: String, value: String, unit: String, status: RobotStatus, progress: Float?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = getStatusDimColor(status)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                Text(label, style = RoboArmTheme.typography.body)
                StatusDot(status)
            }
            Text("$value $unit", style = RoboArmTheme.typography.monoMd)
            if (progress != null) {
                LinearProgressIndicator(progress = progress, color = getStatusColor(status))
            }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/ui/components/Cards.kt
git commit -m "feat: implement StatCard and SensorTile components"
```

---

### Task 3: List Items and Emergency Controls

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/components/Lists.kt`
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/components/Controls.kt`

- [ ] **Step 1: Implement LineCard and RobotCard (DLS 6.4, 6.5)**

```kotlin
// Lists.kt
@Composable
fun LineCard(name: String, description: String, status: RobotStatus) {
    Card(
        colors = CardDefaults.cardColors(containerColor = RoboArmTheme.colors.card),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = RoboArmTheme.typography.titleLg)
                Text(description, style = RoboArmTheme.typography.body)
            }
            StatusDot(status)
        }
    }
}
```

- [ ] **Step 2: Implement EmergencyStopButton (DLS 6.12)**

```kotlin
// Controls.kt
@Composable
fun EmergencyStopButton(isStopped: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isStopped) RoboArmTheme.colors.elevated else RoboArmTheme.colors.critical.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(2.dp, if (isStopped) RoboArmTheme.colors.border else RoboArmTheme.colors.critical),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(
            text = if (isStopped) "ВОЗОБНОВИТЬ РАБОТУ" else "АВАРИЙНАЯ ОСТАНОВКА",
            color = if (isStopped) RoboArmTheme.colors.textPrimary else RoboArmTheme.colors.critical,
            style = RoboArmTheme.typography.titleLg
        )
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/ui/components/Lists.kt mobile/android/app/src/main/java/com/apollo/roboarm/ui/components/Controls.kt
git commit -m "feat: implement List cards and EmergencyStopButton"
```

---

### Task 4: Visual Verification via Preview

- [ ] **Step 1: Create a ComponentGallery screen for previews**

```kotlin
// MainActivity.kt or a new Preview file
@Preview(showBackground = true, backgroundColor = 0xFF0D0E14)
@Composable
fun ComponentGalleryPreview() {
    RoboArmTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            StatusPill(status = RobotStatus.OK, label = "OK")
            Spacer(Modifier.height(8.dp))
            SensorTile(label = "Temperature", value = "45.2", unit = "°C", status = RobotStatus.OK, progress = 0.5f)
            Spacer(Modifier.height(8.dp))
            EmergencyStopButton(isStopped = false) {}
        }
    }
}
```

- [ ] **Step 2: Commit**

```bash
git commit -am "chore: add component previews for visual verification"
```
