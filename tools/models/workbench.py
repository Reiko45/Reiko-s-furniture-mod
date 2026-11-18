"""Model templates for the workbench section. Run tools/models/generate_all.py to rewrite every template."""
from modelkit import *  # noqa: F401,F403

# ---- from workbench.py ----
# Carpenter's bench: oak table with a lower shelf and a saw blade sticking out of the top.
P, L = "#planks", "#log"
boxes = [Box((x, 0, z), (x + 2, 12, z + 2), L) for x in (1, 13) for z in (1, 13)] + [
    Box((0, 12, 0), (16, 15, 16), P, faces={"up": "#top"}),
    Box((3, 4, 3), (13, 5, 13), P),
    # Saw blade: a flat plane like the stonecutter's, only front and back faces.
    Box((3, 15, 8), (13, 20, 8), "#saw", skip=("up", "down", "east", "west"),
        uvs={"north": [3, 9, 13, 14], "south": [3, 9, 13, 14]}),
]
write("", "workbench", boxes, {
    "planks": "minecraft:block/oak_planks",
    "log": "minecraft:block/stripped_oak_log",
    "top": "minecraft:block/oak_planks",
    "saw": "minecraft:block/stonecutter_saw",
}, render_type="minecraft:cutout")

