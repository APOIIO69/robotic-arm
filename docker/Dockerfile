# Используем готовый официальный образ micro-ROS Agent
FROM microros/micro-ros-agent:humble

# Переходим под root для установки доп. пакетов
USER root

# Устанавливаем только то, что нужно для нашей Python-ноды управления
# Нам не нужно собирать агент, он уже там есть
RUN apt-get update && apt-get install -y \
    python3-pip \
    ros-humble-rclpy \
    ros-humble-std-msgs \
    ros-humble-sensor-msgs \
    && rm -rf /var/lib/apt/lists/*

# Создаем рабочую директорию
WORKDIR /app

# Настраиваем точку входа так, чтобы агент был доступен
# (Официальный образ использует свою логику, мы просто подготовим среду)
ENTRYPOINT ["/bin/bash", "-c", "source /opt/ros/humble/setup.bash && \"\$@\"", "--"]
