#!/bin/bash
# Настройка путей для ROS 2, micro-ROS и Webots
export ROS_DISTRO=humble
source /opt/ros/$ROS_DISTRO/setup.bash

export WEBOTS_HOME=/snap/webots/current/usr/share/webots
export MICRO_ROS_PATH=/usr/local

# Указываем использовать micro-ROS как middleware
export RMW_IMPLEMENTATION=rmw_microxrcedds

# Объединяем все пути к библиотекам
export LD_LIBRARY_PATH=$MICRO_ROS_PATH/lib:$WEBOTS_HOME/lib/controller:$LD_LIBRARY_PATH

echo "Starting arm_controller in EXTERN mode with RMW_IMPLEMENTATION=$RMW_IMPLEMENTATION..."
./arm_controller
