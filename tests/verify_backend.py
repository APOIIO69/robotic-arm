import unittest
import sqlite3
import os
import sys
from unittest.mock import MagicMock

# Add project root to sys.path
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from backend.database import init_db, get_connection
from backend.seed import seed

class TestBackend(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        # Use a separate test database
        cls.db_path = "test_roboarm.db"
        import backend.database
        backend.database.DB_PATH = cls.db_path
        import backend.seed
        backend.seed.DB_PATH = cls.db_path

    def setUp(self):
        if os.path.exists(self.db_path):
            os.remove(self.db_path)
        init_db()

    def tearDown(self):
        if os.path.exists(self.db_path):
            os.remove(self.db_path)

    def test_database_init(self):
        """Verify that all required tables are created."""
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        cursor.execute("SELECT name FROM sqlite_master WHERE type='table'")
        tables = [t[0] for t in cursor.fetchall()]
        self.assertIn('lines', tables)
        self.assertIn('robots', tables)
        self.assertIn('sensors', tables)
        self.assertIn('notifications', tables)
        conn.close()

    def test_seeding(self):
        """Verify that seeding populates the database correctly."""
        seed()
        conn = sqlite3.connect(self.db_path)
        cursor = conn.cursor()
        
        cursor.execute("SELECT COUNT(*) FROM lines")
        self.assertEqual(cursor.fetchone()[0], 1)
        
        cursor.execute("SELECT name FROM robots")
        self.assertEqual(cursor.fetchone()[0], "KUKA KR 210 #1")
        
        cursor.execute("SELECT COUNT(*) FROM sensors")
        self.assertEqual(cursor.fetchone()[0], 2)
        
        conn.close()

    def test_api_logic_lines(self):
        """Verify the logic of /api/lines endpoint."""
        from backend.web_bridge import get_lines
        seed()
        response = get_lines()
        self.assertIn("lines", response)
        self.assertEqual(len(response["lines"]), 1)
        self.assertEqual(response["lines"][0]["name"], "Линия A — Сварка")
        self.assertEqual(response["lines"][0]["robots_count"], 1)

    def test_telemetry_merging(self):
        """Verify that live telemetry is correctly merged with database metadata."""
        from backend.web_bridge import get_robot_telemetry
        import backend.web_bridge
        
        seed()
        
        # Mock the ROS node
        mock_node = MagicMock()
        mock_node.robots_state = {
            "/arm": {
                "position": [45.0, 1.2],
                "effort": [10.0, 5.0]
            }
        }
        backend.web_bridge.ros_node = mock_node
        
        response = get_robot_telemetry(1)
        self.assertNotIn("error", response)
        self.assertEqual(len(response["sensors"]), 2)
        
        # Check first sensor (Temperature J1, threshold 80.0)
        s1 = response["sensors"][0]
        self.assertEqual(s1["label"], "Температура J1")
        self.assertEqual(s1["value"], 45.0)
        self.assertEqual(s1["status"], "ok")
        
        # Check second sensor (Current J2, threshold 15.0)
        # We'll set a value above threshold to test status
        mock_node.robots_state["/arm"]["position"] = [45.0, 20.0]
        response = get_robot_telemetry(1)
        s2 = response["sensors"][1]
        self.assertEqual(s2["value"], 20.0)
        self.assertEqual(s2["status"], "critical")
        
        # Verify robot overall status is critical
        self.assertEqual(response["robot"]["status"], "critical")

if __name__ == "__main__":
    unittest.main()
