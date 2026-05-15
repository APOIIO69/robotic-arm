# Migration to Host-based ROS 2 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Move the ROS 2 control logic from Docker to the Host OS (Ubuntu 22.04) and simplify the environment.

**Architecture:** ROS 2 Humble and `arm_control_node.py` run natively on the host. micro-ROS Agent remains in Docker for convenience (using host network mode). Webots C-controller connects to the agent on `localhost`.

**Tech Stack:** ROS 2 Humble (Native), Docker (Agent only), Python, C.

---

### Task 1: Environment Setup & Cleanup

**Files:**
- Create: `setup_ros2.sh` (already created)
- Modify: `docker/docker-compose.yml`
- Modify: `run_demo.sh`

- [ ] **Step 1: Update docker-compose.yml to only include the micro-ROS Agent**
Remove the `arm-controller` service.

```yaml
services:
  uros-agent:
    image: docker.m.daocloud.io/microros/micro-ros-agent:humble
    container_name: uros_agent
    network_mode: host
    restart: always
    environment:
      - http_proxy=http://127.0.0.1:8118
      - https_proxy=http://127.0.0.1:8118
    command: udp4 --port 8888
```

- [ ] **Step 2: Update run_demo.sh instructions**
Reflect that the control node runs natively.

```bash
# ... in run_demo.sh ...
echo "Step 3: Run ROS 2 Control Node (in a separate terminal)"
echo "  1. Ensure you have ROS 2 Humble installed natively."
echo "  2. Source your ROS 2 workspace: source /opt/ros/humble/setup.bash"
echo "  3. Run the node: python3 server/arm_control_node.py"
```

- [ ] **Step 3: Commit cleanup**
```bash
git add docker/docker-compose.yml run_demo.sh
git commit -m "chore: migrate ROS 2 control logic to host and cleanup docker"
```

### Task 2: Native ROS 2 Installation

- [ ] **Step 1: Execute setup_ros2.sh**
Run the script to install ROS 2 Humble.

- [ ] **Step 2: Verify installation**
Run: `source /opt/ros/humble/setup.bash && ros2 topic list`
Expected: Command works without errors (agent might need to be running to see topics).

### Task 3: micro-ROS Library Setup for Webots

**Files:**
- Modify: `simulation/controllers/arm_controller/Makefile`

- [ ] **Step 1: Instructions for compiling micro-ROS libraries locally**
Since Webots needs the C headers/libs, they must be built or installed on the host.

- [ ] **Step 2: Update Makefile to point to local installation**
(Already points to `/usr/local`, ensure user knows how to populate it).

### Task 4: Final Verification

- [ ] **Step 1: Start Agent in Docker**
- [ ] **Step 2: Start Webots**
- [ ] **Step 3: Run arm_control_node.py on Host**
- [ ] **Step 4: Verify control loop**
