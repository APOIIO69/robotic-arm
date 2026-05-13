# Design Spec: Robotic Arm Simulation (Webots + ROS 2)

**Date:** 2026-05-13
**Topic:** 2-Axis Robotic Arm Prototype
**Status:** Approved

## 1. Overview
Development of a 2-axis robotic arm simulation for an educational project. The system includes a physical simulation in Webots, a low-level C controller (simulating STM32) with micro-ROS, and a high-level control server in Docker (simulating Raspberry Pi with ROS 2).

## 2. Physical Specifications (TTX)
- **Model Name:** RoboArm-L2 (Laboratory Edition)
- **Kinematic Scheme:**
  - **Axis 1 (Base):** Horizontal rotation, range [0, 360°] (0 to 2π rad).
  - **Axis 2 (Elbow):** Vertical rotation, range [0, 180°] (0 to π rad).
- **Dimensions:**
  - Base height: 20 cm.
  - Link 1 (Shoulder): 45 cm.
  - Link 2 (Forearm): 40 cm.
- **Payload:** 1-2 kg at the end-effector (probe).
- **Actuators:** High-torque rotational motors with torque feedback enabled.
- **Sensors:**
  - Position sensors (encoders) on each joint.
  - Torque sensors on each motor.

## 3. Software Architecture
### 3.1. Local Simulation (Webots)
- **Environment:** Webots R2023b (or latest).
- **Controller:** C-based controller.
- **Communication:** micro-ROS client library (linked statically).
- **Transport:** UDP via localhost (Host OS to Docker).

### 3.2. Edge Server (Docker)
- **OS/Middleware:** Ubuntu + ROS 2 (Humble/Iron).
- **Components:**
  - **micro-ROS Agent:** Bridges UDP traffic to the ROS 2 graph.
  - **Control Node:** Implements PID control logic in Joint Space.
  - **Kinematics Engine:** Prepared for future Forward/Inverse kinematics expansion.

## 4. Communication Interface (API)
### 4.1. Telemetry (from Webots to ROS 2)
- **Topic:** `/arm/state`
- **Type:** `sensor_msgs/msg/JointState`
- **Rate:** 100 Hz (10ms cycle).
- **Fields:** Position (rad), Effort (Nm).

### 4.2. Commands (from ROS 2 to Webots)
- **Topic:** `/arm/command`
- **Type:** `std_msgs/msg/Float64MultiArray` (Targets for motors).

## 5. Implementation Roadmap
1. **Phase 1:** Webots world creation and basic C controller skeleton.
2. **Phase 2:** Docker environment setup with micro-ROS Agent.
3. **Phase 3:** Integration of micro-ROS into the C controller.
4. **Phase 4:** Implementation of the PID Control Node in ROS 2.
5. **Phase 5:** Verification and testing of positioning accuracy.

## 6. Future Extensions
- Web-based Dashboard (FastAPI/React).
- Joystick control integration.
- Diagnostic telemetry (temperature/current simulation).
