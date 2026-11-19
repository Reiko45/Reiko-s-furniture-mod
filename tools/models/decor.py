"""Model templates for the decor section: parquet, baseboards, crown molding."""
from modelkit import *  # noqa: F401,F403

ROOM = "decor"
P, L, S = "#planks", "#log", "#stripped_log"


# Parquet: four quadrants of the wood's planks, every other one turned 90 degrees (a basket weave).
def parquet(y1, y2):
    boxes = []
    for qx in (0, 8):
        for qz in (0, 8):
            turned = (qx + qz) % 16 == 8
            skip = ["east" if qx == 0 else "west", "south" if qz == 0 else "north"]
            boxes.append(Box((qx, y1, qz), (qx + 8, y2, qz + 8), P, skip=skip,
                             rot={"up": 90, "down": 90} if turned else None))
    return boxes

write(ROOM, "parquet", parquet(0, 16))
write(ROOM, "parquet_slab", parquet(0, 8))
write(ROOM, "parquet_slab_top", parquet(8, 16))


# Wall trims: a strip along the back (the wall side) of the block, with corner versions like stairs.
# INNER_LEFT adds a strip along the east side, INNER_RIGHT along the west side; the outer corners are
# just the matching back corner.
def trim(profile, shape):
    """profile: list of (y1, y2, depth) layers, depth measured from the wall."""
    parts = []
    for y1, y2, depth, tex in profile:
        back = (16 - depth, 16)
        if shape in ("straight", "inner_left", "inner_right"):
            parts.append(Box((0, y1, back[0]), (16, y2, 16), tex))
        if shape == "inner_left":
            parts.append(Box((16 - depth, y1, 0), (16, y2, back[0]), tex))
        if shape == "inner_right":
            parts.append(Box((0, y1, 0), (depth, y2, back[0]), tex))
        if shape == "outer_left":
            parts.append(Box((16 - depth, y1, back[0]), (16, y2, 16), tex))
        if shape == "outer_right":
            parts.append(Box((0, y1, back[0]), (depth, y2, 16), tex))
    return parts

BASEBOARD = [(0, 3, 2, P), (3, 3.5, 1.25, S)]
CROWN = [(14, 16, 2, P), (13, 14, 1.25, S), (12.5, 13, 0.75, S)]
for shape in ("straight", "inner_left", "inner_right", "outer_left", "outer_right"):
    write(ROOM, "baseboard_" + shape, trim(BASEBOARD, shape))
    write(ROOM, "crown_molding_" + shape, trim(CROWN, shape))


# ---- Doors and windows ----------------------------------------------------------------------
GLASS = {"glass": "reikos_furniture:block/window_glass", "metal": "minecraft:block/iron_block"}
G = "#glass"


# French door. Like vanilla door models, the closed door is the slab x 0..3 (the blockstate turns it).
# The handle sits on the edge away from the hinge: near z=14 ("south") or z=2 ("north").
def french_door_bottom(handle):
    hz = 12 if handle == "south" else 3
    return [
        Box((0, 0, 0), (3, 16, 2), P), Box((0, 0, 14), (3, 16, 16), P),
        Box((0, 0, 2), (3, 4, 14), S),
        Box((1.25, 4, 2), (1.75, 16, 14), G),
        Box((0.75, 9.5, 2), (2.25, 10.5, 14), P),
        Box((0.75, 4, 7.5), (2.25, 16, 8.5), P),
        Box((-0.75, 7, hz), (3.75, 8, hz + 1), "#metal"),
    ]


def french_door_top():
    return [
        Box((0, 0, 0), (3, 16, 2), P), Box((0, 0, 14), (3, 16, 16), P),
        Box((0, 13, 2), (3, 16, 14), S),
        Box((1.25, 0, 2), (1.75, 13, 14), G),
        Box((0.75, 6, 2), (2.25, 7, 14), P),
        Box((0.75, 0, 7.5), (2.25, 13, 8.5), P),
    ]


TRANSLUCENT = "minecraft:translucent"
for handle in ("north", "south"):
    write(ROOM, "french_door_bottom_handle_" + handle, french_door_bottom(handle), GLASS, render_type=TRANSLUCENT)
write(ROOM, "french_door_top", french_door_top(), GLASS, render_type=TRANSLUCENT)
write(ROOM, "french_door_item", french_door_bottom("south") + [b.moved(dy=16) for b in french_door_top()], GLASS,
      display=TALL_ITEM_DISPLAY, cull=False, render_type=TRANSLUCENT)


# Sliding glass door: two blocks tall, at the back of the block (in the wall line). Opening slides the
# panel 14 pixels to the west, into the wall next to it.
def sliding_panel(lower):
    if lower:
        return [Box((1, 0.5, 13.75), (2.5, 16, 15.25), P), Box((13.5, 0.5, 13.75), (15, 16, 15.25), P),
                Box((2.5, 0.5, 13.75), (13.5, 2.5, 15.25), P), Box((2.5, 2.5, 14.25), (13.5, 16, 14.75), G),
                Box((12, 7, 13.25), (12.75, 10, 13.75), "#metal")]
    return [Box((1, 0, 13.75), (2.5, 15, 15.25), P), Box((13.5, 0, 13.75), (15, 15, 15.25), P),
            Box((2.5, 13.5, 13.75), (13.5, 15, 15.25), P), Box((2.5, 0, 14.25), (13.5, 13.5, 14.75), G)]


def sliding_frame(lower):
    parts = [Box((0, 0, 13), (1, 16, 16), S), Box((15, 0, 13), (16, 16, 16), S)]
    parts.append(Box((1, 0, 13), (15, 0.5, 16), S) if lower else Box((1, 15, 13), (15, 16, 16), S))
    return parts


for lower, half in ((True, "lower"), (False, "upper")):
    write(ROOM, "sliding_door_" + half, sliding_frame(lower) + sliding_panel(lower), GLASS, render_type=TRANSLUCENT)
    write(ROOM, "sliding_door_" + half + "_open", sliding_frame(lower) + [b.moved(dx=-14) for b in sliding_panel(lower)],
          GLASS, render_type=TRANSLUCENT, cull=False)
write(ROOM, "sliding_door_item", sliding_frame(True) + sliding_panel(True)
      + [b.moved(dy=16) for b in sliding_frame(False) + sliding_panel(False)], GLASS,
      display=TALL_ITEM_DISPLAY, cull=False, render_type=TRANSLUCENT)


# Framed window: a pane in the middle of the block, with frame bars only on the sides that don't join
# another window. Open: the sash tilts in.
pane = [Box((0, 0, 7.75), (16, 16, 8.25), G)]
pane_open = [Box((1.5, 1.5, 7.75), (14.5, 14.5, 8.25), G, rotation={"origin": [8, 1.5, 8], "axis": "x", "angle": -22.5}),
             Box((1.5, 1.5, 7.5), (14.5, 2.5, 8.5), P, rotation={"origin": [8, 1.5, 8], "axis": "x", "angle": -22.5}),
             Box((1.5, 13.5, 7.5), (14.5, 14.5, 8.5), P, rotation={"origin": [8, 1.5, 8], "axis": "x", "angle": -22.5})]
frame = {
    "top": [Box((0, 14.5, 6.5), (16, 16, 9.5), P)],
    "bottom": [Box((0, 0, 6.5), (16, 1.5, 9.5), P), Box((-0.5, 0, 4.5), (16.5, 1, 11.5), S)],
    "left": [Box((0, 0, 6.5), (1.5, 16, 9.5), P)],
    "right": [Box((14.5, 0, 6.5), (16, 16, 9.5), P)],
}
write(ROOM, "window_pane", pane, GLASS, render_type=TRANSLUCENT)
write(ROOM, "window_pane_open", pane_open, GLASS, render_type=TRANSLUCENT, cull=False)
for side, boxes in frame.items():
    write(ROOM, "window_frame_" + side, boxes, render_type=TRANSLUCENT)
write(ROOM, "window_item", pane + sum(frame.values(), []), GLASS, render_type=TRANSLUCENT)


# Picture frame: a wooden frame around a light backing, on the wall (back of the block).
write(ROOM, "picture_frame", [
    Box((2, 2, 15.25), (14, 14, 16), "#backing"),
    Box((1, 1, 14.5), (15, 2, 16), P), Box((1, 14, 14.5), (15, 15, 16), P),
    Box((1, 2, 14.5), (2, 14, 16), P), Box((14, 2, 14.5), (15, 14, 16), P),
], {"backing": "minecraft:block/white_concrete_powder"})

# Light switch: a wall plate with a rocker; the rocker tips up when the lights are on.
def light_switch(on):
    top, bottom = (10.5, 7.5) if on else (8.5, 5.5)
    return [
        Box((5, 4, 15), (11, 12, 16), "#plate"),
        Box((6.5, 5, 14.75), (9.5, 11, 15), "#frame"),
        Box((7, bottom, 14.25), (9, top, 14.75), "#plate"),
    ]

SWITCH = {"plate": "minecraft:block/white_concrete", "frame": "minecraft:block/light_gray_concrete"}
write(ROOM, "light_switch", light_switch(False), SWITCH, particle="minecraft:block/white_concrete")
write(ROOM, "light_switch_on", light_switch(True), SWITCH, particle="minecraft:block/white_concrete")
