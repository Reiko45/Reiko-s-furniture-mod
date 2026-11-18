"""Model templates for the home bar section."""
from modelkit import *  # noqa: F401,F403

ROOM = "bar"
P, L, S = "#planks", "#log", "#stripped_log"
BRASS = {"brass": "minecraft:block/gold_block", "metal": "minecraft:block/iron_block", "black": "minecraft:block/black_concrete"}


# Bar counter: slatted front, a thick top that overhangs the customer side, a brass foot rail.
# Corners work like kitchen counters (straight, inner, outer left/right); storage is behind the bar.
def bar_front(extra_side=None):
    parts = [Box((0, 0, 2), (16, 14, 16), P)]
    parts += [Box((x, 1, 1.5), (x + 1.5, 14, 2), S) for x in (1, 4.5, 8, 11.5)]
    parts += [Box((0, 2, -0.75), (16, 3, 0.25), "#brass"), Box((1.5, 0, -0.25), (2.5, 2, 1.5), "#brass"),
              Box((13.5, 0, -0.25), (14.5, 2, 1.5), "#brass")]
    if extra_side == "left":
        parts += [Box((1.5, 1, z), (2, 14, z + 1.5), S) for z in (4, 7.5, 11)]
    if extra_side == "right":
        parts += [Box((14, 1, z), (14.5, 14, z + 1.5), S) for z in (4, 7.5, 11)]
    return parts


TOP = [Box((0, 14, -1), (16, 16, 16), P)]
write(ROOM, "bar_counter", bar_front() + TOP, BRASS)
write(ROOM, "bar_counter_inner", [Box((0, 0, 2), (16, 14, 16), P)] + TOP, BRASS)
write(ROOM, "bar_counter_outer_left", [Box((2, 0, 2), (16, 14, 16), P)] + bar_front("left")[1:] + [Box((-1, 14, -1), (16, 16, 16), P)], BRASS)
write(ROOM, "bar_counter_outer_right", [Box((0, 0, 2), (14, 14, 16), P)] + bar_front("right")[1:] + [Box((0, 14, -1), (17, 16, 16), P)], BRASS)

# Bar stool: a padded round-ish seat on a chrome post with a foot ring.
write(ROOM, "bar_stool", [
    Box((4.5, 0, 4.5), (11.5, 0.75, 11.5), "#metal"),
    Box((7.25, 0.75, 7.25), (8.75, 11.5, 8.75), "#metal"),
    Box((5, 5, 5), (11, 5.5, 5.75), "#metal"), Box((5, 5, 10.25), (11, 5.5, 11), "#metal"),
    Box((5, 5, 5.75), (5.75, 5.5, 10.25), "#metal"), Box((10.25, 5, 5.75), (11, 5.5, 10.25), "#metal"),
    Box((4, 11.5, 5), (12, 13.5, 11), "#wool"), Box((5, 11.5, 4), (11, 13.5, 12), "#wool"),
], BRASS, particle="#wool")

# Wine rack: nine cubbies; the bottles are drawn by the display renderer.
rack = [Box((0, 0, 1), (16, 1, 16), P), Box((0, 15, 1), (16, 16, 16), P),
        Box((0, 1, 1), (1, 15, 16), P), Box((15, 1, 1), (16, 15, 16), P),
        Box((1, 1, 15), (15, 15, 16), S),
        Box((5.5, 1, 2), (6, 15, 15), P), Box((10, 1, 2), (10.5, 15, 15), P),
        Box((1, 5.5, 2), (15, 6, 15), P), Box((1, 10, 2), (15, 10.5, 15), P)]
write(ROOM, "wine_rack", rack)


# Espresso machine: steel body, group head with a portafilter, steam wand and a gauge.
def espresso(on):
    parts = [
        Box((2, 0, 4), (14, 10, 14), "#steel"),
        Box((2, 10, 4), (14, 10.5, 14), "#metal"),
        Box((3, 0, 1.5), (13, 1, 4), "#metal"),
        Box((6, 6, 2.5), (10, 8, 4), "#metal"), Box((7.5, 6.5, 0), (8.5, 7.5, 2.5), "#black"),
        Box((12, 3.5, 2.5), (12.5, 8, 3), "#metal"),
        Box((4.5, 8.25, 3.9), (6.5, 9.75, 4), "#gauge", glow=on),
        Box((3, 10.5, 6), (5, 12, 8), "#cup"), Box((5.5, 10.5, 6), (7.5, 12, 8), "#cup"),
    ]
    if on:
        parts += [Box((7, 1, 2.5), (9, 3, 4), "#cup"), Box((7.9, 3, 3.1), (8.1, 6, 3.3), "#coffee")]
    return parts

ESPRESSO = {**BRASS, "steel": "reikos_furniture:block/appliance/steel", "cup": "minecraft:block/white_concrete",
            "gauge": "minecraft:block/white_concrete", "coffee": "minecraft:block/brown_concrete"}
write(ROOM, "espresso_machine", espresso(False), ESPRESSO, particle="reikos_furniture:block/appliance/steel")
write(ROOM, "espresso_machine_on", espresso(True), ESPRESSO, particle="reikos_furniture:block/appliance/steel")
