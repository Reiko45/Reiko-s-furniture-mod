"""Model templates for the bedroom section. Run tools/models/generate_all.py to rewrite every template."""
from modelkit import *  # noqa: F401,F403

# ---- from bedroom.py ----
P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "bedroom"


def feet(h=2):
    return [Box((x, 0, z), (x + 2, h, z + 2), S) for x in (1, 13) for z in (1, 13)]


def drawer_unit(x1, x2, y1, y2, hx1, hx2, opened, pull=5.0):
    """Drawer front plus a handle centered vertically; both move out when opened."""
    mid = (y1 + y2) / 2
    d = pull if opened else 0
    parts = drawer(x1, x2, y1, y2, 1, 1.5, S, P, opened, pull)
    parts.append(Box((hx1, mid - 0.5, 0.5 - d), (hx2, mid + 0.5, 1 - d), L))
    return parts


# Nightstand: two drawers; the top one pulls out when open.
def nightstand(opened):
    return feet() + [
        Box((1, 2, 1.5), (15, 14, 15), P),
        Box((0, 14, 0), (16, 16, 16), P),
    ] + drawer_unit(2, 14, 8.5, 13, 7, 9, opened) + drawer_unit(2, 14, 3, 7.5, 7, 9, False)

write(ROOM, "nightstand", nightstand(False))
write(ROOM, "nightstand_open", nightstand(True))


# Dresser: two columns of three drawers; the top-left one pulls out when open.
def dresser(opened):
    parts = feet() + [
        Box((0.5, 2, 1.5), (15.5, 14, 15.5), P),
        Box((0, 14, 0), (16, 16, 16), P),
    ]
    for row, (y1, y2) in enumerate(((10, 13.5), (6, 9.5), (2.5, 5.5))):
        for col, (x1, x2, hx) in enumerate(((1.25, 7.75, 3.75), (8.25, 14.75, 10.75))):
            parts += drawer_unit(x1, x2, y1, y2, hx, hx + 1.5, opened and row == 0 and col == 0)
    return parts

write(ROOM, "dresser", dresser(False))
write(ROOM, "dresser_open", dresser(True))


# Wardrobe: two tall doors over two blocks; both swing open, showing a clothes rail.
def wardrobe(opened):
    lower = [
        Box((0, 0, 0.5), (16, 2, 16), S),
        Box((0.5, 2, 1), (15.5, 16, 16), P, skip=("up",)),
    ]
    lower += [b for b in door(1, 7.9, 2, 16, 0.5, 1, S, opened, "left") + door(8.1, 15, 2, 16, 0.5, 1, S, opened, "right")]
    upper = [
        Box((0.5, 0, 1), (15.5, 14, 16), P, skip=("down",)),
        Box((0, 14, 0), (16, 16, 16), P),
    ]
    upper += door(1, 7.9, 0, 13, 0.5, 1, S, opened, "left") + door(8.1, 15, 0, 13, 0.5, 1, S, opened, "right")
    upper += door_handle(6.5, 7.5, 0.5, 4.5, 0, 0.5, 1, 7.9, 0.5, L, opened, "left")
    upper += door_handle(8.5, 9.5, 0.5, 4.5, 0, 0.5, 8.1, 15, 0.5, L, opened, "right")
    if opened:
        lower.append(Box((1, 2, 0.9), (15, 16, 1), S, skip=("up",)))
        upper.append(Box((1, 0, 0.9), (15, 13, 1), S, skip=("down",)))
        upper.append(Box((1, 9.5, 0.4), (15, 10.25, 0.9), L))
    for b in lower:
        if b.t[1] == 16 and "up" not in b.skip and b.f[1] > 0:
            b.skip.add("up")
    for b in upper:
        if b.f[1] == 0 and "down" not in b.skip:
            b.skip.add("down")
    return lower, upper

lo, up = wardrobe(False)
write_tall(ROOM, "wardrobe", lo, up)
lo, up = wardrobe(True)
write(ROOM, "wardrobe_lower_open", lo)
write(ROOM, "wardrobe_upper_open", up)

# Desk: top, a side panel on the left, a drawer column on the right.
desk = [
    Box((0, 14, 0), (16, 16, 16), P),
    Box((1, 0, 1), (3, 14, 15), P),
    Box((9, 0, 1), (15, 14, 15), P),
    Box((3, 6, 13.5), (9, 14, 14.5), P),
]
for y1, y2 in ((9.5, 13), (5, 8.5), (0.5, 4)):
    desk.append(Box((9.5, y1, 0.5), (14.5, y2, 1), S))
    mid = (y1 + y2) / 2
    desk.append(Box((11.25, mid - 0.4, 0), (12.75, mid + 0.4, 0.5), L))
write(ROOM, "desk", desk)


# Table lamp: wooden base and stem, wool shade that glows when switched on.
def lamp(glow):
    return [
        Box((5, 0, 5), (11, 1, 11), P),
        Box((7.5, 1, 7.5), (8.5, 6, 8.5), L),
        Box((4.5, 6, 4.5), (11.5, 11, 11.5), "#shade", glow=glow),
    ]

write(ROOM, "table_lamp", lamp(False), {"shade": "minecraft:block/white_wool"})
write(ROOM, "table_lamp_on", lamp(True), {"shade": "minecraft:block/ochre_froglight_side"})

# ---- from bedroom_extra.py ----
P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "bedroom"
SHEET = "#sheet"
BED_TEX = {"sheet": "minecraft:block/white_wool", "wool": "reikos_furniture:block/fabric/red"}

# Bed (facing north: the head half is north of the foot half). Blanket uses #wool, sheets and pillow #sheet.
bed_head = [
    Box((0, 0, 0), (2, 15, 2), L),
    Box((14, 0, 0), (16, 15, 2), L),
    Box((2, 5, 0.5), (14, 13, 1.5), P),
    Box((0, 3, 2), (1, 6, 16), P),
    Box((15, 3, 2), (16, 6, 16), P),
    Box((1, 3, 2), (15, 4, 16), P),
    Box((1, 4, 2), (15, 8, 16), SHEET),
    Box((3, 8, 3), (13, 9.5, 7), SHEET),
    Box((1, 8, 8), (15, 8.75, 16), "#wool"),
    Box((0.75, 5, 8), (1, 8.75, 16), "#wool"),
    Box((15, 5, 8), (15.25, 8.75, 16), "#wool"),
]
bed_foot = [
    Box((0, 0, 14), (2, 10, 16), L),
    Box((14, 0, 14), (16, 10, 16), L),
    Box((2, 4, 14.5), (14, 9, 15.5), P),
    Box((0, 3, 0), (1, 6, 14), P),
    Box((15, 3, 0), (16, 6, 14), P),
    Box((1, 3, 0), (15, 4, 14), P),
    Box((1, 4, 0), (15, 8, 14), SHEET),
    Box((1, 8, 0), (15, 8.75, 14), "#wool"),
    Box((0.75, 5, 0), (1, 8.75, 14), "#wool"),
    Box((15, 5, 0), (15.25, 8.75, 14), "#wool"),
]
write(ROOM, "bed_head", bed_head, BED_TEX)
write(ROOM, "bed_foot", bed_foot, BED_TEX)
BED_ITEM_DISPLAY = {
    "gui": {"rotation": [30, 160, 0], "translation": [2, 3, 0], "scale": [0.5, 0.5, 0.5]},
    "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.25, 0.25, 0.25]},
    "fixed": {"rotation": [0, 180, 0], "translation": [0, 0, 0], "scale": [0.5, 0.5, 0.5]},
    "thirdperson_righthand": {"rotation": [30, 160, 0], "translation": [0, 3, -2], "scale": [0.375, 0.375, 0.375]},
    "firstperson_righthand": {"rotation": [30, 160, 0], "translation": [0, 0, 0], "scale": [0.375, 0.375, 0.375]},
    "firstperson_lefthand": {"rotation": [30, 160, 0], "translation": [0, 0, 0], "scale": [0.375, 0.375, 0.375]},
}
write(ROOM, "bed_item", [b.moved(dz=-8) for b in bed_head] + [b.moved(dz=8) for b in bed_foot], BED_TEX,
      display=BED_ITEM_DISPLAY, cull=False)

# Mirror: hangs on the wall behind it.
MIRROR = {"glass": "minecraft:block/light_blue_stained_glass"}
write(ROOM, "mirror", [
    Box((1, 1, 15), (15, 15, 16), P),
    Box((2, 2, 14.5), (14, 14, 15), "#glass"),
    Box((1, 1, 14), (15, 2, 15), P),
    Box((1, 14, 14), (15, 15, 15), P),
    Box((1, 2, 14), (2, 14, 15), P),
    Box((14, 2, 14), (15, 14, 15), P),
], MIRROR)


# Vanity: small table with a drawer and a mirror at the back.
def vanity(opened):
    parts = [Box((x, 0, z), (x + 1.5, 9, z + 1.5), L) for x in (1, 13.5) for z in (1, 13.5)] + [
        Box((0, 9, 0), (16, 11, 16), P),
        Box((1, 6, 1.5), (15, 9, 15), P),
        Box((2, 11, 13.5), (14, 16, 15.5), P),
        Box((3, 12, 13), (13, 15.5, 13.5), "#glass"),
    ]
    d = 4 if opened else 0
    parts += drawer(3, 13, 6.5, 8.75, 1, 1.5, S, P, opened, 4)
    parts.append(Box((7, 7.25, 0.5 - d), (9, 8, 1 - d), L))
    return parts

write(ROOM, "vanity", vanity(False), MIRROR)
write(ROOM, "vanity_open", vanity(True), MIRROR)

