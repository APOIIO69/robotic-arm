# [Mobile Home Screen & Navigation] Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the Home Screen (Dashboard & Lines list) with full MVI logic, Ktor repository, and Type-Safe Navigation.

**Architecture:** MVI (Model-View-Intent) pattern. ViewModel interacts with a Repository that uses Ktor Client. Navigation is handled via Jetpack Navigation Compose with Kotlin DSL.

**Tech Stack:** Kotlin, Jetpack Compose, Ktor, Koin, Jetpack Navigation.

---

### Task 1: Data Models and API Repository

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/data/models/ApiModels.kt`
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/data/repository/RoboArmRepository.kt`
- Modify: `mobile/android/app/src/main/java/com/apollo/roboarm/di/Modules.kt`

- [ ] **Step 1: Define API data models matching the backend**

```kotlin
package com.apollo.roboarm.data.models

import kotlinx.serialization.Serializable

@Serializable
data class LineDto(
    val id: Int,
    val name: String,
    val description: String,
    val status: String,
    val robots_count: Int
)

@Serializable
data class LinesResponse(val lines: List<LineDto>)
```

- [ ] **Step 2: Implement the Repository using Ktor**

```kotlin
class RoboArmRepository(private val client: HttpClient) {
    suspend fun getLines(): Result<List<LineDto>> = runCatching {
        client.get("/api/lines").body<LinesResponse>().lines
    }
}
```

- [ ] **Step 3: Register Repository in Koin module**

```kotlin
// Modules.kt
val appModule = module {
    single { createKtorClient() }
    single { RoboArmRepository(get()) }
    viewModel { HomeViewModel(get()) }
}
```

- [ ] **Step 4: Commit**

```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/data/ mobile/android/app/src/main/java/com/apollo/roboarm/di/Modules.kt
git commit -m "feat: add API models and repository with Koin integration"
```

---

### Task 2: Home Screen MVI Logic

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/home/HomeContract.kt`
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/home/HomeViewModel.kt`

- [ ] **Step 1: Define Home Screen Contract (State, Intent, Effect)**

```kotlin
data class HomeState(
    val isLoading: Boolean = false,
    val lines: List<LineDto> = emptyList(),
    val error: String? = null
)

sealed class HomeIntent {
    object LoadLines : HomeIntent()
    data class SelectLine(val lineId: Int) : HomeIntent()
}

sealed class HomeEffect {
    data class NavigateToLine(val lineId: Int) : HomeEffect()
}
```

- [ ] **Step 2: Implement HomeViewModel**

```kotlin
class HomeViewModel(private val repository: RoboArmRepository) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadLines -> loadLines()
            is HomeIntent.SelectLine -> viewModelScope.launch { _effect.emit(HomeEffect.NavigateToLine(intent.lineId)) }
        }
    }

    private fun loadLines() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            repository.getLines()
                .onSuccess { lines -> _state.update { it.copy(isLoading = false, lines = lines) } }
                .onFailure { err -> _state.update { it.copy(isLoading = false, error = err.message) } }
        }
    }
}
```

- [ ] **Step 3: Commit**

```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/home/
git commit -m "feat: implement Home screen MVI logic (Contract & ViewModel)"
```

---

### Task 3: Home Screen UI and Navigation Setup

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/navigation/NavGraph.kt`
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/home/HomeScreen.kt`
- Modify: `mobile/android/app/src/main/java/com/apollo/roboarm/MainActivity.kt`

- [ ] **Step 1: Define Navigation Destinations**

```kotlin
@Serializable object Home
@Serializable data class LineDetail(val id: Int)
```

- [ ] **Step 2: Implement HomeScreen Composable**

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel(), onNavigateToLine: (Int) -> Unit) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeIntent.LoadLines)
        viewModel.effect.collect { effect ->
            if (effect is HomeEffect.NavigateToLine) onNavigateToLine(effect.lineId)
        }
    }

    Scaffold(topBar = { /* SearchBar from DLS 6.8 */ }) { padding ->
        // Use StatCard (DLS 6.3) for Dashboard
        // Use LineCard (DLS 6.4) for list
    }
}
```

- [ ] **Step 3: Setup NavHost in MainActivity**

```kotlin
// NavGraph.kt
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Home) {
        composable<Home> { HomeScreen(onNavigateToLine = { id -> navController.navigate(LineDetail(id)) }) }
        composable<LineDetail> { /* TODO: LineDetailScreen */ }
    }
}
```

- [ ] **Step 4: Commit**

```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/ui/navigation/ mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/home/HomeScreen.kt
git commit -m "feat: setup navigation and Home screen UI"
```
