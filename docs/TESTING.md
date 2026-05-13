# Integration Testing Guide

This document describes how to set up and run the integration test for the Robotic Arm project, connecting the Webots simulation with the ROS 2 control logic via micro-ROS.

## Prerequisites

1.  **Webots:** Download and install Webots from [cyberbotics.com](https://cyberbotics.com/).
2.  **Docker & Docker Compose:** Required to run the micro-ROS agent.
3.  **ROS 2 Humble:** Required to run the server-side control node.
4.  **micro-ROS Client Libraries:** (For Webots controller compilation) Ensure you have micro-ROS client libraries installed or available for the Webots compiler.

## Step 1: Start the micro-ROS Agent

The micro-ROS agent acts as a bridge between the low-level controller (Webots) and the ROS 2 network. It runs inside a Docker container.

1.  Navigate to the `docker/` directory:
    ```bash
    cd docker
    ```
2.  Build and start the agent:
    ```bash
    docker-compose up -d --build
    ```
3.  Verify the agent is running and listening on UDP port 8888:
    ```bash
    docker-compose logs -f
    ```

## Step 2: Launch and Compile Webots Simulation

1.  Launch **Webots**.
2.  Open the world file: `simulation/worlds/robotic_arm.wbt`.
3.  **Compile the Controller:**
    - If the arm doesn't move or connection fails, you may need to compile the controller.
    - Open the `arm_controller.c` file in the Webots text editor.
    - Click the **Build** (gear icon) button.
    - *Note: Ensure the micro-ROS headers and libraries are in your include/library path as specified in `simulation/controllers/arm_controller/Makefile`.*
4.  Once compiled, click **Play** to start the simulation.
5.  Check the Webots console for "micro-ROS node initialized" message.

## Step 3: Run the ROS 2 Control Node

The control node implements the PID logic and sends commands to the arm based on feedback.

1.  Open a new terminal.
2.  Source your ROS 2 environment:
    ```bash
    source /opt/ros/humble/setup.bash
    ```
3.  Run the control node:
    ```bash
    python3 server/arm_control_node.py
    ```
    *If ROS 2 is not installed on your host, you can run this node inside another ROS 2 Docker container sharing the host network.*

## Step 4: Verification and Interaction

1.  **Check Topics:**
    ```bash
    ros2 topic list
    ```
    You should see:
    - `/arm/state` (published by Webots)
    - `/arm/command` (published by the control node)

2.  **Monitor Telemetry:**
    ```bash
    ros2 topic echo /arm/state
    ```

3.  **Manually Send Commands:**
    If you want to bypass the control node and test raw movements:
    ```bash
    ros2 topic pub /arm/command std_msgs/msg/Float64MultiArray "{data: [0.7, -0.3]}"
    ```

## Troubleshooting

-   **UDP Port Conflict:** If port 8888 is already in use, the agent will fail to start.
-   **Docker Network:** On Linux, `network_mode: host` allows the container to use the host's network stack directly. On macOS/Windows, you might need to use `127.0.0.1` and ensure port 8888 is forwarded.
-   **Build Failures:** Ensure all micro-ROS dependencies are satisfied. The controller expects `rclc`, `rcl`, and other micro-ROS libraries.
