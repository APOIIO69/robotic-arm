# Robotic Arm Simulation Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a 2-axis robotic arm simulation in Webots controlled via micro-ROS and a ROS 2 edge server.

**Architecture:** Distributed control system with a Webots C controller (client) and a Docker-based ROS 2 environment (agent + control node). Communication via XRCE-DDS over UDP.

**Tech Stack:** Webots, micro-ROS, ROS 2 Humble, Docker, C, Python.

---

### Task 1: Docker Environment Setup

**Files:**
- Create: `docker/Dockerfile`
- Create: `docker/docker-compose.yml`

- [ ] **Step 1: Create Dockerfile with ROS 2 and micro-ROS Agent**
```dockerfile
FROM ros:humble-ros-base
RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    python3-pip \
    git \
    && rm -rf /var/lib/apt/lists/*
RUN mkdir -p /uros_ws/src && cd /uros_ws/src && \
    git clone -b humble https://github.com/micro-ROS/micro_ros_setup.git && \
    cd /uros_ws && rosdep update && rosdep install --from-paths src --ignore-src -y && \
    . /opt/ros/humble/setup.sh && colcon build && \
    . install/setup.sh && \
    ros2 run micro_ros_setup create_agent_ws.sh && \
    ros2 run micro_ros_setup build_agent.sh
WORKDIR /app
```

- [ ] **Step 2: Create docker-compose.yml for easy launch**
```yaml
version: '3.8'
services:
  uros-agent:
    build: 
      context: .
      dockerfile: Dockerfile
    network_mode: host
    command: bash -c "source /uros_ws/install/setup.bash && ros2 run micro_ros_agent micro_ros_agent udp4 --port 8888"
```

- [ ] **Step 3: Commit**
```bash
git add docker/
git commit -m "feat: setup docker environment for micro-ros agent"
```

### Task 2: Webots World and Robot Model (PROTO)

**Files:**
- Create: `simulation/protos/RoboArmL2.proto`
- Create: `simulation/worlds/robotic_arm.wbt`

- [ ] **Step 1: Write the PROTO file for the 2-axis arm**
```proto
#VRML_SIM R2023b utf8
PROTO RoboArmL2 [
  field SFVec3f    translation 0 0 0
  field SFRotation rotation    0 0 1 0
  field SFFloat    shoulderLength 0.45
  field SFFloat    forearmLength  0.4
]
{
  Robot {
    translation IS translation
    rotation IS rotation
    children [
      DEF BASE_LINK Solid {
        children [
          Shape { appearance PBRAppearance { baseColor 0.5 0.5 0.5 } geometry Cylinder { height 0.2 radius 0.1 } }
        ]
      }
      HingeJoint {
        jointParameters HingeJointParameters { axis 0 0 1 }
        device [ RotationalMotor { name "base_motor" } PositionSensor { name "base_sensor" } ]
        endPoint Solid {
          children [
            DEF SHOULDER Shape { appearance PBRAppearance { baseColor 0.8 0.1 0.1 } geometry Box { size 0.05 0.05 IS shoulderLength } }
            HingeJoint {
              jointParameters HingeJointParameters { axis 0 1 0 anchor 0 0 0.225 }
              device [ RotationalMotor { name "elbow_motor" } PositionSensor { name "elbow_sensor" } ]
              endPoint Solid {
                translation 0 0 0.45
                children [
                  DEF FOREARM Shape { appearance PBRAppearance { baseColor 0.1 0.1 0.8 } geometry Box { size 0.04 0.04 IS forearmLength } }
                ]
              }
            }
          ]
        }
      }
    ]
    controller "arm_controller"
  }
}
```

- [ ] **Step 2: Create the world file including the RoboArmL2**
```wbt
#VRML_SIM R2023b utf8
WorldInfo { basicTimeStep 10 }
Viewpoint { orientation -0.2 -0.2 0.9 2.5 position 2 2 1 }
TexturedBackground {}
TexturedBackgroundLight {}
RectangleArena {}
RoboArmL2 { translation 0 0 0.1 }
```

- [ ] **Step 3: Commit**
```bash
git add simulation/
git commit -m "feat: create webots world and arm model"
```

### Task 3: C Controller with micro-ROS Client

**Files:**
- Create: `simulation/controllers/arm_controller/arm_controller.c`
- Create: `simulation/controllers/arm_controller/Makefile`

- [ ] **Step 1: Implement basic Webots controller loop with micro-ROS init**
(Code will include micro-ROS headers and basic init logic)

- [ ] **Step 2: Add Publisher for /arm/state and Subscriber for /arm/command**
(Code will use sensor_msgs/JointState and std_msgs/Float64MultiArray)

- [ ] **Step 3: Commit**
```bash
git add simulation/controllers/
git commit -m "feat: implement C controller with micro-ROS integration"
```

### Task 4: ROS 2 Control Node (PID)

**Files:**
- Create: `server/arm_control_node.py`

- [ ] **Step 1: Implement ROS 2 Node with PID logic**
(Python code with rclpy and simple PID class)

- [ ] **Step 2: Commit**
```bash
git add server/
git commit -m "feat: implement ROS 2 PID control node"
```

### Task 5: Integration Testing

- [ ] **Step 1: Launch micro-ROS agent in Docker**
- [ ] **Step 2: Launch Webots simulation**
- [ ] **Step 3: Run ROS 2 Control Node**
- [ ] **Step 4: Verify arm reaches target angle**
```bash
ros2 topic pub /arm/command std_msgs/msg/Float64MultiArray "{data: [0.78, 1.57]}"
```
