"""Model templates for the kitchen section. Run tools/models/generate_all.py to rewrite every template."""
from modelkit import *  # noqa: F401,F403

# ---- from kitchen.py ----
P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "kitchen"
TOP = {"top": "minecraft:block/smooth_stone"}


def cabinet_doors(y1, y2, zf, hy1, hy2, opened):
    """Two doors (and handles) on the front of a cabinet whose front face is at zf + 0.5."""
    zb = zf + 0.5
    return (door(0.75, 7.85, y1, y2, zf, zb, S, opened, "left")
            + door(8.15, 15.25, y1, y2, zf, zb, S, opened, "right")
            + door_handle(6.5, 7.25, hy1, hy2, zf - 0.5, zf, 0.75, 7.85, zf, L, opened, "left")
            + door_handle(8.75, 9.5, hy1, hy2, zf - 0.5, zf, 8.15, 15.25, zf, L, opened, "right"))


def cabinet_base(opened=False):
    """Lower cabinet: toe kick, body and two doors with handles."""
    parts = [
        Box((0, 0, 2), (16, 1, 16), P),
        Box((0, 1, 1), (16, 14, 16), P),
    ] + cabinet_doors(1.75, 13.25, 0.5, 10, 12.5, opened)
    if opened:
        parts.append(Box((0.75, 1.75, 0.9), (15.25, 13.25, 1), S))
        parts.append(Box((0.75, 7.25, 0.5), (15.25, 7.75, 1), P))
    return parts


COUNTER_TOP = [Box((0, 14, 0), (16, 16, 16), "#top")]

for suffix, opened in (("", False), ("_open", True)):
    write(ROOM, "counter" + suffix, cabinet_base(opened) + COUNTER_TOP, TOP)

# Inner corner: the front is covered by the counter next to it, so it has a plain panel instead of doors.
write(ROOM, "counter_inner", [
    Box((0, 0, 2), (16, 1, 16), P),
    Box((0, 1, 1), (16, 14, 16), P),
    Box((0.75, 1.75, 0.5), (15.25, 13.25, 1), S),
] + COUNTER_TOP, TOP)


def outer_counter(side, opened):
    """Outer corner: doors on the front and on one side (left or right)."""
    front = cabinet_doors(1.75, 13.25, 0.5, 10, 12.5, opened)
    if side == "left":
        side_doors = rotate_ccw(cabinet_doors(1.75, 13.25, 0.5, 10, 12.5, opened))
        body = [Box((2, 0, 2), (16, 1, 16), P), Box((1, 1, 1), (16, 14, 16), P)]
    else:
        side_doors = rotate_cw(cabinet_doors(1.75, 13.25, 0.5, 10, 12.5, opened))
        body = [Box((0, 0, 2), (14, 1, 16), P), Box((0, 1, 1), (15, 14, 16), P)]
    # Front doors sit on the inset body; keep them clear of the side doors.
    shift = 0.5 if side == "left" else -0.5
    front = [b.moved(dx=shift) for b in front]
    return body + front + side_doors + COUNTER_TOP


for side in ("left", "right"):
    write(ROOM, f"counter_outer_{side}", outer_counter(side, False), TOP)
    write(ROOM, f"counter_outer_{side}_open", outer_counter(side, True), TOP)


def upper_cabinet(opened):
    parts = [Box((0, 4, 8), (16, 16, 16), P)] + cabinet_doors(4.75, 15.25, 7.5, 5.5, 8, opened)
    if opened:
        parts.append(Box((0.75, 4.75, 7.9), (15.25, 15.25, 8), S))
        parts.append(Box((0.75, 9.75, 7.5), (15.25, 10.25, 8), P))
    return parts


write(ROOM, "upper_cabinet", upper_cabinet(False))
write(ROOM, "upper_cabinet_open", upper_cabinet(True))

sink = cabinet_base()
sink[1] = Box((0, 1, 1), (16, 14, 16), P, faces={"up": "#basin"})
sink += [
    Box((0, 14, 0), (16, 16, 3), "#top"),
    Box((0, 14, 12), (16, 16, 16), "#top"),
    Box((0, 14, 3), (3, 16, 12), "#top"),
    Box((13, 14, 3), (16, 16, 12), "#top"),
    # Faucet
    Box((7, 16, 12.5), (9, 17, 14.5), "#metal"),
    Box((7.5, 17, 13), (8.5, 20, 14), "#metal"),
    Box((7.5, 19, 9.5), (8.5, 20, 13), "#metal"),
    Box((4.5, 16, 13), (5.5, 17.5, 14), "#metal"),
    Box((10.5, 16, 13), (11.5, 17.5, 14), "#metal"),
]
write(ROOM, "sink", sink, {**TOP, "basin": "minecraft:block/iron_block", "metal": "minecraft:block/iron_block"})


def stove(lit):
    burner = "#burner"
    return [
        Box((0, 0, 0), (16, 14, 16), "#side", faces={"north": "#front"}, uvs={"north": [0, 0, 16, 16]}),
        Box((0, 14, 0), (16, 16, 16), "#side", faces={"up": "#top"}),
    ] + [Box((x, 16, z), (x + 4, 16.5, z + 4), burner, glow=lit) for x in (2.5, 9.5) for z in (2.5, 9.5)]


STOVE = {"side": "reikos_furniture:block/appliance/steel", "top": "minecraft:block/black_concrete"}
write(ROOM, "stove", stove(False), {**STOVE, "front": "reikos_furniture:block/appliance/oven_front",
                                    "burner": "minecraft:block/gray_concrete"}, particle="reikos_furniture:block/appliance/steel")
write(ROOM, "stove_on", stove(True), {**STOVE, "front": "reikos_furniture:block/appliance/oven_front_on",
                                      "burner": "minecraft:block/magma"}, particle="reikos_furniture:block/appliance/steel")

FRIDGE = {"body": "reikos_furniture:block/appliance/fridge_body", "door": "reikos_furniture:block/appliance/fridge_door",
          "handle": "minecraft:block/light_gray_concrete", "inside": "minecraft:block/quartz_block_bottom"}
FRIDGE_LOWER = [
    Box((0, 0, 1), (16, 16, 16), "#body", skip=("up",)),
    Box((0.5, 0.5, 0), (15.5, 16, 1), "#door", skip=("up",)),
    Box((12.5, 8, -1), (13.5, 16, 0), "#handle", skip=("up",)),
]
FRIDGE_UPPER = [
    Box((0, 0, 1), (16, 16, 16), "#body", skip=("down",)),
    Box((0.5, 0, 0), (15.5, 6, 1), "#door", skip=("down",)),
    Box((0.5, 6.5, 0), (15.5, 15.5, 1), "#door"),
    Box((12.5, 0, -1), (13.5, 4, 0), "#handle", skip=("down",)),
    Box((12.5, 8, -1), (13.5, 12.5, 0), "#handle"),
]
write_tall(ROOM, "fridge", FRIDGE_LOWER, FRIDGE_UPPER, textures=FRIDGE, particle="minecraft:block/white_concrete")

# Open fridge: the doors swing out to the left (hinges on the left), showing shelves inside.
write(ROOM, "fridge_lower_open", [
    Box((0, 0, 1), (16, 16, 16), "#body", skip=("up",)),
    Box((1, 0.5, 0.9), (15, 16, 1), "#inside", skip=("up",)),
    Box((1, 5, 0.4), (15, 5.5, 1), "#handle"),
    Box((1, 10.5, 0.4), (15, 11, 1), "#handle"),
    Box((0.5, 0.5, -15), (1.5, 16, 0), "#door", skip=("up",)),
    Box((-0.5, 8, -13), (0.5, 16, -12), "#handle", skip=("up",)),
], FRIDGE, particle="minecraft:block/white_concrete")
write(ROOM, "fridge_upper_open", [
    Box((0, 0, 1), (16, 16, 16), "#body", skip=("down",)),
    Box((1, 0, 0.9), (15, 15.5, 1), "#inside", skip=("down",)),
    Box((1, 3, 0.4), (15, 3.5, 1), "#handle"),
    Box((1, 6, 0.4), (15, 6.5, 1), "#handle"),
    Box((0.5, 0, -15), (1.5, 6, 0), "#door", skip=("down",)),
    Box((0.5, 6.5, -15), (1.5, 15.5, 0), "#door"),
    Box((-0.5, 0, -13), (0.5, 4, -12), "#handle", skip=("down",)),
    Box((-0.5, 8, -13), (0.5, 12.5, -12), "#handle"),
], FRIDGE, particle="minecraft:block/white_concrete")

# ---- from kitchen_extra.py ----
ROOM = "kitchen"


# Microwave: sits on a counter. The window glows while it is cooking.
def microwave(on):
    return [
        Box((1.5, 0, 3), (14.5, 8, 13), "#body", faces={"north": "#front"}, uvs={"north": [0, 0, 16, 16]}),
        Box((9.75, 2, 2.5), (10.25, 6, 3), "#panel"),
    ]

MW = {"body": "reikos_furniture:block/appliance/fridge_body", "panel": "minecraft:block/gray_concrete"}
write(ROOM, "microwave", microwave(False), {**MW, "front": "reikos_furniture:block/appliance/microwave_front"},
      particle="reikos_furniture:block/appliance/fridge_body")
write(ROOM, "microwave_on", microwave(True), {**MW, "front": "reikos_furniture:block/appliance/microwave_front_on"},
      particle="reikos_furniture:block/appliance/fridge_body")


# Kitchen island: doors on the front and the back, stone top overhanging both sides.
def island(opened):
    front = cabinet_doors(1.75, 13.25, 1.5, 10, 12.5, opened)
    back = rotate_cw(rotate_cw(cabinet_doors(1.75, 13.25, 1.5, 10, 12.5, opened)))
    return [
        Box((0, 0, 3), (16, 1, 13), P),
        Box((0, 1, 2), (16, 14, 14), P),
        Box((0, 14, 0), (16, 16, 16), "#top"),
    ] + front + back

write(ROOM, "kitchen_island", island(False), TOP)
write(ROOM, "kitchen_island_open", island(True), TOP)


# Glass upper cabinet: like the upper cabinet, but the doors are glass with a wooden frame and you can see a shelf.
def glass_door(x1, x2, opened, hinge):
    frame = []
    y1, y2, zf, zb = 4.75, 15.25, 7.5, 8
    frame += door(x1, x2, y1, y1 + 1, zf, zb, S, opened, hinge)
    frame += door(x1, x2, y2 - 1, y2, zf, zb, S, opened, hinge)
    frame += door(x1, x1 + 1, y1 + 1, y2 - 1, zf, zb, S, opened, hinge) if not opened else []
    frame += door(x2 - 1, x2, y1 + 1, y2 - 1, zf, zb, S, opened, hinge) if not opened else []
    frame += door(x1 + 1, x2 - 1, y1 + 1, y2 - 1, zf + 0.2, zb - 0.2, "#glass", opened, hinge)
    return frame

def glass_upper_cabinet(opened):
    parts = [
        Box((0, 4, 8), (16, 16, 16), P),
        Box((0.75, 4.75, 7.9), (15.25, 15.25, 8), S),
        Box((0.75, 9.75, 7.6), (15.25, 10.25, 8), P),
    ]
    parts += glass_door(0.75, 7.85, opened, "left") + glass_door(8.15, 15.25, opened, "right")
    parts += door_handle(6.5, 7.25, 5.5, 8, 7, 7.5, 0.75, 7.85, 7.5, L, opened, "left")
    parts += door_handle(8.75, 9.5, 5.5, 8, 7, 7.5, 8.15, 15.25, 7.5, L, opened, "right")
    return parts

GLASS = {"glass": "minecraft:block/glass"}
write(ROOM, "glass_upper_cabinet", glass_upper_cabinet(False), GLASS, render_type="minecraft:cutout")
write(ROOM, "glass_upper_cabinet_open", glass_upper_cabinet(True), GLASS, render_type="minecraft:cutout")


# ---- 1.2: cutting board, pendant lamp, trash can ----

write(ROOM, "cutting_board", [
    Box((2, 0, 4), (13, 1, 12), P),
    Box((13, 0, 7), (15, 1, 9), P),
])


def pendant(on):
    return [
        Box((7.75, 8, 7.75), (8.25, 16, 8.25), "#cord"),
        Box((7, 7.5, 7), (9, 8, 9), "#cord"),
        Box((4, 4, 4), (12, 7.5, 12), "#wool", skip=("down",)),
        Box((6, 3.5, 6), (10, 4, 10), "#bulb", glow=on),
    ]

write(ROOM, "pendant_lamp", pendant(False), {"cord": "minecraft:block/black_concrete", "bulb": "minecraft:block/white_concrete"},
      particle="#wool")
write(ROOM, "pendant_lamp_on", pendant(True), {"cord": "minecraft:block/black_concrete", "bulb": "minecraft:block/ochre_froglight_side"},
      particle="#wool")


def trash_can(opened):
    parts = [Box((4, 0, 4), (12, 11, 12), "#metal"), Box((3.5, 10, 3.5), (12.5, 11, 12.5), "#rim")]
    if opened:
        parts.append(Box((3.5, 11, 12), (12.5, 20, 13), "#rim"))
        parts.append(Box((4.5, 10.9, 4.5), (11.5, 11, 11.5), "#dark"))
    else:
        parts.append(Box((3.5, 11, 3.5), (12.5, 12, 12.5), "#rim"))
        parts.append(Box((7, 12, 7), (9, 12.5, 9), "#dark"))
    return parts

TRASH = {"metal": "reikos_furniture:block/appliance/steel", "rim": "minecraft:block/iron_block", "dark": "minecraft:block/black_concrete"}
write(ROOM, "trash_can", trash_can(False), TRASH, particle="reikos_furniture:block/appliance/steel")
write(ROOM, "trash_can_open", trash_can(True), TRASH, particle="reikos_furniture:block/appliance/steel")
