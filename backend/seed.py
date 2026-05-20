from backend.database import get_connection, init_db

def seed():
    init_db()
    conn = get_connection()
    cursor = conn.cursor()
    
    # Check if data exists
    cursor.execute("SELECT COUNT(*) FROM lines")
    if cursor.fetchone()[0] > 0:
        conn.close()
        return

    # Seed Line
    cursor.execute(
        "INSERT INTO lines (name, description) VALUES (?, ?)",
        ("Линия A — Сварка", "Штатная работа")
    )
    line_id = cursor.lastrowid

    # Seed Robot
    cursor.execute(
        "INSERT INTO robots (line_id, name, model, type, ros_topic_prefix) VALUES (?, ?, ?, ?, ?)",
        (line_id, "KUKA KR 210 #1", "KUKA KR 210 R2700", "welding_arm", "/arm")
    )
    robot_id = cursor.lastrowid

    # Seed Sensors
    sensors = [
        (robot_id, "Угол основания J1", "position", "рад", -3.14, 3.14),
        (robot_id, "Угол локтя J2",     "position", "рад", -3.14, 3.14),
    ]
    cursor.executemany(
        "INSERT INTO sensors (robot_id, label, type, unit, normal_min, normal_max) VALUES (?, ?, ?, ?, ?, ?)",
        sensors
    )
    
    conn.commit()
    conn.close()
    print("Seed complete.")

if __name__ == "__main__":
    seed()
