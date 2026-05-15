#!/bin/bash
set -e

# Proxy check
export http_proxy=http://127.0.0.1:8118
export https_proxy=http://127.0.0.1:8118

echo "Installing micro-ROS Agent natively..."

# Create workspace
WORK_DIR="./microros_agent_ws"
mkdir -p "$WORK_DIR/src"
cd "$WORK_DIR"

# Clone agent
if [ ! -d "src/micro-ROS-Agent" ]; then
    git clone -b humble https://github.com/micro-ROS/micro-ROS-Agent.git src/micro-ROS-Agent
fi

# Source ROS 2
source /opt/ros/humble/setup.bash

# Install dependencies
sudo apt update
rosdep update
rosdep install --from-paths src --ignore-src -y

# Build
colcon build --cmake-args -DUID_AGENT=OFF

echo ""
echo "micro-ROS Agent built successfully!"
echo "To run it: source $WORK_DIR/install/local_setup.bash && ros2 run micro_ros_agent micro_ros_agent udp4 --port 8888"
