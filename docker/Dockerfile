FROM mirror.gcr.io/library/ros:humble-ros-base

RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    python3-pip \
    python3-colcon-common-extensions \
    python3-vcstool \
    git \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /uros_ws

# 1. Сначала клонируем репозиторий установки
RUN mkdir -p src && cd src && \
    git clone -b humble https://github.com/micro-ROS/micro_ros_setup.git

# 2. Обновляем rosdep, используя стабильное зеркало
# Это решает проблему блокировки raw.githubusercontent.com
ENV ROSDISTRO_INDEX_URL=https://mirrors.tuna.tsinghua.edu.cn/rosdistro/index-v4.yaml
RUN . /opt/ros/humble/setup.sh && \
    rosdep update --include-eol-distros && \
    rosdep install --from-paths src --ignore-src -y

# 3. Собираем утилиты установки
RUN . /opt/ros/humble/setup.sh && colcon build

# 4. Создаем воркспейс агента
RUN . /opt/ros/humble/setup.sh && . install/setup.sh && \
    ros2 run micro_ros_setup create_agent_ws.sh

# 5. Собираем сам агент
RUN . /opt/ros/humble/setup.sh && . install/setup.sh && \
    ros2 run micro_ros_setup build_agent.sh

WORKDIR /app
