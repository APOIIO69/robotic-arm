#!/bin/bash
set -e

# Proxy check
export http_proxy=http://127.0.0.1:8118
export https_proxy=http://127.0.0.1:8118

echo "Configuring locale..."
sudo apt update && sudo apt install locales -y
sudo locale-gen en_US en_US.UTF-8
sudo update-locale LC_ALL=en_US.UTF-8 LANG=en_US.UTF-8
export LANG=en_US.UTF-8

echo "Adding ROS 2 GPG key and repository..."
sudo apt install software-properties-common -y
sudo add-apt-repository universe -y
sudo apt update && sudo apt install curl -y
sudo curl -sSL https://raw.githubusercontent.com/ros/rosdistro/master/ros.key -o /usr/share/keyrings/ros-archive-keyring.gpg
# Hardcoded jammy for Ubuntu 22.04 to avoid command substitution
echo "deb [arch=amd64 signed-by=/usr/share/keyrings/ros-archive-keyring.gpg] http://packages.ros.org/ros2/ubuntu jammy main" | sudo tee /etc/apt/sources.list.d/ros2.list > /dev/null

echo "Installing ROS 2 Humble..."
sudo apt update
sudo apt install ros-humble-ros-base python3-colcon-common-extensions -y
sudo apt install ros-humble-sensor-msgs ros-humble-std-msgs -y

echo "Installing micro-ROS build tools..."
sudo apt install python3-rosdep -y
sudo rosdep init || true
rosdep update

echo "Done! Run 'source /opt/ros/humble/setup.bash' to use ROS 2."
