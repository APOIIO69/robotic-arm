#include <webots/robot.h>
#include <webots/motor.h>
#include <webots/position_sensor.h>

#include <rcl/rcl.h>
#include <rcl/error_handling.h>
#include <rclc/rclc.h>
#include <rclc/executor.h>

#include <sensor_msgs/msg/joint_state.h>
#include <std_msgs/msg/float64_multi_array.h>

#include <stdio.h>
#include <unistd.h>

#define TIME_STEP 32

// Handle for motors and sensors
WbDeviceTag base_motor, elbow_motor;
WbDeviceTag base_sensor, elbow_sensor;

// micro-ROS variables
rcl_publisher_t state_publisher;
rcl_subscription_t command_subscriber;
sensor_msgs__msg__JointState state_msg;
std_msgs__msg__Float64MultiArray command_msg;

double target_positions[2] = {0.0, 0.0};

void command_callback(const void *msgin) {
    const std_msgs__msg__Float64MultiArray *msg = (const std_msgs__msg__Float64MultiArray *)msgin;
    if (msg->data.size >= 2) {
        target_positions[0] = msg->data.data[0];
        target_positions[1] = msg->data.data[1];
    }
}

int main(int argc, char **argv) {
    wb_robot_init();

    base_motor = wb_robot_get_device("base_motor");
    elbow_motor = wb_robot_get_device("elbow_motor");
    base_sensor = wb_robot_get_device("base_sensor");
    elbow_sensor = wb_robot_get_device("elbow_sensor");

    wb_position_sensor_enable(base_sensor, TIME_STEP);
    wb_position_sensor_enable(elbow_sensor, TIME_STEP);

    // Initial targets
    wb_motor_set_position(base_motor, 0.0);
    wb_motor_set_position(elbow_motor, 0.0);

    // micro-ROS initialization
    rcl_allocator_t allocator = rcl_get_default_allocator();
    rclc_support_t support;
    rcl_ret_t rc;

    // Initialize support with default options (assumes agent is reachable)
    rc = rclc_support_init(&support, 0, NULL, &allocator);
    if (rc != RCL_RET_OK) {
        printf("Error initializing micro-ROS support\n");
        return -1;
    }

    // Initialize node
    rcl_node_t node;
    rc = rclc_node_init_default(&node, "webots_arm_node", "", &support);

    // Initialize state publisher
    rc = rclc_publisher_init_default(
        &state_publisher,
        &node,
        ROSIDL_GET_MSG_TYPE_SUPPORT(sensor_msgs, msg, JointState),
        "/arm/state");

    // Initialize command subscriber
    rc = rclc_subscription_init_default(
        &command_subscriber,
        &node,
        ROSIDL_GET_MSG_TYPE_SUPPORT(std_msgs, msg, Float64MultiArray),
        "/arm/command");

    // Initialize state message
    static char *joint_names[] = {"base_joint", "elbow_joint"};
    state_msg.name.data = (rosidl_runtime_c__String *)malloc(2 * sizeof(rosidl_runtime_c__String));
    state_msg.name.size = 2;
    state_msg.name.capacity = 2;
    for (int i = 0; i < 2; i++) {
        rosidl_runtime_c__String__assign(&state_msg.name.data[i], joint_names[i]);
    }

    state_msg.position.data = (double *)malloc(2 * sizeof(double));
    state_msg.position.size = 2;
    state_msg.position.capacity = 2;

    state_msg.effort.data = (double *)malloc(2 * sizeof(double));
    state_msg.effort.size = 2;
    state_msg.effort.capacity = 2;

    // Initialize command message for subscriber allocation
    command_msg.data.data = (double *)malloc(2 * sizeof(double));
    command_msg.data.size = 0;
    command_msg.data.capacity = 2;

    // Initialize executor
    rclc_executor_t executor;
    rc = rclc_executor_init(&executor, &support.context, 1, &allocator);
    rc = rclc_executor_add_subscription(&executor, &command_subscriber, &command_msg, &command_callback, ON_NEW_DATA);

    while (wb_robot_step(TIME_STEP) != -1) {
        // Read sensors
        state_msg.position.data[0] = wb_position_sensor_get_value(base_sensor);
        state_msg.position.data[1] = wb_position_sensor_get_value(elbow_sensor);
        
        // Torque feedback (Webots returns torque for RotationalMotor)
        state_msg.effort.data[0] = wb_motor_get_torque_feedback(base_motor);
        state_msg.effort.data[1] = wb_motor_get_torque_feedback(elbow_motor);

        // Update timestamp (simulation time)
        double now = wb_robot_get_time();
        state_msg.header.stamp.sec = (int32_t)now;
        state_msg.header.stamp.nanosec = (uint32_t)((now - state_msg.header.stamp.sec) * 1e9);

        // Publish state
        rcl_publish(&state_publisher, &state_msg, NULL);

        // Handle subscriptions
        rclc_executor_spin_some(&executor, RCL_MS_TO_NS(10));

        // Apply commands
        wb_motor_set_position(base_motor, target_positions[0]);
        wb_motor_set_position(elbow_motor, target_positions[1]);
    }

    // Cleanup
    free(state_msg.name.data);
    free(state_msg.position.data);
    free(state_msg.effort.data);
    free(command_msg.data.data);

    rcl_publisher_fini(&state_publisher, &node);
    rcl_subscription_fini(&command_subscriber, &node);
    rcl_node_fini(&node);
    rclc_support_fini(&support);

    wb_robot_cleanup();
    return 0;
}
