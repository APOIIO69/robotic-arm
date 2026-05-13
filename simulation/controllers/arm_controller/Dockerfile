# Используем ROS 2 Humble как базу для контроллера
FROM mirror.gcr.io/library/ros:humble-ros-base

# Аргументы для прокси
ARG http_proxy
ARG https_proxy
ENV http_proxy=$http_proxy
ENV https_proxy=$https_proxy

# Используем bash для поддержки source и расширенных команд
SHELL ["/bin/bash", "-c"]

# Переходим под root
USER root

# Устанавливаем зависимости
RUN apt-get update && apt-get install -y \
    build-essential \
    cmake \
    git \
    python3-pip \
    python3-colcon-common-extensions \
    && rm -rf /var/lib/apt/lists/*

# Создаем воркспейс для сборки клиентской библиотеки micro-ROS
WORKDIR /uros_ws
RUN mkdir -p src && cd src && \
    git clone -b humble https://github.com/micro-ROS/micro_ros_setup.git

# Настраиваем зеркало для rosdep
ENV ROSDISTRO_INDEX_URL=https://mirrors.tuna.tsinghua.edu.cn/rosdistro/index-v4.yaml
RUN source /opt/ros/humble/setup.bash && \
    rosdep update --include-eol-distros && \
    rosdep install --from-paths src --ignore-src -y

# Собираем утилиты установки
RUN source /opt/ros/humble/setup.bash && colcon build

# Создаем воркспейс клиента (используем bash напрямую для надежности)
RUN source /opt/ros/humble/setup.bash && source install/setup.bash && \
    ros2 run micro_ros_setup create_client_ws.sh -n host

# Собираем клиентскую библиотеку
RUN source /opt/ros/humble/setup.bash && source install/setup.bash && \
    ros2 run micro_ros_setup build_client.sh

# Копируем исходный код нашего контроллера
WORKDIR /app
COPY . .

# Собираем наш контроллер
ENV MICRO_ROS_INCLUDE_DIR=/uros_ws/install/include
ENV MICRO_ROS_LIB_DIR=/uros_ws/install/lib

RUN source /opt/ros/humble/setup.bash && \
    make WEBOTS_HOME=/usr

# Команда запуска
CMD ["./arm_controller"]
