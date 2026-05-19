# [Mobile Robot Details] Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement the Robot Details screen with real-time telemetry visualization and control capabilities (Axis sliders, Emergency Stop).

**Architecture:** MVI pattern. ViewModel polls telemetry data from the repository and sends control commands. UI uses a Tabbed layout (Telemetry/Control).

**Tech Stack:** Kotlin, Jetpack Compose, Ktor, Koin.

---

### Task 1: Detailed Models and Repository Updates

**Files:**
- Modify: `mobile/android/app/src/main/java/com/apollo/roboarm/data/models/ApiModels.kt`
- Modify: `mobile/android/app/src/main/java/com/apollo/roboarm/data/repository/RoboArmRepository.kt`

- [ ] **Step 1: Add detailed Robot and Sensor models**

```kotlin
// ApiModels.kt
@Serializable
data class RobotDetailDto(
    val id: Int,
    val name: String,
    val model: String,
    val status: String
)

@Serializable
data class SensorDto(
    val id: Int,
    val label: String,
    val type: String,
    val unit: String,
    val value: Float,
    val status: String
)

@Serializable
data class RobotTelemetryResponse(
    val robot: RobotDetailDto,
    val sensors: List<SensorDto>
)

@Serializable
data class CommandRequest(
    val robot_id: Int,
    val angles: List<Float>
)
```

- [ ] **Step 2: Update Repository with telemetry and command methods**

```kotlin
// RoboArmRepository.kt
suspend fun getRobotTelemetry(robotId: Int): Result<RobotTelemetryResponse> = runCatching {
    client.get("/api/robots/$robotId/telemetry").body<RobotTelemetryResponse>()
}

suspend fun sendCommand(robotId: Int, angles: List<Float>): Result<Unit> = runCatching {
    client.post("/api/command") {
        setBody(CommandRequest(robot_id = robotId, angles = angles))
        contentType(ContentType.Application.Json)
    }.body()
}
```

- [ ] **Step 3: Commit**

```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/data/
git commit -m "feat: add telemetry models and update repository with command support"
```

---

### Task 2: Robot Details MVI Logic

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/details/RobotDetailsContract.kt`
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/details/RobotDetailsViewModel.kt`
- Modify: `mobile/android/app/src/main/java/com/apollo/roboarm/di/Modules.kt`

- [ ] **Step 1: Define Robot Details Contract**

```kotlin
data class RobotDetailsState(
    val isLoading: Boolean = false,
    val robot: RobotDetailDto? = null,
    val sensors: List<SensorDto> = emptyList(),
    val error: String? = null,
    val isEmergencyStopped: Boolean = false
)

sealed class RobotDetailsIntent {
    data class LoadTelemetry(val robotId: Int) : RobotDetailsIntent()
    data class UpdateAxis(val robotId: Int, val index: Int, val value: Float) : RobotDetailsIntent()
    object ToggleEmergencyStop : RobotDetailsIntent()
}
```

- [ ] **Step 2: Implement RobotDetailsViewModel with polling**

```kotlin
class RobotDetailsViewModel(private val repository: RoboArmRepository) : ViewModel() {
    private val _state = MutableStateFlow(RobotDetailsState())
    val state = _state.asStateFlow()

    private var pollingJob: Job? = null

    fun handleIntent(intent: RobotDetailsIntent) {
        when (intent) {
            is RobotDetailsIntent.LoadTelemetry -> startPolling(intent.robotId)
            is RobotDetailsIntent.UpdateAxis -> sendCommand(intent.robotId, intent.index, intent.value)
            is RobotDetailsIntent.ToggleEmergencyStop -> { /* Logic for ESTOP */ }
        }
    }

    private fun startPolling(robotId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                repository.getRobotTelemetry(robotId)
                    .onSuccess { data -> _state.update { it.copy(robot = data.robot, sensors = data.sensors) } }
                delay(2000) // Poll every 2 seconds
            }
        }
    }
    
    private fun sendCommand(robotId: Int, index: Int, value: Float) {
        // Implementation for sending axis values
    }
}
```

- [ ] **Step 3: Register ViewModel in Koin**

```kotlin
// Modules.kt
viewModel { RobotDetailsViewModel(get()) }
```

- [ ] **Step 4: Commit**

```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/details/ mobile/android/app/src/main/java/com/apollo/roboarm/di/Modules.kt
git commit -m "feat: implement Robot Details MVI logic and ViewModel"
```

---

### Task 3: Robot Details UI Implementation

**Files:**
- Create: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/details/RobotDetailsScreen.kt`
- Modify: `mobile/android/app/src/main/java/com/apollo/roboarm/ui/navigation/NavGraph.kt`

- [ ] **Step 1: Implement RobotDetailsScreen with Tabs**

```kotlin
@Composable
fun RobotDetailsScreen(robotId: Int, viewModel: RobotDetailsViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(robotId) {
        viewModel.handleIntent(RobotDetailsIntent.LoadTelemetry(robotId))
    }

    Scaffold(topBar = { /* Header from DLS 6.15 */ }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("Телеметрия") }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Управление") }
            }
            if (selectedTab == 0) TelemetryTab(state.sensors)
            else ControlTab(state)
        }
    }
}
```

- [ ] **Step 2: Implement Telemetry and Control Tabs using DLS components**

```kotlin
@Composable
fun TelemetryTab(sensors: List<SensorDto>) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(sensors) { sensor ->
            SensorTile(
                label = sensor.label,
                value = sensor.value.toString(),
                unit = sensor.unit,
                status = mapStatus(sensor.status),
                progress = null // Map from DLS rules
            )
        }
    }
}
```

- [ ] **Step 3: Update NavGraph to include RobotDetails**

```kotlin
// NavGraph.kt
@Serializable data class RobotDetail(val id: Int)

// Inside NavHost
composable<RobotDetail> { backStackEntry ->
    val detail: RobotDetail = backStackEntry.toRoute()
    RobotDetailsScreen(robotId = detail.id)
}
```

- [ ] **Step 4: Commit**

```bash
git add mobile/android/app/src/main/java/com/apollo/roboarm/ui/screens/details/RobotDetailsScreen.kt mobile/android/app/src/main/java/com/apollo/roboarm/ui/navigation/NavGraph.kt
git commit -m "feat: implement Robot Details UI and navigation"
```
