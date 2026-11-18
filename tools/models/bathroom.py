"""Model templates for the bathroom section. Run tools/models/generate_all.py to rewrite every template."""
from modelkit import *  # noqa: F401,F403

# ---- from bathroom.py ----
ROOM = "bathroom"
C, M = "#ceramic", "#metal"
CERAMIC = {"ceramic": "minecraft:block/white_concrete", "seat": "minecraft:block/quartz_block_bottom",
           "metal": "minecraft:block/iron_block"}

# Toilet: bowl at the front, tank at the back.
write(ROOM, "toilet", [
    Box((5, 0, 4), (11, 5, 11), C),
    Box((3, 5, 2), (13, 8, 12), C),
    Box((3, 8, 2), (13, 9, 12), "#seat"),
    Box((2, 5, 12), (14, 15, 16), C),
    Box((1.5, 15, 11.5), (14.5, 16, 16), "#seat"),
    Box((11, 13, 11), (13, 14, 11.5), M),
], CERAMIC, particle="minecraft:block/white_concrete")

# Bathtub: pieces join side by side; each open end gets an end wall. Water shows when filled.
write(ROOM, "bathtub_base", [
    Box((0, 0, 1), (16, 2, 15), C),
    Box((0, 0, 0), (16, 10, 2), C),
    Box((0, 0, 14), (16, 10, 16), C),
    Box((0, 9.5, -0.5), (16, 10.5, 2), "#seat"),
    Box((0, 9.5, 14), (16, 10.5, 16.5), "#seat"),
], CERAMIC, particle="minecraft:block/white_concrete")
write(ROOM, "bathtub_end_left", [Box((0, 0, 2), (2, 10, 14), C), Box((-0.5, 9.5, 2), (2, 10.5, 14), "#seat")],
      CERAMIC, particle="minecraft:block/white_concrete")
write(ROOM, "bathtub_end_right", [Box((14, 0, 2), (16, 10, 14), C), Box((14, 9.5, 2), (16.5, 10.5, 14), "#seat")],
      CERAMIC, particle="minecraft:block/white_concrete")
write(ROOM, "bathtub_water", [Box((0, 8, 2), (16, 8, 14), "#water", skip=("down", "north", "south", "east", "west"), tint=True)],
      {"water": "minecraft:block/water_still"}, particle="minecraft:block/water_still")
write(ROOM, "bathtub_item", [
    Box((0, 0, 1), (16, 2, 15), C), Box((0, 0, 0), (16, 10, 2), C), Box((0, 0, 14), (16, 10, 16), C),
    Box((0, 0, 2), (2, 10, 14), C), Box((14, 0, 2), (16, 10, 14), C),
], CERAMIC, particle="minecraft:block/white_concrete")

# Shower: two blocks tall, tiled back wall, glass side panels, shower head at the top.
SHOWER = {**CERAMIC, "tile": "minecraft:block/quartz_bricks", "glass": "minecraft:block/glass"}
write_tall(ROOM, "shower",
    lower=[
        Box((0, 0, 0), (16, 1, 16), C),
        Box((0, 1, 15), (16, 16, 16), "#tile", skip=("up",)),
        Box((0, 1, 0), (0.5, 16, 15), "#glass", skip=("up",)),
        Box((15.5, 1, 0), (16, 16, 15), "#glass", skip=("up",)),
        Box((7.5, 1, 14.5), (8.5, 16, 15), M, skip=("up",)),
        Box((7, 1, 7), (9, 1.1, 9), M),
    ],
    upper=[
        Box((0, 0, 15), (16, 14, 16), "#tile", skip=("down",)),
        Box((0, 0, 0), (0.5, 14, 15), "#glass", skip=("down",)),
        Box((15.5, 0, 0), (16, 14, 15), "#glass", skip=("down",)),
        Box((0, 14, 0), (16, 15, 16), C),
        Box((7.5, 0, 14.5), (8.5, 12, 15), M, skip=("down",)),
        Box((7.5, 11, 9), (8.5, 12, 14.5), M),
        Box((6, 10, 6), (10, 11, 10), M),
    ], textures=SHOWER, particle="minecraft:block/white_concrete", render_type="minecraft:cutout")

# Bathroom sink: basin on a pedestal.
write(ROOM, "bathroom_sink", [
    Box((6, 0, 7), (10, 10, 12), C),
    Box((2, 10, 4), (14, 13, 15), C, faces={"up": "#basin"}),
    Box((2, 13, 4), (14, 14, 5.5), C),
    Box((2, 13, 13), (14, 14, 15), C),
    Box((2, 13, 5.5), (3.5, 14, 13), C),
    Box((12.5, 13, 5.5), (14, 14, 13), C),
    Box((7.5, 14, 13), (8.5, 16, 14), M),
    Box((7.5, 15, 10.5), (8.5, 16, 13), M),
], {**CERAMIC, "basin": "minecraft:block/iron_block"}, particle="minecraft:block/white_concrete")


# Bathroom cabinet: hangs on the wall; the door is a mirror.
def bathroom_cabinet(opened):
    parts = [Box((2, 2, 11), (14, 14, 16), C)]
    parts += door(2.5, 13.5, 2.5, 13.5, 10.5, 11, "#mirror", opened, "left")
    if opened:
        parts.append(Box((2.5, 2.5, 10.9), (13.5, 13.5, 11), "#seat"))
        parts.append(Box((2.5, 7.75, 10.5), (13.5, 8.25, 11), C))
    else:
        parts.append(Box((12, 7, 10), (12.5, 9, 10.5), M))
    return parts

CABINET = {**CERAMIC, "mirror": "minecraft:block/light_blue_stained_glass"}
write(ROOM, "bathroom_cabinet", bathroom_cabinet(False), CABINET, particle="minecraft:block/white_concrete")
write(ROOM, "bathroom_cabinet_open", bathroom_cabinet(True), CABINET, particle="minecraft:block/white_concrete")

# Towel rack: a metal bar on the wall with a towel hanging over it.
write(ROOM, "towel_rack", [
    Box((1, 11, 14), (15, 12, 15), M),
    Box((1, 10.5, 15), (2, 12.5, 16), M),
    Box((14, 10.5, 15), (15, 12.5, 16), M),
    Box((2.5, 3, 13), (13.5, 12, 13.6), "#wool"),
    Box((2.5, 6, 15), (13.5, 12, 15.5), "#wool"),
    Box((2.5, 12, 13), (13.5, 12.6, 15.5), "#wool"),
], {"metal": "minecraft:block/iron_block"}, particle="#wool")

