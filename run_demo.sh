#!/bin/bash

# RoboticArm Demo Launch Script
# This script helps to start the micro-ROS agent and provides instructions for the demo.

set -e

echo "=== RoboticArm Demo Setup ==="

# Check for Docker
if ! [ -x "$(command -v docker)" ]; then
  echo 'Error: docker is not installed. Please install Docker and try again.' >&2
  exit 1
fi

# Check for Docker Compose
if ! [ -x "$(command -v docker-compose)" ]; then
    if ! docker compose version > /dev/null 2>&1; then
        echo 'Error: docker-compose is not installed. Please install Docker Compose and try again.' >&2
        exit 1
    fi
    DOCKER_COMPOSE="docker compose"
else
    DOCKER_COMPOSE="docker-compose"
fi

echo "Step 1: Starting micro-ROS agent..."
# Assuming native installation
source microros_agent_ws/install/local_setup.bash
ros2 run micro_ros_agent micro_ros_agent udp4 --port 8888 &
AGENT_PID=$!

echo ""
echo "Agent started successfully (PID: $AGENT_PID)! It is listening on UDP port 8888."
echo ""
echo "Step 2: Start Webots Simulation"
echo "  1. Open Webots."
echo "  2. Load the world: simulation/worlds/robotic_arm.wbt"
echo "  3. Ensure the 'arm_controller' is compiled. If not, click 'Build' in the Webots text editor."
echo "  4. The controller will automatically connect to the agent on localhost:8888."
echo ""
echo "Step 3: Run ROS 2 Control Node (in a separate terminal)"
echo "  1. Ensure you have ROS 2 Humble installed."
echo "  2. Source your ROS 2 workspace: source /opt/ros/humble/setup.bash"
echo "  3. Run the node: python3 server/arm_control_node.py"
echo ""
echo "Step 4: Verification"
echo "  - Use 'ros2 topic list' to see active topics."
echo "  - Use 'ros2 topic echo /arm/state' to see telemetry."
echo "  - Send a command manually: ros2 topic pub /arm/command std_msgs/msg/Float64MultiArray \"{data: [0.5, -0.5]}\""
echo ""
echo "To view agent logs: cd docker && $DOCKER_COMPOSE logs -f"
echo "To stop the agent: cd docker && $DOCKER_COMPOSE down"
echo "=============================="
