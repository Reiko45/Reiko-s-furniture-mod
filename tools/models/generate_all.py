"""Rewrites every model template in src/main/resources/.../models/block/template/ from the room scripts.

Usage: python3 tools/models/generate_all.py
Then run ./gradlew runData so the per-wood and per-color models are regenerated too.
"""
import os
import runpy

HERE = os.path.dirname(os.path.abspath(__file__))
ROOMS = ["bedroom", "kitchen", "dining", "livingroom", "bathroom", "office", "outdoor", "kids", "workbench"]

if __name__ == "__main__":
    os.chdir(HERE)
    import sys
    sys.path.insert(0, HERE)
    for room in ROOMS:
        runpy.run_path(os.path.join(HERE, room + ".py"), run_name="__main__")
        print("wrote", room)
