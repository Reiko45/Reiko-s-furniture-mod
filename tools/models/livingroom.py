"""Model templates for the livingroom section. Run tools/models/generate_all.py to rewrite every template."""
from modelkit import *  # noqa: F401,F403

# ---- from livingroom.py ----
import os, json

P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "livingroom"
W = "#wool"

# Sofa (front faces north, sitter's left is west). The base is always shown; each arm only on the open end.
sofa_base = [Box((x, 0, z), (x + 1.5, 2, z + 1.5), "#frame") for x in (1, 13.5) for z in (1.5, 13)] + [
    Box((0, 2, 1), (16, 5, 16), W),
    Box((0, 5, 1.5), (16, 8, 11.5), W),
    Box((0, 5, 11.5), (16, 15, 16), W),
]
arm_left = [Box((0, 5, 1), (2.5, 11, 11.5), W)]
arm_right = [Box((13.5, 5, 1), (16, 11, 11.5), W)]
FRAME = {"frame": "minecraft:block/stripped_dark_oak_log"}
write(ROOM, "sofa_base", sofa_base, FRAME, particle=W)
write(ROOM, "sofa_arm_left", arm_left, FRAME, particle=W)
write(ROOM, "sofa_arm_right", arm_right, FRAME, particle=W)
write(ROOM, "sofa_item", sofa_base + arm_left + arm_right, FRAME, particle=W)
# Inner corner of an L-shaped sofa: a second backrest along one side.
write(ROOM, "sofa_corner_left", [Box((0, 5, 0), (4.5, 15, 11.5), W)], FRAME, particle=W)
write(ROOM, "sofa_corner_right", [Box((11.5, 5, 0), (16, 15, 11.5), W)], FRAME, particle=W)

# Coffee table: low connecting table.
top = [Box((0, 7, 0), (16, 9, 16), P)]
leg = Box((1, 0, 1), (3, 7, 3), S)
write(ROOM, "coffee_table_top", top)
write(ROOM, "coffee_table_leg", [leg])
write(ROOM, "coffee_table_item", top + [leg.moved(dx=dx, dz=dz) for dx in (0, 12) for dz in (0, 12)])

# Bookshelf: wooden frame around vanilla-style books.
write(ROOM, "bookshelf", [
    Box((0, 0, 0), (1, 16, 16), P),
    Box((15, 0, 0), (16, 16, 16), P),
    Box((1, 15, 0), (15, 16, 16), P),
    Box((1, 0, 0), (15, 1, 16), P),
    Box((1, 1, 1), (15, 15, 16), P, faces={"north": "#books"}),
], {"books": "minecraft:block/bookshelf"})

# TV stand: open shelf on top, two doors below.
def tv_stand(opened):
    parts = [
        Box((0, 14, 0), (16, 16, 16), P),
        Box((0, 0, 0.5), (1, 14, 16), P),
        Box((15, 0, 0.5), (16, 14, 16), P),
        Box((1, 0, 0.5), (15, 2, 16), P),
        Box((1, 2, 15), (15, 14, 16), P),
        Box((1, 7.5, 1), (15, 8.5, 15), P),
    ]
    parts += door(1, 7.9, 2, 7.5, 1, 1.5, S, opened, "left")
    parts += door(8.1, 15, 2, 7.5, 1, 1.5, S, opened, "right")
    parts += door_handle(6.5, 7.25, 4.25, 5.25, 0.5, 1, 1, 7.9, 1, L, opened, "left")
    parts += door_handle(8.75, 9.5, 4.25, 5.25, 0.5, 1, 8.1, 15, 1, L, opened, "right")
    if opened:
        parts.append(Box((1, 2, 1.4), (15, 7.5, 1.5), S))
    return parts


write(ROOM, "tv_stand", tv_stand(False))
write(ROOM, "tv_stand_open", tv_stand(True))

# TV: flat screen on a small stand.
def tv(on):
    return [
        Box((4, 0, 6), (12, 0.75, 10), "#frame"),
        Box((7, 0.75, 7.5), (9, 3, 8.5), "#frame"),
        Box((0, 3, 7), (16, 13, 8.5), "#frame"),
        Box((0.75, 3.75, 6.75), (15.25, 12.25, 7), "#screen", glow=on, uvs={"north": [0, 0, 16, 16]}),
    ]

TV = {"frame": "minecraft:block/black_concrete"}
write(ROOM, "tv", tv(False), {**TV, "screen": "minecraft:block/gray_concrete"}, particle="minecraft:block/black_concrete")
# Four animated channels: nature, news, a cartoon and static (drawn by tools/textures).
for i in range(1, 5):
    write(ROOM, f"tv_on_{i}", tv(True), {**TV, "screen": f"reikos_furniture:block/screen/tv_channel_{i}"},
          particle="minecraft:block/black_concrete")

# Floor lamp: two blocks tall, the shade on the upper half glows when switched on.
lower = [
    Box((5, 0, 5), (11, 1, 11), P),
    Box((7.5, 1, 7.5), (8.5, 16, 8.5), L, skip=("up",)),
]
def upper(on):
    return [
        Box((7.5, 0, 7.5), (8.5, 6, 8.5), L, skip=("down",)),
        Box((3.5, 6, 3.5), (12.5, 14, 12.5), "#shade", glow=on),
    ]
write_tall(ROOM, "floor_lamp", lower, upper(False), {"shade": "minecraft:block/white_wool"})
write(ROOM, "floor_lamp_upper_on", upper(True), {"shade": "minecraft:block/ochre_froglight_side"})

# ---- from livingroom_extra.py ----
P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "livingroom"
W = "#wool"
FRAME = {"frame": "minecraft:block/dark_oak_planks", "leg": "minecraft:block/stripped_dark_oak_log"}

# Armchair: cushioned seat and back with wooden arms.
write(ROOM, "armchair", [Box((x, 0, z), (x + 1.5, 2, z + 1.5), "#leg") for x in (1.5, 13) for z in (1.5, 13)] + [
    Box((1, 2, 1), (15, 4, 15), "#frame"),
    Box((2.5, 4, 1.5), (13.5, 7, 12), W),
    Box((2.5, 4, 12), (13.5, 15, 15), W),
    Box((1, 4, 1), (2.5, 10, 15), "#frame"),
    Box((13.5, 4, 1), (15, 10, 15), "#frame"),
    Box((0.75, 10, 1), (2.75, 11, 15), W),
    Box((13.25, 10, 1), (15.25, 11, 15), W),
], FRAME, particle=W)

# Rug: a flat wool rug; border strips are added on the sides that don't join another rug.
write(ROOM, "rug_center", [Box((0, 0, 0), (16, 1, 16), W)], particle=W)
write(ROOM, "rug_border", [Box((0, 1, 0), (16, 1.5, 1.5), "#border")], {"border": "minecraft:block/white_wool"}, particle=W)
write(ROOM, "rug_item", [Box((0, 0, 0), (16, 1, 16), W)] + [
    Box((0, 1, 0), (16, 1.5, 1.5), "#border"), Box((0, 1, 14.5), (16, 1.5, 16), "#border"),
    Box((0, 1, 1.5), (1.5, 1.5, 14.5), "#border"), Box((14.5, 1, 1.5), (16, 1.5, 14.5), "#border"),
], {"border": "minecraft:block/white_wool"}, particle=W)

# Curtains: hang at the back of the block (in front of the window behind them).
ROD = {"rod": "minecraft:block/stripped_dark_oak_log"}
write(ROOM, "curtains", [
    Box((0, 15, 14.5), (16, 16, 15.5), "#rod"),
    Box((0, 0.5, 13.5), (8, 15, 14.5), W),
    Box((8, 0.5, 13.5), (16, 15, 14.5), W),
], ROD, particle=W)
write(ROOM, "curtains_open", [
    Box((0, 15, 14.5), (16, 16, 15.5), "#rod"),
    Box((0, 0.5, 12.5), (3, 15, 15), W),
    Box((13, 0.5, 12.5), (16, 15, 15), W),
], ROD, particle=W)


# Fireplace: brick surround with logs inside; flames when lit.
def fireplace(lit):
    parts = [
        Box((0, 0, 0), (3, 16, 16), "#brick"),
        Box((13, 0, 0), (16, 16, 16), "#brick"),
        Box((3, 11, 0), (13, 16, 16), "#brick"),
        Box((3, 0, 10), (13, 11, 16), "#brick"),
        Box((3, 0, 0), (13, 1, 10), "#hearth"),
        Box((-0.5, 14, -1), (16.5, 15, 3), "#mantel"),
        Box((4, 1, 4), (12, 3, 6), "#logs"),
        Box((5, 1, 6.5), (11, 3, 8.5), "#logs"),
    ]
    if lit:
        parts += [
            Box((4, 2, 6), (12, 10, 6), "#fire", glow=True, skip=("up", "down", "east", "west"), uvs={"north": [0, 0, 16, 16], "south": [0, 0, 16, 16]}),
            Box((8, 2, 2), (8, 10, 10), "#fire", glow=True, skip=("up", "down", "north", "south"), uvs={"east": [0, 0, 16, 16], "west": [0, 0, 16, 16]}),
        ]
    return parts

FIRE = {"brick": "minecraft:block/bricks", "hearth": "minecraft:block/smooth_stone",
        "mantel": "minecraft:block/dark_oak_planks", "logs": "minecraft:block/oak_log", "fire": "minecraft:block/fire_0"}
write(ROOM, "fireplace", fireplace(False), FIRE, particle="minecraft:block/bricks", render_type="minecraft:cutout")
write(ROOM, "fireplace_on", fireplace(True), FIRE, particle="minecraft:block/bricks", render_type="minecraft:cutout")

