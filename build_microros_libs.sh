#!/bin/bash
set -e

# Proxy check
export http_proxy=http://127.0.0.1:8118
export https_proxy=http://127.0.0.1:8118

if [ "$(id -u)" -eq 0 ]; then
    echo "Warning: Running this script with sudo/root is not recommended."
    echo "It may cause issues with rosdep and ROS 2 environment."
    echo "Please run it as a normal user: ./build_microros_libs.sh"
    # We continue, but this is likely the cause of rosdep issues
fi

echo "Updating rosdep..."
rosdep update --quiet || echo "rosdep update failed, continuing anyway..."

echo "Installing micro-ROS build dependencies..."
sudo apt update
sudo apt install -y python3-pip cmake git python3-vcstool

# Create a workspace for building micro-ROS
WORK_DIR="./microros_ws"
mkdir -p "$WORK_DIR"
cd "$WORK_DIR"

echo "Cloning micro-ros-build..."
if [ ! -d "src/micro_ros_setup" ]; then
    mkdir -p src
    git clone -b humble https://github.com/micro-ROS/micro_ros_setup.git src/micro_ros_setup
fi

echo "Sourcing ROS 2..."
if [ -f "/opt/ros/humble/setup.bash" ]; then
    source /opt/ros/humble/setup.bash
else
    echo "Error: ROS 2 Humble not found. Please run ./setup_ros2.sh first."
    exit 1
fi

echo "Building micro-ROS build tools..."
colcon build --packages-select micro_ros_setup

# Source the newly built tools
if [ -f "install/local_setup.bash" ]; then
    source install/local_setup.bash
else
    echo "Error: micro_ros_setup build failed or install directory not found."
    exit 1
fi

echo "Creating library workspace..."
# Clean up old firmware directory if it exists to avoid errors
if [ -d "firmware" ]; then
    echo "Removing existing firmware directory..."
    rm -rf firmware
fi
# For Ubuntu Host (Webots), we use 'host' as the RTOS type
ros2 run micro_ros_setup create_firmware_ws.sh host generic

echo "Building micro-ROS libraries..."
# We run colcon directly to avoid issues with the build_firmware.sh wrapper
# and to ensure --merge-install is used correctly.

# Clean up previous build artifacts to avoid isolated/merge layout conflicts
echo "Cleaning up previous build directories..."
rm -rf build install log

# 1. Build C typesupport
colcon build --merge-install \
    --packages-up-to rosidl_typesupport_microxrcedds_c \
    --metas src \
    --cmake-args -DBUILD_TESTING=OFF -DBUILD_SHARED_LIBS=ON

# 2. Build CPP typesupport
colcon build --merge-install \
    --packages-up-to rosidl_typesupport_microxrcedds_cpp \
    --metas src \
    --cmake-args -DBUILD_TESTING=OFF -DBUILD_SHARED_LIBS=ON

# Source the intermediate results
if [ -f "install/local_setup.bash" ]; then
    source install/local_setup.bash
fi

# 3. Build everything else
colcon build --merge-install \
    --metas src \
    --cmake-args -DBUILD_TESTING=OFF -DBUILD_SHARED_LIBS=ON

echo "Installing libraries to /usr/local..."
# Now all headers and libs are in the merged install/ directory
if [ -d "install/include" ]; then
    sudo cp -r install/include/* /usr/local/include/
    sudo cp -r install/lib/* /usr/local/lib/
else
    echo "Error: install directory not found. Build might have failed."
    exit 1
fi

echo ""
echo "Successfully built and installed micro-ROS libraries!"
