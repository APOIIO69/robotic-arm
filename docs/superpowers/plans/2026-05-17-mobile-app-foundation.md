# [Mobile App Foundation] Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Setup the technical foundation (Koin, Ktor, Navigation) and implement the Design Language System (Typography, Colors, Theme) in Jetpack Compose.

**Architecture:** MVI with Koin for DI and Ktor for Networking. Custom Theme implementation using `CompositionLocal` to provide DLS tokens.

**Tech Stack:** Kotlin, Jetpack Compose, Koin, Ktor Client, Jetpack Navigation.

---

### Task 1: Gradle Dependencies Setup

**Files:**
- Modify: `mobile/android/gradle/libs.versions.toml`
- Modify: `mobile/android/app/build.gradle.kts`

- [ ] **Step 1: Add versions and libraries to `libs.versions.toml`**

```toml
[versions]
# ... existing
koin = "3.5.3"
ktor = "2.3.7"
navigationCompose = "2.8.0-alpha08" # Type-safe navigation
kotlinxSerialization = "1.6.2"

[libraries]
# ... existing
koin-android = { group = "io.insert-koin", name = "koin-android", version.ref = "koin" }
koin-androidx-compose = { group = "io.insert-koin", name = "koin-androidx-compose", version.ref = "koin" }
ktor-client-core = { group = "io.ktor", name = "ktor-client-core", version.ref = "ktor" }
ktor-client-okhttp = { group = "io.ktor", name = "ktor-client-okhttp", version.ref = "ktor" }
ktor-client-content-negotiation = { group = "io.ktor", name = "ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { group = "io.ktor", name = "ktor-serialization-kotlinx-json", version.ref = "ktor" }
androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
kotlinx-serialization-json = { group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version.ref = "kotlinxSerialization" }

[plugins]
# ... existing
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

- [ ] **Step 2: Apply plugins and dependencies in `app/build.gradle.kts`**

```kotlin
plugins {
    // ...
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    // ...
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
}
```

- [ ] **Step 3: Sync and Commit**

```bash
git add mobile/android/gradle/libs.versions.toml mobile/android/app/build.gradle.kts
git commit -m "chore: setup mobile dependencies (Koin, Ktor, Navigation)"
```

---

### Task 2: DLS Theme Implementation (Colors & Typography)

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/theme/DlsColors.kt`
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/theme/DlsTypography.kt`
- Modify: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/theme/Theme.kt`

- [ ] **Step 1: Define DLS Color tokens from DLS.md**

```kotlin
package com.apollo.roboarm.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.staticCompositionLocalOf

data class RoboArmColors(
    val bg: Color,
    val card: Color,
    val elevated: Color,
    val input: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val border: Color,
    // Accents
    val blue: Color = Color(0xFF4F87FF),
    val ok: Color = Color(0xFF32D74B),
    val warning: Color = Color(0xFFFF9F0A),
    val critical: Color = Color(0xFFFF3B30)
)

val DarkColors = RoboArmColors(
    bg = Color(0xFF0D0E14),
    card = Color(0xFF161820),
    elevated = Color(0xFF1E2028),
    input = Color(0xFF1A1C24),
    textPrimary = Color(0xFFEEEEF3),
    textSecondary = Color(0xFF7B7D8E),
    textTertiary = Color(0xFF4D4F62),
    border = Color(0xFFFFFFFF).copy(alpha = 0.08f)
)

val LightColors = RoboArmColors(
    bg = Color(0xFFF1F2F8),
    card = Color(0xFFFFFFFF),
    elevated = Color(0xFFE8E9F2),
    input = Color(0xFFE4E5EE),
    textPrimary = Color(0xFF13141C),
    textSecondary = Color(0xFF585A6E),
    textTertiary = Color(0xFF8D8FA3),
    border = Color(0x12000000)
)

val LocalRoboArmColors = staticCompositionLocalOf { DarkColors }
```

- [ ] **Step 2: Define DLS Typography with Manrope and JetBrains Mono**

```kotlin
package com.apollo.roboarm.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.staticCompositionLocalOf

data class RoboArmTypography(
    val titleLg: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
    val monoMd: TextStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, fontWeight = FontWeight.Bold),
    val body: TextStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal)
)

val LocalRoboArmTypography = staticCompositionLocalOf { RoboArmTypography() }
```

- [ ] **Step 3: Setup Theme provider**

```kotlin
// In Theme.kt
@Composable
fun RoboArmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    CompositionLocalProvider(
        LocalRoboArmColors provides colors,
        LocalRoboArmTypography provides RoboArmTypography()
    ) {
        MaterialTheme(content = content)
    }
}
```

- [ ] **Step 4: Commit**
```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/ui/theme/
git commit -m "feat: implement DLS theme system"
```

---

### Task 3: Ktor Client and DI Setup

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/di/Modules.kt`
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/data/network/KtorClient.kt`
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/RoboArmApp.kt`
- Modify: `mobile/android/app/src/main/AndroidManifest.xml`

- [ ] **Step 1: Create Ktor Client Factory**

```kotlin
// mobile/android/app/src/main/java/com/apollo/roboarm/data/network/KtorClient.kt
fun createKtorClient() = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
    defaultRequest {
        url("http://10.0.2.2:8000") 
    }
}
```

- [ ] **Step 2: Setup Koin Module**

```kotlin
// mobile/android/app/src/main/java/com/apollo/roboarm/di/Modules.kt
val appModule = module {
    single { createKtorClient() }
}
```

- [ ] **Step 3: Initialize Koin in Application class and update Manifest**

```kotlin
// mobile/android/app/src/main/java/com/apollo/roboarm/RoboArmApp.kt
class RoboArmApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RoboArmApp)
            modules(appModule)
        }
    }
}
```

- [ ] **Step 4: Commit**
```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/
git commit -m "feat: setup Ktor client and Koin DI"
```
