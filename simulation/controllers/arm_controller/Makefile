# Webots Makefile for C controllers
C_SOURCES = arm_controller.c

# ROS 2 and micro-ROS paths
ROS_DISTRO = humble
ROS_PATH = /opt/ros/$(ROS_DISTRO)
MICRO_ROS_PATH = /usr/local

# Include paths
INCLUDE = -I$(MICRO_ROS_PATH)/include \
	-I$(MICRO_ROS_PATH)/include/rclc \
	-I$(MICRO_ROS_PATH)/include/rmw_microxrcedds_c \
	-I$(MICRO_ROS_PATH)/include/microxrcedds_client \
	-I$(MICRO_ROS_PATH)/include/sensor_msgs \
	-I$(MICRO_ROS_PATH)/include/std_msgs \
	-I$(MICRO_ROS_PATH)/include/builtin_interfaces \
	-I$(MICRO_ROS_PATH)/include/action_msgs \
	-I$(MICRO_ROS_PATH)/include/unique_identifier_msgs \
	-I$(ROS_PATH)/include/rcl \
	-I$(ROS_PATH)/include/rcl_action \
	-I$(ROS_PATH)/include/rcutils \
	-I$(ROS_PATH)/include/rmw \
	-I$(ROS_PATH)/include/rosidl_runtime_c \
	-I$(ROS_PATH)/include/rosidl_typesupport_interface \
	-I$(ROS_PATH)/include/rcl_yaml_param_parser \
	-I$(ROS_PATH)/include/rosidl_typesupport_introspection_c

# Library paths and libraries
LIBRARIES = -L$(MICRO_ROS_PATH)/lib \
	-L$(ROS_PATH)/lib \
	-lrclc -lrmw_microxrcedds -lmicroxrcedds_client \
	-lrcl -lrcutils -lrcl_action \
	-lrosidl_runtime_c -lrosidl_typesupport_c \
	-lrosidl_typesupport_microxrcedds_c \
	-lsensor_msgs__rosidl_typesupport_microxrcedds_c -lsensor_msgs__rosidl_generator_c \
	-lstd_msgs__rosidl_typesupport_microxrcedds_c -lstd_msgs__rosidl_generator_c

# Webots Home for Snap installation
WEBOTS_HOME = /snap/webots/current/usr/share/webots
include $(WEBOTS_HOME)/resources/Makefile.include
