import asyncio
import threading
import math
import sqlite3
import json
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
import rclpy
from rclpy.node import Node
from sensor_msgs.msg import JointState
from std_msgs.msg import Float64MultiArray

DB_PATH = "roboarm.db"

def get_db_connection():
    return sqlite3.connect(DB_PATH)

app = FastAPI()

class ROS2Bridge(Node):
    def __init__(self, robot_configs):
        super().__init__('web_bridge_node')
        self.robots_state = {}
        self.publishers_map = {}

        for robot in robot_configs:
            # robot: (id, line_id, name, model, type, ros_topic_prefix, ...)
            prefix = robot[5] 
            if not prefix: continue
            
            self.robots_state[prefix] = {"position": [], "effort": [], "timestamp": 0}
            
            # Subscribe to telemetry
            self.create_subscription(
                JointState,
                f"{prefix}/state",
                lambda msg, p=prefix: self.state_callback(msg, p),
                10
            )
            # Create publisher for commands
            self.publishers_map[prefix] = self.create_publisher(
                Float64MultiArray,
                f"{prefix}/setpoint",
                10
            )
        self.get_logger().info(f"ROS 2 Bridge started with {len(self.publishers_map)} robots")

    def state_callback(self, msg, prefix):
        self.robots_state[prefix]["position"] = [x if not math.isnan(x) else 0.0 for x in msg.position]
        self.robots_state[prefix]["effort"] = [x if not math.isnan(x) else 0.0 for x in msg.effort]
        self.robots_state[prefix]["timestamp"] = self.get_clock().now().to_msg().sec

    def send_command(self, prefix, angles):
        if prefix in self.publishers_map:
            msg = Float64MultiArray()
            msg.data = [float(x) for x in angles]
            self.publishers_map[prefix].publish(msg)
            return True
        return False

# Global bridge instance
ros_node = None

def run_ros2():
    global ros_node
    rclpy.init()
    
    # Load robots from DB
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM robots")
    robots = cursor.fetchall()
    conn.close()
    
    ros_node = ROS2Bridge(robots)
    rclpy.spin(ros_node)
    ros_node.destroy_node()
    rclpy.shutdown()

threading.Thread(target=run_ros2, daemon=True).start()

# API Endpoints (Task 4)
@app.get("/api/lines")
def get_lines():
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM lines")
    lines = cursor.fetchall()
    
    result = []
    for line in lines:
        cursor.execute("SELECT COUNT(*) FROM robots WHERE line_id = ?", (line[0],))
        count = cursor.fetchone()[0]
        result.append({
            "id": line[0],
            "name": line[1],
            "description": line[2],
            "status": "ok",
            "robots_count": count
        })
    conn.close()
    return {"lines": result}

@app.get("/api/lines/{line_id}/robots")
def get_line_robots(line_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM robots WHERE line_id = ?", (line_id,))
    robots = cursor.fetchall()
    
    result = []
    for r in robots:
        result.append({
            "id": r[0],
            "name": r[2],
            "model": r[3],
            "type": r[4],
            "status": "ok"
        })
    conn.close()
    return {"robots": result}

@app.get("/api/robots/{robot_id}/telemetry")
def get_robot_telemetry(robot_id: int):
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT * FROM robots WHERE id = ?", (robot_id,))
    robot = cursor.fetchone()
    if not robot:
        conn.close()
        return {"error": "Robot not found"}
    
    cursor.execute("SELECT * FROM sensors WHERE robot_id = ?", (robot_id,))
    sensors = cursor.fetchall()
    conn.close()

    prefix = robot[5]
    live_data = ros_node.robots_state.get(prefix, {}) if ros_node else {}
    positions = live_data.get("position", [])
    efforts   = live_data.get("effort",   [])

    mapped_sensors = []
    overall_status = "ok"
    pos_idx = 0
    eff_idx = 0
    for s in sensors:
        sensor_type = s[3]
        if sensor_type == "position":
            val = positions[pos_idx] if pos_idx < len(positions) else 0.0
            pos_idx += 1
        elif sensor_type == "effort":
            val = efforts[eff_idx] if eff_idx < len(efforts) else 0.0
            eff_idx += 1
        else:
            val = 0.0

        status = "ok"
        if s[5] is not None and val < s[5]: status = "warning"
        if s[6] is not None and val > s[6]: status = "critical"

        if status == "critical": overall_status = "critical"
        elif status == "warning" and overall_status == "ok": overall_status = "warning"

        mapped_sensors.append({
            "id": s[0],
            "label": s[2],
            "type": s[3],
            "unit": s[4],
            "value": val,
            "status": status
        })

    return {
        "robot": {
            "id": robot[0],
            "name": robot[2],
            "model": robot[3],
            "status": overall_status
        },
        "sensors": mapped_sensors
    }

# Original command route (updated)
class JointCommand(BaseModel):
    robot_id: int
    angles: list[float]

@app.post("/api/command")
async def set_angles(cmd: JointCommand):
    if not ros_node:
        return {"status": "error", "message": "ROS 2 node not ready"}
    
    conn = get_db_connection()
    cursor = conn.cursor()
    cursor.execute("SELECT ros_topic_prefix FROM robots WHERE id = ?", (cmd.robot_id,))
    res = cursor.fetchone()
    conn.close()
    
    if res and ros_node.send_command(res[0], cmd.angles):
        return {"status": "success"}
    return {"status": "error", "message": "Robot not found or failed to send"}

app.mount("/", StaticFiles(directory="backend/static", html=True), name="static")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
