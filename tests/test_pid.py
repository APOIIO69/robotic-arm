import unittest
import sys
import os

# Add server directory to path to import PID
sys.path.append(os.path.join(os.path.dirname(__file__), '../server'))

from arm_control_node import PID

class TestPID(unittest.TestCase):
    def test_pid_output(self):
        """Test that PID produces an output when there is an error."""
        pid = PID(kp=1.0, ki=0.1, kd=0.01)
        output = pid.compute(setpoint=1.0, measurement=0.0, dt=0.1)
        self.assertNotEqual(output, 0.0)

    def test_pid_setpoint_reached(self):
        """Test that PID moves toward the setpoint."""
        pid = PID(kp=1.0, ki=0.0, kd=0.0)
        measurement = 0.0
        setpoint = 1.0
        
        # Run a few iterations
        for _ in range(10):
            output = pid.compute(setpoint=setpoint, measurement=measurement, dt=0.1)
            # Simple plant: change in position = output * dt
            measurement += output * 0.1
            
        self.assertAlmostEqual(measurement, setpoint, delta=0.5)

    def test_pid_anti_windup(self):
        """Test that anti-windup prevents integral from growing indefinitely when saturated."""
        # max_output is 10.0 by default. Set kp=0, ki=1.0 to only use integral.
        pid = PID(kp=0.0, ki=1.0, kd=0.0, max_output=10.0)
        
        # Large error for a long time
        for _ in range(100):
            pid.compute(setpoint=100.0, measurement=0.0, dt=1.0)
            
        # If anti-windup works, integral should be capped such that output is just max_output.
        # Without anti-windup, integral would be 100 * 100 = 10000.
        # With anti-windup, integral should be around max_output / ki = 10.0.
        self.assertLessEqual(pid.integral, 11.0) # Allow some small margin

if __name__ == '__main__':
    unittest.main()
