"""Model templates for the kids section. Run tools/models/generate_all.py to rewrite every template."""
from modelkit import *  # noqa: F401,F403

# ---- from kids.py ----
P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "kids"
SHEET = "#sheet"
BED_TEX = {"sheet": "minecraft:block/white_wool", "wool": "reikos_furniture:block/fabric/red"}


# Toy chest: lid opens upwards.
def toy_chest(opened):
    parts = [
        Box((1, 0, 2), (15, 9, 14), P),
        Box((1, 3, 1.5), (15, 5, 2), S),
        Box((7, 6, 1.5), (9, 8, 2), L),
    ]
    if opened:
        parts.append(Box((1, 9, 13), (15, 21, 15), S))
        parts.append(Box((1.5, 8.9, 2.5), (14.5, 9, 13.5), "#inside"))
    else:
        parts.append(Box((1, 9, 2), (15, 11, 14), S))
    return parts

write(ROOM, "toy_chest", toy_chest(False), {"inside": "reikos_furniture:block/fabric/red"})
write(ROOM, "toy_chest_open", toy_chest(True), {"inside": "reikos_furniture:block/fabric/red"})

# Kids' table: low table that joins like the dining table.
top = [Box((0, 9, 0), (16, 10.5, 16), P)]
leg = Box((1.5, 0, 1.5), (3.5, 9, 3.5), S)
write(ROOM, "kids_table_top", top)
write(ROOM, "kids_table_leg", [leg])
write(ROOM, "kids_table_item", top + [leg.moved(dx=dx, dz=dz) for dx in (0, 11) for dz in (0, 11)])

# Kids' chair: small and low.
write(ROOM, "kids_chair", [
    Box((3.5, 0, 3.5), (5, 5, 5), S), Box((11, 0, 3.5), (12.5, 5, 5), S),
    Box((3.5, 0, 11), (5, 5, 12.5), S), Box((11, 0, 11), (12.5, 5, 12.5), S),
    Box((3, 5, 3), (13, 6.5, 13), P),
    Box((3.5, 6.5, 11.25), (5, 13, 12.5), S), Box((11, 6.5, 11.25), (12.5, 13, 12.5), S),
    Box((5, 9, 11.5), (11, 12, 12.25), P),
])

# Rocking horse: runners, legs, body, head and a red saddle.
write(ROOM, "rocking_horse", [
    Box((3, 0, 2), (4.5, 1, 14), S), Box((11.5, 0, 2), (13, 1, 14), S),
    Box((3, 1, 0), (4.5, 2, 3), S), Box((11.5, 1, 0), (13, 2, 3), S),
    Box((3, 1, 13), (4.5, 2, 16), S), Box((11.5, 1, 13), (13, 2, 16), S),
    Box((4, 1, 4), (5.5, 6, 5.5), P), Box((10.5, 1, 4), (12, 6, 5.5), P),
    Box((4, 1, 10.5), (5.5, 6, 12), P), Box((10.5, 1, 10.5), (12, 6, 12), P),
    Box((4.5, 6, 3), (11.5, 10, 13), P),
    Box((5.5, 9, 1.5), (10.5, 14, 4.5), P),
    Box((5.5, 12, -1.5), (10.5, 15.5, 1.5), P),
    Box((7, 14, 2), (9, 15.5, 5), L),
    Box((7, 7, 13), (9, 10, 15), L),
    Box((4.25, 10, 5.5), (11.75, 10.5, 10.5), "#saddle"),
], {"saddle": "reikos_furniture:block/fabric/red"})


# Bunk bed: the lower bed works like a normal bed; the top bunk is decoration.
def bunk_part(head, top):
    """head: head or foot half; top: upper bunk (in the block above) or lower bed."""
    zs = (0, 2) if head else (14, 16)
    post_h = 14 if top else 16
    parts = [Box((0, 0, zs[0]), (2, post_h, zs[1]), L), Box((14, 0, zs[0]), (16, post_h, zs[1]), L)]
    y = 4 if top else 3
    rail_z = (2, 16) if head else (0, 14)
    parts += [
        Box((0, y, rail_z[0]), (1, y + 3, rail_z[1]), P),
        Box((15, y, rail_z[0]), (16, y + 3, rail_z[1]), P),
        Box((1, y, rail_z[0]), (15, y + 1, rail_z[1]), P),
        Box((1, y + 1, rail_z[0]), (15, y + 5, rail_z[1]), SHEET),
    ]
    if head:
        parts.append(Box((3, y + 5, 3), (13, y + 6.5, 7), SHEET))
        parts.append(Box((1, y + 5, 8), (15, y + 5.75, 16), "#wool"))
        parts.append(Box((2, y + 6, 0.5), (14, y + 10, 1.5), P))
    else:
        parts.append(Box((1, y + 5, 0), (15, y + 5.75, 14), "#wool"))
        parts.append(Box((2, y + 4, 14.5), (14, y + 8, 15.5), P))
    if top:
        # Safety rail along the left side of the top bunk.
        parts.append(Box((0, y + 5, rail_z[0]), (1, y + 8, rail_z[1]), P))
    return parts

for top in (False, True):
    for head in (True, False):
        write(ROOM, f"bunk_bed_{'top' if top else 'lower'}_{'head' if head else 'foot'}", bunk_part(head, top), BED_TEX)

ITEM_DISPLAY = dict(TALL_ITEM_DISPLAY)
ITEM_DISPLAY["gui"] = {"rotation": [30, 160, 0], "translation": [1, -3, 0], "scale": [0.36, 0.36, 0.36]}
item = ([b.moved(dz=-8) for b in bunk_part(True, False)] + [b.moved(dz=8) for b in bunk_part(False, False)]
        + [b.moved(dz=-8, dy=16) for b in bunk_part(True, True)] + [b.moved(dz=8, dy=16) for b in bunk_part(False, True)])
write(ROOM, "bunk_bed_item", item, BED_TEX, display=ITEM_DISPLAY, cull=False)

