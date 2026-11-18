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
