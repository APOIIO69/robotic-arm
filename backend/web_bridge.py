import asyncio
import threading
import math
from fastapi import FastAPI, WebSocket, WebSocketDisconnect
from fastapi.staticfiles import StaticFiles
from pydantic import BaseModel
import rclpy
from rclpy.node import Node
from sensor_msgs.msg import JointState
from std_msgs.msg import Float64MultiArray
import json

app = FastAPI()

# Global state for telemetry
arm_state = {
    "position": [0.0, 0.0],
    "effort": [0.0, 0.0],
    "names": ["base_joint", "elbow_joint"]
}

class ROS2Bridge(Node):
    def __init__(self):
        super().__init__('web_bridge_node')
        self.publisher = self.create_publisher(
            Float64MultiArray, 
            '/arm/setpoint', 
            10
        )
        self.subscription = self.create_subscription(
            JointState,
            '/arm/state',
            self.state_callback,
            10
        )
        self.get_logger().info("ROS 2 Bridge Node started")

    def state_callback(self, msg):
        global arm_state
        # Replace NaN with 0.0 for safety
        arm_state["position"] = [x if not math.isnan(x) else 0.0 for x in msg.position]
        arm_state["effort"] = [x if not math.isnan(x) else 0.0 for x in msg.effort]
        
        if not hasattr(self, 'count'): self.count = 0
        if self.count % 50 == 0:
            print(f"[Bridge] Telemetry: Pos={arm_state['position']}, Eff={arm_state['effort']}")
        self.count += 1

    def send_command(self, base_angle, elbow_angle):
        msg = Float64MultiArray()
        msg.data = [float(base_angle), float(elbow_angle)]
        self.publisher.publish(msg)

# ROS 2 Threading setup
ros_node = None

def run_ros2():
    global ros_node
    rclpy.init()
    ros_node = ROS2Bridge()
    rclpy.spin(ros_node)
    ros_node.destroy_node()
    rclpy.shutdown()

# Start ROS 2 in a background thread
threading.Thread(target=run_ros2, daemon=True).start()

# API Models
class JointCommand(BaseModel):
    base: float
    elbow: float

# Routes
@app.post("/api/command")
async def set_angles(cmd: JointCommand):
    if ros_node:
        ros_node.send_command(cmd.base, cmd.elbow)
        return {"status": "success", "command": cmd}
    return {"status": "error", "message": "ROS 2 node not ready"}

@app.get("/api/state")
async def get_state():
    return arm_state

# WebSocket for real-time telemetry
connected_clients = set()

@app.websocket("/ws/telemetry")
async def telemetry_websocket(websocket: WebSocket):
    await websocket.accept()
    connected_clients.add(websocket)
    try:
        while True:
            # Send current state every 100ms
            await websocket.send_json(arm_state)
            await asyncio.sleep(0.1)
    except WebSocketDisconnect:
        connected_clients.remove(websocket)

# Serve static frontend
app.mount("/", StaticFiles(directory="backend/static", html=True), name="static")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
