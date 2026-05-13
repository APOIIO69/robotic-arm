try:
    import rclpy
    from rclpy.node import Node
    from sensor_msgs.msg import JointState
    from std_msgs.msg import Float64MultiArray
    HAS_ROS2 = True
except ImportError:
    HAS_ROS2 = False

class PID:
    def __init__(self, kp, ki, kd, min_output=-10.0, max_output=10.0):
        self.kp = kp
        self.ki = ki
        self.kd = kd
        self.min_output = min_output
        self.max_output = max_output
        
        self.integral = 0.0
        self.prev_error = 0.0

    def compute(self, setpoint, measurement, dt):
        if dt <= 0:
            return 0.0
            
        error = setpoint - measurement
        
        # Proportional term
        p_term = self.kp * error
        
        # Derivative term
        derivative = (error - self.prev_error) / dt
        d_term = self.kd * derivative
        
        # Integral term
        self.integral += error * dt
        i_term = self.ki * self.integral
        
        output = p_term + i_term + d_term
        
        # Clamping and simple anti-windup: 
        # If output is saturated, don't accumulate integral if error has same sign
        if output > self.max_output:
            if error > 0:
                self.integral -= error * dt
            output = self.max_output
        elif output < self.min_output:
            if error < 0:
                self.integral -= error * dt
            output = self.min_output
            
        self.prev_error = error
        return output

if HAS_ROS2:
    class ArmControlNode(Node):
        def __init__(self):
            super().__init__('arm_control_node')
            
            # Parameters (default targets)
            self.declare_parameter('targets', [0.0, 0.0])
            self.targets = self.get_parameter('targets').value
            
            # PID Controllers for base and elbow
            self.pids = [
                PID(kp=5.0, ki=0.1, kd=0.1), # Base
                PID(kp=5.0, ki=0.1, kd=0.1)  # Elbow
            ]
            
            # Subscriptions
            self.subscription = self.create_subscription(
                JointState,
                '/arm/state',
                self.state_callback,
                10
            )
            
            # Publishers
            self.publisher = self.create_publisher(
                Float64MultiArray,
                '/arm/command',
                10
            )
            
            self.last_time = self.get_clock().now()
            self.get_logger().info('Arm Control Node started')

        def state_callback(self, msg):
            current_time = self.get_clock().now()
            dt = (current_time - self.last_time).nanoseconds / 1e9
            self.last_time = current_time
            
            if dt <= 0:
                return

            # Assuming msg.position contains [base_pos, elbow_pos]
            if len(msg.position) < 2:
                return

            efforts = []
            for i in range(2):
                effort = self.pids[i].compute(self.targets[i], msg.position[i], dt)
                efforts.append(effort)
                
            command_msg = Float64MultiArray()
            command_msg.data = efforts
            self.publisher.publish(command_msg)

    def main(args=None):
        rclpy.init(args=args)
        node = ArmControlNode()
        rclpy.spin(node)
        node.destroy_node()
        rclpy.shutdown()

if __name__ == '__main__':
    if HAS_ROS2:
        main()
    else:
        print("ROS 2 not found. PID class is available for import.")
