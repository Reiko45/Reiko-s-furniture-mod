"""Model templates for the dining section. Run tools/models/generate_all.py to rewrite every template."""
from modelkit import *  # noqa: F401,F403

# ---- from dining.py ----
P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "dining"

# Dining table: the top is always drawn, legs are added per open corner by the blockstate.
top = [Box((0, 14, 0), (16, 16, 16), P)]
leg = Box((1, 0, 1), (3, 14, 3), S)
write(ROOM, "dining_table_top", top)
write(ROOM, "dining_table_leg", [leg])
write(ROOM, "dining_table_item", top + [leg.moved(dx=dx, dz=dz) for dx in (0, 12) for dz in (0, 12)])

# Dining chair: seat at 9 px, backrest at the back (south side when facing north).
write(ROOM, "dining_chair", [
    Box((2.5, 0, 2.5), (4, 7, 4), S),
    Box((12, 0, 2.5), (13.5, 7, 4), S),
    Box((2.5, 0, 12.5), (4, 7, 14), S),
    Box((12, 0, 12.5), (13.5, 7, 14), S),
    Box((2, 7, 2), (14, 9, 14), P),
    Box((2.5, 9, 12.5), (4, 16, 14), S),
    Box((12, 9, 12.5), (13.5, 16, 14), S),
    Box((4, 10.5, 12.75), (12, 11.5, 13.75), P),
    Box((4, 13, 12.75), (12, 15, 13.75), P),
])

# Bench: seat at 8 px, two side panels and a stretcher.
write(ROOM, "bench", [
    Box((0, 6, 2.5), (16, 8, 13.5), P),
    Box((1.5, 0, 3.5), (3.5, 6, 12.5), S),
    Box((12.5, 0, 3.5), (14.5, 6, 12.5), S),
    Box((3.5, 2, 7), (12.5, 3, 9), S),
])

# Sideboard: two drawers over two doors, on small feet. When open, the doors swing out.
def sideboard(opened):
    parts = [
        Box((1, 0, 1.5), (2.5, 2, 3), L),
        Box((13.5, 0, 1.5), (15, 2, 3), L),
        Box((1, 0, 13), (2.5, 2, 14.5), L),
        Box((13.5, 0, 13), (15, 2, 14.5), L),
        Box((0.5, 2, 1.5), (15.5, 14, 15.5), P),
        Box((0, 14, 0.5), (16, 16, 16), P),
        Box((1.25, 10.5, 1), (7.75, 13.25, 1.5), S),
        Box((8.25, 10.5, 1), (14.75, 13.25, 1.5), S),
        Box((4, 11.5, 0.5), (5, 12.25, 1), L),
        Box((11, 11.5, 0.5), (12, 12.25, 1), L),
    ]
    parts += door(1.25, 7.75, 2.75, 10, 1, 1.5, S, opened, "left")
    parts += door(8.25, 14.75, 2.75, 10, 1, 1.5, S, opened, "right")
    parts += door_handle(6.75, 7.25, 7, 9, 0.5, 1, 1.25, 7.75, 1, L, opened, "left")
    parts += door_handle(8.75, 9.25, 7, 9, 0.5, 1, 8.25, 14.75, 1, L, opened, "right")
    if opened:
        parts.append(Box((1.25, 2.75, 1.4), (14.75, 10, 1.5), S))
        parts.append(Box((1.25, 6.25, 1), (14.75, 6.75, 1.5), P))
    return parts


write(ROOM, "sideboard", sideboard(False))
write(ROOM, "sideboard_open", sideboard(True))

# ---- from dining_extra.py ----
P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "dining"
GLASS = {"glass": "minecraft:block/glass"}


def glass_panel_door(x1, x2, y1, y2, zf, zb, opened, hinge):
    """Wooden frame with a glass pane; open = swung out (frame drawn as one slab, glass inside)."""
    if opened:
        return door(x1, x2, y1, y2, zf, zb, S, True, hinge)
    return [
        Box((x1, y1, zf), (x2, y1 + 1, zb), S),
        Box((x1, y2 - 1, zf), (x2, y2, zb), S),
        Box((x1, y1 + 1, zf), (x1 + 1, y2 - 1, zb), S),
        Box((x2 - 1, y1 + 1, zf), (x2, y2 - 1, zb), S),
        Box((x1 + 1, y1 + 1, zf + 0.2), (x2 - 1, y2 - 1, zb - 0.2), "#glass"),
    ]


def cupboard(opened):
    """Two blocks tall: wooden doors below, glass doors and shelves above."""
    lower = [
        Box((0, 0, 0.5), (16, 2, 16), S),
        Box((0.5, 2, 1), (15.5, 16, 16), P, skip=("up",)),
        Box((0, 13, 0), (16, 14, 16), P),
    ]
    lower += door(1, 7.9, 2.5, 12.5, 0.5, 1, S, opened, "left") + door(8.1, 15, 2.5, 12.5, 0.5, 1, S, opened, "right")
    lower += door_handle(6.75, 7.25, 9, 11.5, 0, 0.5, 1, 7.9, 0.5, L, opened, "left")
    lower += door_handle(8.75, 9.25, 9, 11.5, 0, 0.5, 8.1, 15, 0.5, L, opened, "right")
    upper = [
        Box((0.5, 0, 3), (15.5, 14, 16), P, skip=("down",)),
        Box((0, 14, 0), (16, 16, 16), P),
        Box((0.5, 0, 1), (1, 14, 3), P, skip=("down",)),
        Box((15, 0, 1), (15.5, 14, 3), P, skip=("down",)),
        Box((1, 6.5, 1.5), (15, 7, 3), P),
        Box((1, 0, 2.9), (15, 14, 3), S, skip=("down",)),
    ]
    upper += glass_panel_door(1, 7.9, 0.5, 13.5, 0.5, 1, opened, "left")
    upper += glass_panel_door(8.1, 15, 0.5, 13.5, 0.5, 1, opened, "right")
    return lower, upper


lo, up = cupboard(False)
write_tall(ROOM, "glass_cupboard", lo, up, GLASS, render_type="minecraft:cutout")
lo, up = cupboard(True)
write(ROOM, "glass_cupboard_lower_open", lo, GLASS, render_type="minecraft:cutout")
write(ROOM, "glass_cupboard_upper_open", up, GLASS, render_type="minecraft:cutout")


# ---- 1.2: plate, chandelier ----

# Plate: a white ceramic plate; one item lies on it.
write(ROOM, "plate", [
    Box((4, 0, 4), (12, 0.5, 12), "#plate"),
    Box((3, 0.5, 3), (13, 1, 4), "#plate"), Box((3, 0.5, 12), (13, 1, 13), "#plate"),
    Box((3, 0.5, 4), (4, 1, 12), "#plate"), Box((12, 0.5, 4), (13, 1, 12), "#plate"),
], {"plate": "minecraft:block/white_concrete"}, particle="minecraft:block/white_concrete")


# Chandelier: hangs from the ceiling; four candles that glow when lit.
def chandelier(lit):
    parts = [
        Box((7.5, 11, 7.5), (8.5, 16, 8.5), "#chain"),
        Box((6.5, 9, 6.5), (9.5, 11, 9.5), "#gold"),
        Box((2, 9, 7.5), (14, 10, 8.5), "#gold"),
        Box((7.5, 9, 2), (8.5, 10, 14), "#gold"),
    ]
    for x, z in ((2, 7), (13, 7), (7, 2), (7, 13)):
        parts.append(Box((x, 10, z), (x + 1, 12.5, z + 2) if x in (2, 13) else (x + 2, 12.5, z + 1), "#candle"))
        cx, cz = (x, z + 0.5) if x in (2, 13) else (x + 0.5, z)
        parts.append(Box((cx + 0.25, 12.5, cz + 0.25), (cx + 0.75, 13.5, cz + 0.75), "#flame", glow=lit))
    return parts

CHANDELIER = {"chain": "minecraft:block/chain", "gold": "minecraft:block/gold_block", "candle": "minecraft:block/white_concrete"}
write(ROOM, "chandelier", chandelier(False), {**CHANDELIER, "flame": "minecraft:block/white_concrete"},
      particle="minecraft:block/gold_block", render_type="minecraft:cutout")
write(ROOM, "chandelier_on", chandelier(True), {**CHANDELIER, "flame": "minecraft:block/ochre_froglight_side"},
      particle="minecraft:block/gold_block", render_type="minecraft:cutout")
