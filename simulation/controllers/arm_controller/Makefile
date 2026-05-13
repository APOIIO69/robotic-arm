# Webots Makefile for C controllers
# Webots standard Makefile should be included at the end

# List of your source files
C_SOURCES = arm_controller.c

# micro-ROS include and library paths
# These should be adjusted to the actual location of micro-ROS libraries in the environment
MICRO_ROS_INCLUDE_DIR ?= /usr/local/include
MICRO_ROS_LIB_DIR ?= /usr/local/lib

# Add micro-ROS to include paths
INCLUDE = -I$(MICRO_ROS_INCLUDE_DIR)

# Add micro-ROS libraries
LIBRARIES = -L$(MICRO_ROS_LIB_DIR) -lrcl -lrclc -lrcutils -lrosidl_runtime_c -lrosidl_typesupport_c -lrosidl_typesupport_microxrcedds_c -lmicroxrcedds_client -lrmw_microxrcedds_c

# Default Webots Makefile logic
null :=
space := $(null) $(null)
WEBOTS_HOME_PATH?=$(subst $(space),\ ,$(strip $(subst \,/,$(WEBOTS_HOME))))
include $(WEBOTS_HOME_PATH)/resources/projects/default/controllers/Makefile.include
