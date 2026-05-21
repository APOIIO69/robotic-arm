package com.apollo.roboarm.data.models

enum class RobotStatus {
    OK, WARNING, CRITICAL, OFFLINE
}

fun String.toRobotStatus(): RobotStatus = try {
    RobotStatus.valueOf(this.uppercase())
} catch (e: Exception) {
    RobotStatus.OK
}
