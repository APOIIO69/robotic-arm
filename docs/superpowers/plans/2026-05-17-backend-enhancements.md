# Backend Enhancements & SQLite Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Enhance the backend to support persistent storage, a hierarchical data model, and extended API for the mobile app.

**Architecture:** Hybrid storage using SQLite (SQLAlchemy) for persistence and FastAPI/ROS 2 for real-time telemetry. A Data Merger layer will combine metadata and live values.

**Tech Stack:** FastAPI, SQLAlchemy, SQLite, ROS 2 (rclpy).

---

### Task 1: Database Setup and Models

**Files:**
- Create: `backend/database.py`
- Create: `backend/models.py`
- Test: `tests/test_database.py`

- [ ] **Step 1: Write database models and initialization**

```python
from sqlalchemy import create_engine, Column, Integer, String, Boolean, Float, ForeignKey, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
import datetime

Base = declarative_base()

class Line(Base):
    __tablename__ = "lines"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String)
    description = Column(String)

class Robot(Base):
    __tablename__ = "robots"
    id = Column(Integer, primary_key=True, index=True)
    line_id = Column(Integer, ForeignKey("lines.id"))
    name = Column(String)
    model = Column(String)
    type = Column(String)
    ros_topic_prefix = Column(String)
    has_gripper = Column(Boolean, default=False)
    has_torch = Column(Boolean, default=False)

class Sensor(Base):
    __tablename__ = "sensors"
    id = Column(Integer, primary_key=True, index=True)
    robot_id = Column(Integer, ForeignKey("robots.id"))
    label = Column(String)
    type = Column(String)
    unit = Column(String)
    normal_min = Column(Float, nullable=True)
    normal_max = Column(Float, nullable=True)

class Notification(Base):
    __tablename__ = "notifications"
    id = Column(Integer, primary_key=True, index=True)
    type = Column(String) # critical, warning
    robot_name = Column(String)
    line_name = Column(String)
    message = Column(String)
    timestamp = Column(DateTime, default=datetime.datetime.utcnow)

SQLALCHEMY_DATABASE_URL = "sqlite:///./roboarm.db"
engine = create_engine(SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def init_db():
    Base.metadata.create_all(bind=engine)
```

- [ ] **Step 2: Create a test for database initialization**

```python
import os
from backend.database import init_db, SQLALCHEMY_DATABASE_URL

def test_db_init():
    if os.path.exists("./roboarm.db"): os.remove("./roboarm.db")
    init_db()
    assert os.path.exists("./roboarm.db")
```

- [ ] **Step 3: Run test**
Run: `pytest tests/test_database.py`
Expected: PASS

- [ ] **Step 4: Commit**
```bash
git add backend/database.py backend/models.py tests/test_database.py
git commit -m "feat: setup sqlite database and sqlalchemy models"
```

---

### Task 2: Seeding Initial Data

**Files:**
- Create: `backend/seed.py`

- [ ] **Step 1: Write seed script**

```python
from backend.database import SessionLocal, init_db
from backend.models import Line, Robot, Sensor

def seed():
    init_db()
    db = SessionLocal()
    
    # Check if data exists
    if db.query(Line).first(): return

    line_a = Line(name="Линия A — Сварка", description="Штатная работа")
    db.add(line_a)
    db.commit()
    db.refresh(line_a)

    kuka = Robot(
        line_id=line_a.id, 
        name="KUKA KR 210 #1", 
        model="KUKA KR 210 R2700", 
        type="welding_arm",
        ros_topic_prefix="/arm"
    )
    db.add(kuka)
    db.commit()
    db.refresh(kuka)

    sensors = [
        Sensor(robot_id=kuka.id, label="Температура J1", type="temperature", unit="°C", normal_max=80.0),
        Sensor(robot_id=kuka.id, label="Ток двигателя J2", type="current", unit="А", normal_max=15.0)
    ]
    for s in sensors: db.add(s)
    db.commit()
    db.close()

if __name__ == "__main__":
    seed()
```

- [ ] **Step 2: Run seed script**
Run: `python3 -m backend.seed`
Expected: Database populated with initial data.

- [ ] **Step 3: Commit**
```bash
git add backend/seed.py
git commit -m "feat: add data seeding script"
```

---

### Task 3: Refactoring ROS 2 Bridge for Multi-Robot Support

**Files:**
- Modify: `backend/web_bridge.py`

- [ ] **Step 1: Update ROS2Bridge to use dynamic subscribers**

```python
# In backend/web_bridge.py
class ROS2Bridge(Node):
    def __init__(self, robot_configs):
        super().__init__('web_bridge_node')
        self.robots_state = {} # dict mapping prefix to state
        self.publishers_map = {}

        for robot in robot_configs:
            prefix = robot.ros_topic_prefix
            self.robots_state[prefix] = {"position": [], "effort": []}
            
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

    def state_callback(self, msg, prefix):
        self.robots_state[prefix]["position"] = [x if not math.isnan(x) else 0.0 for x in msg.position]
        self.robots_state[prefix]["effort"] = [x if not math.isnan(x) else 0.0 for x in msg.effort]
```

- [ ] **Step 2: Initialize bridge with DB data**

```python
# In backend/web_bridge.py start logic
def run_ros2():
    rclpy.init()
    db = SessionLocal()
    robots = db.query(Robot).all()
    db.close()
    
    global ros_node
    ros_node = ROS2Bridge(robots)
    rclpy.spin(ros_node)
```

- [ ] **Step 3: Commit**
```bash
git add backend/web_bridge.py
git commit -m "refactor: support dynamic ROS 2 topics based on database"
```

---

### Task 4: Implementing Extended API Endpoints

**Files:**
- Modify: `backend/web_bridge.py`

- [ ] **Step 1: Implement /api/lines and /api/lines/{id}/robots**

```python
@app.get("/api/lines")
def get_lines():
    db = SessionLocal()
    lines = db.query(Line).all()
    result = []
    for line in lines:
        robots = db.query(Robot).filter(Robot.line_id == line.id).all()
        # Simple status calculation for MVP
        result.append({
            "id": line.id,
            "name": line.name,
            "description": line.description,
            "status": "ok", 
            "robots_count": len(robots)
        })
    db.close()
    return {"lines": result}

@app.get("/api/lines/{line_id}/robots")
def get_line_robots(line_id: int):
    db = SessionLocal()
    robots = db.query(Robot).filter(Robot.line_id == line_id).all()
    db.close()
    return {"robots": robots}
```

- [ ] **Step 2: Implement merged /api/robots/{id}/telemetry**

```python
@app.get("/api/robots/{robot_id}/telemetry")
def get_robot_telemetry(robot_id: int):
    db = SessionLocal()
    robot = db.query(Robot).filter(Robot.id == robot_id).first()
    sensors = db.query(Sensor).filter(Sensor.robot_id == robot_id).all()
    db.close()

    if not robot: return {"error": "Robot not found"}
    
    live_data = ros_node.robots_state.get(robot.ros_topic_prefix, {})
    
    # Map ROS values to sensor objects (simplified logic for MVP)
    mapped_sensors = []
    for i, s in enumerate(sensors):
        val = live_data.get("position", [0.0]*len(sensors))[i]
        mapped_sensors.append({
            "id": s.id,
            "label": s.label,
            "value": val,
            "unit": s.unit,
            "status": "ok" if (s.normal_max is None or val <= s.normal_max) else "critical"
        })

    return {
        "robot": robot,
        "sensors": mapped_sensors
    }
```

- [ ] **Step 3: Commit**
```bash
git add backend/web_bridge.py
git commit -m "feat: implement extended API endpoints with DB integration"
```

---

### Task 5: Final Verification

- [ ] **Step 1: Start system**
Run: `python3 -m backend.web_bridge`
Run: `simulation/controllers/arm_controller/start_controller.sh` (if available)

- [ ] **Step 2: Verify via curl**
Run: `curl http://localhost:8000/api/lines`
Expected: JSON with lines data.

- [ ] **Step 3: Commit all changes**
```bash
git commit -am "chore: finalize backend enhancements"
```
