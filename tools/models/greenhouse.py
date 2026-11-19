"""Model templates for the greenhouse and garden section. Plants in pots are drawn by a renderer."""
from modelkit import *  # noqa: F401,F403

ROOM = "greenhouse"
P, L, S = "#planks", "#log", "#stripped_log"
SOIL = "minecraft:block/rooted_dirt"


def pot_rim(x1, x2, y1, y2, z1, z2, t, tex):
    """A square rim of thickness t around an opening (the soil shows through the middle)."""
    return [Box((x1, y1, z1), (x2, y2, z1 + t), tex), Box((x1, y1, z2 - t), (x2, y2, z2), tex),
            Box((x1, y1, z1 + t), (x1 + t, y2, z2 - t), tex), Box((x2 - t, y1, z1 + t), (x2, y2, z2 - t), tex)]


# Plant stand: four legs, a slatted lower shelf, a top and a small terracotta pot.
stand = [Box((x, 0, z), (x + 1.5, 9, z + 1.5), L) for x in (2.5, 12) for z in (2.5, 12)]
stand += [Box((3, 3, z), (13, 4, z + 2), S) for z in (3.5, 7, 10.5)]
stand += [Box((2, 9, 2), (14, 10.5, 14), P)]
stand += [Box((5, 10.5, 5), (11, 14, 11), "#pot"), Box((5.5, 13, 5.5), (10.5, 15, 10.5), "#soil")]
stand += pot_rim(4.5, 11.5, 14, 15.5, 4.5, 11.5, 1, "#pot")
write(ROOM, "plant_stand", stand, {"pot": "minecraft:block/terracotta", "soil": SOIL})

# Growing pot: a wide pot with a darker band and a thick rim, full of soil.
pot = [Box((3, 0, 3), (13, 8, 13), "#pot"), Box((2.75, 2, 2.75), (13.25, 3, 13.25), "#band"),
       Box((3.5, 7, 3.5), (12.5, 9.5, 12.5), "#soil")]
pot += pot_rim(2, 14, 8, 10, 2, 14, 1.5, "#pot")
write(ROOM, "growing_pot", pot, {"pot": "minecraft:block/terracotta", "band": "minecraft:block/brown_terracotta",
                                 "soil": SOIL}, particle="#pot")

# Hanging planter: a pot held by four cords that meet at a hook on the ceiling.
hanging = [Box((4, 2, 4), (12, 7.5, 12), "#pot"), Box((4.5, 7, 4.5), (11.5, 8.5, 11.5), "#soil"),
           Box((5, 1, 5), (11, 2, 11), "#pot")]
hanging += pot_rim(3.5, 12.5, 7.5, 9, 3.5, 12.5, 1, "#pot")
hanging += [Box((7.75, 9, 3.5), (8.25, 15.5, 4), "#cord"), Box((7.75, 9, 12), (8.25, 15.5, 12.5), "#cord"),
            Box((3.5, 9, 7.75), (4, 15.5, 8.25), "#cord"), Box((12, 9, 7.75), (12.5, 15.5, 8.25), "#cord"),
            Box((3.5, 15.5, 7.75), (12.5, 16, 8.25), "#cord"), Box((7.75, 15.5, 3.5), (8.25, 16, 12.5), "#cord"),
            Box((7, 15, 7), (9, 16, 9), "#hook")]
write(ROOM, "hanging_planter", hanging, {"pot": "minecraft:block/terracotta", "soil": SOIL,
                                         "cord": "minecraft:block/white_wool", "hook": "minecraft:block/iron_block"},
      particle="#pot")

# Window flower box: a wooden trough against the wall with soil for three flowers, on two brackets.
box = [Box((0, 1, 9), (16, 2, 16), P),
       Box((0, 2, 9), (16, 6, 10), P), Box((0, 2, 15), (16, 6, 16), P),
       Box((0, 2, 10), (1, 6, 15), P), Box((15, 2, 10), (16, 6, 15), P),
       Box((1, 2, 10), (15, 5, 15), "#soil"),
       Box((0, 6, 8.75), (16, 6.5, 10), S)]
box += [Box((x, 0, 11), (x + 1.5, 1, 16), L) for x in (2, 12.5)]
write(ROOM, "window_flower_box", box, {"soil": SOIL})


# Garden shed cabinet: a tall plank cabinet with a sloping cap, two doors and, inside, hanging garden tools.
def shed(opened):
    lower = [Box((0, 0, 0.5), (16, 1.5, 16), L),
             Box((0.5, 1.5, 1), (15.5, 16, 16), P, skip=("up",))]
    lower += door(1, 7.9, 1.5, 16, 0.5, 1, S, opened, "left") + door(8.1, 15, 1.5, 16, 0.5, 1, S, opened, "right")
    upper = [Box((0.5, 0, 1), (15.5, 13, 16), P, skip=("down",)),
             Box((0, 13, 0), (16, 14.5, 16), L), Box((1, 14.5, 1), (15, 15, 15), L)]
    upper += door(1, 7.9, 0, 12, 0.5, 1, S, opened, "left") + door(8.1, 15, 0, 12, 0.5, 1, S, opened, "right")
    upper += door_handle(6.5, 7.5, 0.5, 4.5, 0, 0.5, 1, 7.9, 0.5, L, opened, "left")
    upper += door_handle(8.5, 9.5, 0.5, 4.5, 0, 0.5, 8.1, 15, 0.5, L, opened, "right")
    if not opened:
        # Cross battens on the closed doors.
        for y in (4, 11):
            lower += [Box((1.5, y, 0.25), (7.4, y + 1, 0.5), L), Box((8.6, y, 0.25), (14.5, y + 1, 0.5), L)]
        upper += [Box((1.5, 7, 0.25), (7.4, 8, 0.5), L), Box((8.6, 7, 0.25), (14.5, 8, 0.5), L)]
    else:
        lower.append(Box((1, 1.5, 0.9), (15, 16, 1), S, skip=("up",)))
        upper.append(Box((1, 0, 0.9), (15, 12, 1), S, skip=("down",)))
        upper.append(Box((1, 6, 0.4), (15, 6.75, 0.9), L))  # a shelf of seed packets
        upper += [Box((x, 6.75, 0.5), (x + 2, 9.5, 0.9), tex) for x, tex in
                  ((2, "#seeds_a"), (4.5, "#seeds_b"), (7, "#seeds_a"), (9.5, "#seeds_b"), (12, "#seeds_a"))]
        # A rake and a spade on hooks.
        lower += [Box((3.5, 3, 0.5), (4.25, 16, 0.9), L), Box((2, 3, 0.4), (5.75, 4, 0.9), "#iron")]
        lower += [Box((x, 2, 0.4), (x + 0.4, 3, 0.9), "#iron") for x in (2.2, 3.1, 4.4, 5.3)]
        lower += [Box((11.5, 5, 0.5), (12.25, 16, 0.9), L), Box((10.5, 2, 0.4), (13.25, 5.5, 0.9), "#iron")]
    for b in lower:
        if b.t[1] == 16 and "up" not in b.skip and b.f[1] > 0:
            b.skip.add("up")
    for b in upper:
        if b.f[1] == 0 and "down" not in b.skip:
            b.skip.add("down")
    return lower, upper


SHED = {"iron": "minecraft:block/iron_block", "seeds_a": "minecraft:block/lime_terracotta",
        "seeds_b": "minecraft:block/yellow_terracotta"}
lo, up = shed(False)
write_tall(ROOM, "garden_shed_cabinet", lo, up, SHED)
lo, up = shed(True)
write(ROOM, "garden_shed_cabinet_lower_open", lo, SHED)
write(ROOM, "garden_shed_cabinet_upper_open", up, SHED)
