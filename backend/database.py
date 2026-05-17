import sqlite3
import os

DB_PATH = "roboarm.db"

def get_connection():
    return sqlite3.connect(DB_PATH)

def init_db():
    conn = get_connection()
    cursor = conn.cursor()
    
    # Lines table
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS lines (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        description TEXT
    )
    ''')
    
    # Robots table
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS robots (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        line_id INTEGER,
        name TEXT NOT NULL,
        model TEXT,
        type TEXT,
        ros_topic_prefix TEXT,
        has_gripper BOOLEAN DEFAULT 0,
        has_torch BOOLEAN DEFAULT 0,
        FOREIGN KEY (line_id) REFERENCES lines (id)
    )
    ''')
    
    # Sensors table
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS sensors (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        robot_id INTEGER,
        label TEXT NOT NULL,
        type TEXT,
        unit TEXT,
        normal_min REAL,
        normal_max REAL,
        FOREIGN KEY (robot_id) REFERENCES robots (id)
    )
    ''')
    
    # Notifications table
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS notifications (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        type TEXT NOT NULL,
        robot_name TEXT,
        line_name TEXT,
        message TEXT,
        timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
    )
    ''')
    
    conn.commit()
    conn.close()

if __name__ == "__main__":
    init_db()
