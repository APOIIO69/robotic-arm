# Task 4: ROS 2 Control Node (PID) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a ROS 2 node that controls a 2-axis robotic arm using PID controllers, subscribing to joint states and publishing effort commands.

**Architecture:** A Python-based ROS 2 node containing a `PID` class with anti-windup. The node handles synchronization between the current state and target positions.

**Tech Stack:** Python, ROS 2 (rclpy), sensor_msgs, std_msgs.

---

### Task 4.1: PID Class with Anti-Windup

**Files:**
- Modify: `server/arm_control_node.py`
- Test: `tests/test_pid.py`

- [ ] **Step 1: Refine PID class with robust anti-windup**

```python
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
```

- [ ] **Step 2: Run tests to verify PID logic**

Run: `python3 tests/test_pid.py`
Expected: PASS (once the NameError in arm_control_node.py is fixed)

- [ ] **Step 3: Fix NameError for non-ROS environments**

Modify `server/arm_control_node.py` to only define `ArmControlNode` if `HAS_ROS2` is True.

- [ ] **Step 4: Commit**

```bash
git add server/arm_control_node.py
git commit -m "feat: refine PID class and fix import issues"
```

### Task 4.2: ROS 2 Node Implementation

**Files:**
- Modify: `server/arm_control_node.py`

- [ ] **Step 1: Complete ArmControlNode implementation**

Implement the subscription to `/arm/state` and publisher to `/arm/command`.

- [ ] **Step 2: Commit**

```bash
git add server/arm_control_node.py
git commit -m "feat: implement ArmControlNode for ROS 2"
```
