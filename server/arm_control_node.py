import math
try:
    import rclpy
    from rclpy.node import Node
    from sensor_msgs.msg import JointState
    from std_msgs.msg import Float64MultiArray
    HAS_ROS2 = True
except ImportError:
    HAS_ROS2 = False

class PID:
    def __init__(self, kp, ki, kd, min_output=-5.0, max_output=5.0):
        self.kp = kp
        self.ki = ki
        self.kd = kd
        self.min_output = min_output
        self.max_output = max_output
        
        self.integral = 0.0
        self.prev_error = 0.0

    def compute(self, setpoint, measurement, dt):
        # Защита от некорректных данных
        if dt <= 0 or math.isnan(measurement):
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
        
        # Clamping and simple anti-windup
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
            
            self.declare_parameter('targets', [0.0, 0.0])
            self.targets = self.get_parameter('targets').value
            
            # Стабильные коэффициенты (высокий KD для гашения колебаний)
            # Внимание: знаки инвертированы (отрицательные), так как моторы в Webots смотрят в другую сторону
            self.pids = [
                PID(kp=-0.3, ki=-0.0, kd=-0.1, min_output=-1.5, max_output=1.5), # Base
                PID(kp=-0.5, ki=-0.0, kd=-0.1, min_output=-1.5, max_output=1.5)  # Elbow
            ]
            
            self.subscription = self.create_subscription(JointState, '/arm/state', self.state_callback, 10)
            self.publisher = self.create_publisher(Float64MultiArray, '/arm/command', 10)
            self.setpoint_sub = self.create_subscription(Float64MultiArray, '/arm/setpoint', self.setpoint_callback, 10)
            
            self.last_time = self.get_clock().now()
            self.get_logger().info('Arm Control Node started')

        def setpoint_callback(self, msg):
            if len(msg.data) >= 2:
                self.targets = list(msg.data)
                self.get_logger().info(f'New targets: {self.targets}')

        def state_callback(self, msg):
            current_time = self.get_clock().now()
            dt = (current_time - self.last_time).nanoseconds / 1e9
            self.last_time = current_time
            
            if dt <= 0 or len(msg.position) < 2:
                return

            efforts = []
            log_str = "Control: "
            for i in range(2):
                pos = msg.position[i]
                if math.isnan(pos): pos = 0.0
                
                effort = self.pids[i].compute(self.targets[i], pos, dt)
                efforts.append(effort)
                log_str += f"J{i}: T={self.targets[i]:.1f} P={pos:.2f} E={effort:.2f} | "
            
            if not hasattr(self, 'count'): self.count = 0
            if self.count % 20 == 0:
                self.get_logger().info(log_str)
            self.count += 1
                
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
