"""Model templates for stone, concrete, copper and wrought-iron furniture.

Stone and concrete pieces use #side, #top and #handle, filled in per stone or color by data
generation; copper pieces use #copper (one texture per age); iron pieces use #iron.
"""
from modelkit import *  # noqa: F401,F403

SIDE, TOP, HANDLE = "#side", "#top", "#handle"
STONE = {"handle": "reikos_furniture:block/style/wrought_iron", "soil": "minecraft:block/rooted_dirt",
         "side": "minecraft:block/stone_bricks", "top": "minecraft:block/smooth_stone"}


def slab(f, t):
    """A stone slab: #top above and below, #side around."""
    return Box(f, t, SIDE, faces={"up": TOP, "down": TOP})


def corners(leg):
    xs = [v for b in leg for v in (b.f[0], b.t[0])]
    zs = [v for b in leg for v in (b.f[2], b.t[2])]
    ox, oz = 16 - min(xs) - max(xs), 16 - min(zs) - max(zs)
    return [b.moved(dx=dx, dz=dz) for dx in (0, ox) for dz in (0, oz) for b in leg]


# ---- Stone and concrete ---------------------------------------------------------------------
ROOM = "stone"
for name, top, leg in (
        ("dining_table", [slab((0, 13, 0), (16, 16, 16))],
         [Box((1.5, 0, 1.5), (4, 13, 4), SIDE), Box((1, 0, 1), (4.5, 1, 4.5), SIDE), Box((1, 12, 1), (4.5, 13, 4.5), SIDE)]),
        ("coffee_table", [slab((0, 6, 0), (16, 8, 16))], [Box((1, 0, 1), (4, 6, 4), SIDE)])):
    write(ROOM, name + "_top", top, STONE, particle=SIDE)
    write(ROOM, name + "_leg", leg, STONE, particle=SIDE)
    write(ROOM, name + "_item", top + corners(leg), STONE, particle=SIDE)

write(ROOM, "bench", [slab((0, 6, 3), (16, 8, 13)), Box((1.5, 0, 4), (4, 6, 12), SIDE), Box((12, 0, 4), (14.5, 6, 12), SIDE)],
      STONE, particle=SIDE)

# A full block of stone with a rim round a bed of soil, for plants to grow on.
write(ROOM, "planter", [
    Box((0, 0, 0), (16, 14, 16), SIDE),
    Box((0, 14, 0), (16, 16, 1.5), SIDE), Box((0, 14, 14.5), (16, 16, 16), SIDE),
    Box((0, 14, 1.5), (1.5, 16, 14.5), SIDE), Box((14.5, 14, 1.5), (16, 16, 14.5), SIDE),
    Box((1.5, 14, 1.5), (14.5, 15.5, 14.5), "#soil"),
], STONE, particle=SIDE)


def cabinet_doors(y1, y2, zf, hy1, hy2, opened):
    zb = zf + 0.5
    return (door(0.75, 7.85, y1, y2, zf, zb, SIDE, opened, "left")
            + door(8.15, 15.25, y1, y2, zf, zb, SIDE, opened, "right")
            + door_handle(6.75, 7.25, hy1, hy2, zf - 0.5, zf, 0.75, 7.85, zf, HANDLE, opened, "left")
            + door_handle(8.75, 9.25, hy1, hy2, zf - 0.5, zf, 8.15, 15.25, zf, HANDLE, opened, "right"))


COUNTER_TOP = [slab((0, 14, 0), (16, 16, 16))]


def counter(opened):
    parts = [Box((0, 0, 2), (16, 1, 16), SIDE), Box((0, 1, 1), (16, 14, 16), SIDE)] + cabinet_doors(1.75, 13.25, 0.5, 10, 12.5, opened)
    if opened:
        parts += [Box((0.75, 1.75, 0.9), (15.25, 13.25, 1), SIDE), Box((0.75, 7.25, 0.5), (15.25, 7.75, 1), SIDE)]
    return parts + COUNTER_TOP


def outer_counter(side, opened):
    doors = cabinet_doors(1.75, 13.25, 0.5, 10, 12.5, opened)
    if side == "left":
        side_doors = rotate_ccw(cabinet_doors(1.75, 13.25, 0.5, 10, 12.5, opened))
        body = [Box((2, 0, 2), (16, 1, 16), SIDE), Box((1, 1, 1), (16, 14, 16), SIDE)]
    else:
        side_doors = rotate_cw(cabinet_doors(1.75, 13.25, 0.5, 10, 12.5, opened))
        body = [Box((0, 0, 2), (14, 1, 16), SIDE), Box((0, 1, 1), (15, 14, 16), SIDE)]
    shift = 0.5 if side == "left" else -0.5
    return body + [b.moved(dx=shift) for b in doors] + side_doors + COUNTER_TOP


for suffix, opened in (("", False), ("_open", True)):
    write(ROOM, "counter" + suffix, counter(opened), STONE, particle=SIDE)
    for side in ("left", "right"):
        write(ROOM, f"counter_outer_{side}{suffix}", outer_counter(side, opened), STONE, particle=SIDE)
write(ROOM, "counter_inner", [Box((0, 0, 2), (16, 1, 16), SIDE), Box((0, 1, 1), (16, 14, 16), SIDE),
                              Box((0.75, 1.75, 0.5), (15.25, 13.25, 1), SIDE)] + COUNTER_TOP, STONE, particle=SIDE)

# ---- Copper ---------------------------------------------------------------------------------
ROOM = "metal"
C = "#copper"
COPPER = {"copper": "minecraft:block/copper_block", "bulb": "minecraft:block/ochre_froglight_side"}


def copper_lamp(on):
    return [Box((5, 0, 5), (11, 1, 11), C), Box((7.5, 1, 7.5), (8.5, 7, 8.5), C),
            Box((4.5, 9, 4.5), (11.5, 11, 11.5), C), Box((5.5, 11, 5.5), (10.5, 12, 10.5), C),
            Box((4.5, 7, 4.5), (5, 9, 11.5), C), Box((11, 7, 4.5), (11.5, 9, 11.5), C),
            Box((5, 7, 4.5), (11, 9, 5), C), Box((5, 7, 11), (11, 9, 11.5), C),
            Box((6.5, 7.5, 6.5), (9.5, 9, 9.5), "#bulb", glow=on)]


write(ROOM, "copper_lamp", copper_lamp(False), COPPER, particle=C)
write(ROOM, "copper_lamp_on", copper_lamp(True), COPPER, particle=C)
copper_top = [Box((0, 14, 0), (16, 15, 16), C), Box((0, 15, 0), (16, 16, 16), C)]
copper_leg = [Box((1, 0, 1), (2.5, 14, 2.5), C), Box((0.75, 0, 0.75), (2.75, 0.5, 2.75), C)]
write(ROOM, "copper_table_top", copper_top, COPPER, particle=C)
write(ROOM, "copper_table_leg", copper_leg, COPPER, particle=C)
write(ROOM, "copper_table_item", copper_top + corners(copper_leg), COPPER, particle=C)
chair = [Box((x, 0, z), (x + 1.25, 8, z + 1.25), C) for x in (3, 11.75) for z in (3, 11.75)]
chair += [Box((2.5, 8, 2.5), (13.5, 9, 13.5), C), Box((3, 9, 12), (4.25, 16, 13.25), C), Box((11.75, 9, 12), (13, 16, 13.25), C),
          Box((4.25, 14.5, 12.25), (11.75, 15.75, 13), C), Box((4.25, 11.5, 12.25), (11.75, 12.5, 13), C),
          Box((4.25, 3, 3.25), (11.75, 3.75, 3.75), C), Box((4.25, 3, 12.25), (11.75, 3.75, 12.75), C)]
write(ROOM, "copper_chair", chair, COPPER, particle=C)

# ---- Wrought iron -----------------------------------------------------------------------------
I = "#iron"
IRON = {"iron": "reikos_furniture:block/style/wrought_iron", "glass": "minecraft:block/glass",
        "light": "minecraft:block/ochre_froglight_side"}

garden_chair = [Box((x, 0, z), (x + 1, 8, z + 1), I) for x in (2.5, 12.5) for z in (2.5, 12.5)]
garden_chair += [Box((2, 7, 2), (14, 8, 14), I), Box((2.5, 8, 12.5), (3.5, 16, 13.5), I), Box((12.5, 8, 12.5), (13.5, 16, 13.5), I),
                 Box((3.5, 15, 12.75), (12.5, 16, 13.25), I)]
garden_chair += [Box((x, 9, 12.75), (x + 0.75, 15, 13.25), I) for x in (5, 7.6, 10.25)]
garden_chair += [Box((x, 11.5, 12.6), (x + 2, 12.5, 13.4), I) for x in (4.5, 9.5)]
write(ROOM, "wrought_iron_garden_chair", garden_chair, IRON, particle=I)

write(ROOM, "wrought_iron_garden_table", [
    Box((1, 12, 1), (15, 13.5, 15), I), Box((0, 12.5, 2), (1, 13.5, 14), I), Box((15, 12.5, 2), (16, 13.5, 14), I),
    Box((2, 12.5, 0), (14, 13.5, 1), I), Box((2, 12.5, 15), (14, 13.5, 16), I),
    Box((7, 1, 7), (9, 12, 9), I), Box((6, 10, 6), (10, 12, 10), I),
    Box((3, 0, 7), (13, 1, 9), I), Box((7, 0, 3), (9, 1, 13), I),
], IRON, particle=I)

bench = [Box((x, 0, 3), (x + 1.5, 7, 4.5), I) for x in (1, 13.5)] + [Box((x, 0, 11.5), (x + 1.5, 15, 13), I) for x in (1, 13.5)]
bench += [Box((0, 7, 3), (16, 8, 12), I), Box((0.5, 8, 2.5), (1.5, 11, 12), I), Box((14.5, 8, 2.5), (15.5, 11, 12), I)]
bench += [Box((2.5, y, 12), (13.5, y + 1, 12.75), I) for y in (9, 11.5, 14)]
write(ROOM, "wrought_iron_bench", bench, IRON, particle=I)


def lamp_post_upper(on):
    return [Box((7, 0, 7), (9, 8, 9), I),
            Box((5, 8, 5), (11, 9, 11), I),
            Box((5.5, 9, 5.5), (10.5, 14, 10.5), "#light" if on else "#glass", glow=on),
            Box((5, 9, 5), (5.75, 14, 5.75), I), Box((10.25, 9, 5), (11, 14, 5.75), I),
            Box((5, 9, 10.25), (5.75, 14, 11), I), Box((10.25, 9, 10.25), (11, 14, 11), I),
            Box((4.5, 14, 4.5), (11.5, 15, 11.5), I), Box((6.5, 15, 6.5), (9.5, 16, 9.5), I)]


lamp_lower = [Box((5, 0, 5), (11, 1, 11), I), Box((6, 1, 6), (10, 2, 10), I), Box((7, 2, 7), (9, 16, 9), I, skip=("up",)),
              Box((6.5, 6, 6.5), (9.5, 7, 9.5), I)]
up_off = lamp_post_upper(False)
for b in up_off:
    if b.f[1] == 0:
        b.skip.add("down")
write_tall(ROOM, "wrought_iron_lamp_post", lamp_lower, up_off, IRON, particle=I)
up_on = lamp_post_upper(True)
for b in up_on:
    if b.f[1] == 0:
        b.skip.add("down")
write(ROOM, "wrought_iron_lamp_post_upper_on", up_on, IRON, particle=I)
