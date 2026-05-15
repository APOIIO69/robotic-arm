#!/usr/bin/python3
import subprocess
import time
import signal
import sys
import os

# Configuration paths
PROJECT_ROOT = os.getcwd()
WEBOTS_WORLD = os.path.join(PROJECT_ROOT, "simulation/worlds/robotic_arm.wbt")

processes = []

def signal_handler(sig, frame):
    print("\n[Orchestrator] Shutting down all components...")
    for p in reversed(processes):
        try:
            # Send SIGINT to the process group to ensure children die too
            os.killpg(os.getpgid(p.pid), signal.SIGINT)
            p.wait(timeout=5)
        except Exception:
            p.kill()
    print("[Orchestrator] Done.")
    sys.exit(0)

signal.signal(signal.SIGINT, signal_handler)

def start_process(name, command, cwd=PROJECT_ROOT, shell=True, env=None):
    print(f"[Orchestrator] Starting {name}...")
    # Use start_new_session to create a process group
    p = subprocess.Popen(
        command, 
        cwd=cwd, 
        shell=shell, 
        preexec_fn=os.setsid, 
        env={**os.environ, **(env or {})}
    )
    processes.append(p)
    return p

def main():
    print("=== Robotic Arm Unified Launcher ===")

    # 1. Start micro-ROS Agent
    # We use source in bash because we need the workspace environment
    agent_cmd = f"bash -c 'source {PROJECT_ROOT}/microros_agent_ws/install/local_setup.bash && ros2 run micro_ros_agent micro_ros_agent udp4 --port 8888'"
    start_process("Micro-ROS Agent", agent_cmd)
    time.sleep(2)

    # 2. Start Webots
    # Note: Webots is a GUI app, it will stay open.
    webots_cmd = f"webots {WEBOTS_WORLD}"
    start_process("Webots", webots_cmd)
    time.sleep(5) # Give Webots time to load the world

    # 3. Start External Controller
    # Use the script we created earlier
    controller_cmd = "./start_controller.sh"
    start_process("Arm Controller (C)", controller_cmd, cwd=os.path.join(PROJECT_ROOT, "simulation/controllers/arm_controller"))
    time.sleep(2)

    # 4. Start PID Control Node
    pid_cmd = "bash -c 'source /opt/ros/humble/setup.bash && python3 server/arm_control_node.py'"
    start_process("PID Control Node", pid_cmd)
    time.sleep(1)

    # 5. Start Web Backend
    backend_cmd = "bash -c 'source /opt/ros/humble/setup.bash && python3 backend/web_bridge.py'"
    start_process("Web Backend", backend_cmd)

    print("\n[Orchestrator] SYSTEM READY.")
    print("[Orchestrator] Open http://localhost:8000 in your browser.")
    print("[Orchestrator] Press Ctrl+C to stop everything.")

    # Keep the main script alive
    while True:
        time.sleep(1)

if __name__ == "__main__":
    main()
