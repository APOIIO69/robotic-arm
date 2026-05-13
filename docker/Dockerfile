FROM mirror.gcr.io/library/ros:humble-ros-base
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
