# Design Spec — Backend Enhancements for Mobile App

**Date:** 2026-05-17  
**Status:** Draft  
**Topic:** SQLite Integration and Extended API for RoboArm Mobile

---

## 1. Overview
The goal is to enhance the existing `web_bridge.py` backend to support a full-featured mobile application. This involves adding persistent storage (SQLite), implementing a hierarchical data model (Lines -> Robots -> Sensors), and expanding the API to match the mobile app's requirements.

---

## 2. Architecture & Data Strategy

### 2.1 Hybrid Storage Approach
- **SQLite (SQLAlchemy):** Persistent storage for configuration (Lines, Robots, Sensor thresholds) and historical data (Notifications).
- **In-Memory (RAM):** Real-time telemetry from ROS 2 topics. Live values are served directly to clients via WebSocket or polling without hitting the database.
- **Data Merger:** A logic layer that combines persistent metadata from SQLite with live values from ROS 2 to produce a complete `Robot` / `Sensor` object.

### 2.2 Database Schema
Using SQLAlchemy models:
- **`Line`**: `id`, `name`, `description`.
- **`Robot`**: `id`, `line_id`, `name`, `model`, `type`, `ros_topic_prefix` (e.g., `/arm`), `has_gripper`, `has_torch`, etc.
- **`Sensor`**: `id`, `robot_id`, `label`, `type` (e.g., `temperature`), `unit`, `normal_min`, `normal_max`.
- **`Notification`**: `id`, `type` (`critical`/`warning`), `robot_name`, `line_name`, `message`, `timestamp`.

---

## 3. Core Components

### 3.1 ROS 2 Bridge (Updated)
- Continues to handle multi-threaded ROS 2 communication.
- Dynamically creates publishers/subscribers based on the `Robot` configurations in the database.

### 3.2 Database Manager
- Manages connection to `roboarm.db`.
- Handles CRUD operations for lines, robots, and notifications.
- Seed data functionality for initial setup.

### 3.3 Health Monitor (Background Task)
- Monitors live sensor values against `normal_min`/`normal_max` thresholds.
- Triggers new `Notification` records in SQLite when a threshold is breached.

---

## 4. API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/lines` | Returns all production lines with summarized statuses. |
| `GET` | `/api/lines/{id}/robots` | Returns all robots for a specific line. |
| `GET` | `/api/robots/{id}/telemetry` | Returns merged metadata and live telemetry for a robot. |
| `POST` | `/api/robots/{id}/command` | Sends control commands (`ESTOP`, `SET_AXIS`, `ACTION`). |
| `GET` | `/api/notifications` | Returns historical alerts from SQLite. |
| `POST` | `/api/auth/login` | Simple session-based auth (MVP). |

---

## 5. Testing & Validation
- **Unit Tests:** For Database Manager (CRUD operations).
- **Integration Tests:** Verifying the "Data Merger" logic (RAM + DB).
- **Manual Verification:** Using `curl` or Postman to verify all new API endpoints return expected structures from the mobile TZ.

---

## 6. Implementation Notes
- Use `FastAPI` for the web server.
- Use `SQLAlchemy` for ORM.
- Maintain existing ROS 2 threading logic but make it dynamic.
