import os
from backend.database import init_db

def test_db_init():
    db_path = "./roboarm.db"
    if os.path.exists(db_path):
        os.remove(db_path)
    init_db()
    assert os.path.exists(db_path)
    # Cleanup after test if desired, but here we just check it was created
