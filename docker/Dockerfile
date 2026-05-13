FROM mirror.gcr.io/library/ros:humble-ros-base

RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    python3-pip \
    git \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /uros_ws

# 1. Сначала клонируем репозиторий установки
RUN mkdir -p src && cd src && \
    git clone -b humble https://github.com/micro-ROS/micro_ros_setup.git

# 2. Обновляем rosdep с повтором при неудаче
RUN . /opt/ros/humble/setup.sh && \
    (rosdep update || (sleep 5 && rosdep update)) && \
    rosdep install --from-paths src --ignore-src -y

# 3. Собираем утилиты установки
RUN . /opt/ros/humble/setup.sh && colcon build

# 4. Создаем воркспейс агента (этот шаг тоже качает много данных)
RUN . /opt/ros/humble/setup.sh && . install/setup.sh && \
    ros2 run micro_ros_setup create_agent_ws.sh

# 5. Собираем сам агент
RUN . /opt/ros/humble/setup.sh && . install/setup.sh && \
    ros2 run micro_ros_setup build_agent.sh

WORKDIR /app
