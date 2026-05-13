# Используем ROS 2 Humble как базу для контроллера
FROM ros:humble-ros-base

# Устанавливаем зависимости для сборки и micro-ROS
RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    git \
    python3-pip \
    && rm -rf /var/lib/apt/lists/*

# Создаем воркспейс для сборки клиентской библиотеки micro-ROS
WORKDIR /uros_ws
RUN mkdir -p src && cd src && \
    git clone -b humble https://github.com/micro-ROS/micro_ros_setup.git && \
    cd /uros_ws && . /opt/ros/humble/setup.sh && colcon build && \
    . install/setup.sh && \
    ros2 run micro_ros_setup create_client_ws.sh -n host

# Собираем клиентскую библиотеку
RUN . /opt/ros/humble/setup.sh && . install/setup.sh && \
    ros2 run micro_ros_setup build_client.sh

# Копируем исходный код нашего контроллера
WORKDIR /app
COPY . .

# Собираем контроллер, указывая пути к библиотекам micro-ROS
# (Пути в образе: /uros_ws/install/include и /uros_ws/install/lib)
ENV MICRO_ROS_INCLUDE_DIR=/uros_ws/install/include
ENV MICRO_ROS_LIB_DIR=/uros_ws/install/lib

# Для Webots Docker контроллеров нужно, чтобы бинарник назывался так же, как папка
RUN . /opt/ros/humble/setup.sh && \
    make WEBOTS_HOME=/usr

# Команда запуска (Webots сам вызовет бинарник)
CMD ["./arm_controller"]
