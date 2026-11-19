"""Model templates for the art studio section. Paintings are drawn by a renderer."""
from modelkit import *  # noqa: F401,F403

ROOM = "studio"
P, L, S = "#planks", "#log", "#stripped_log"

# Easel: two front legs and a mast, a back leg leaning out, a ledge for the canvas and a top clamp.
# The canvas stands on the ledge and leans back against the mast (drawn by the display renderer).
easel = [Box((3, 0, 8), (4.5, 15, 9.5), L), Box((11.5, 0, 8), (13, 15, 9.5), L),
         Box((7.25, 3, 8.5), (8.75, 16, 10), L),
         Box((7.25, 0, 12), (8.75, 13, 13.5), L, rotation={"origin": [8, 13, 12.75], "axis": "x", "angle": 22.5}),
         Box((2, 4, 4.5), (14, 4.75, 8), S), Box((2, 4.75, 4.5), (14, 5.5, 5), S),
         Box((3, 13, 8.5), (13, 14, 9.5), S), Box((6.5, 15, 8), (9.5, 16, 9.5), S)]
write(ROOM, "easel", easel)

# Canvas on a wall: linen stretched over a thin wooden frame (the painting is drawn on top).
write(ROOM, "canvas", [
    Box((0, 0, 15), (16, 16, 16), "#canvas"),
    Box((0, 0, 14.75), (16, 1, 15), "#frame"), Box((0, 15, 14.75), (16, 16, 15), "#frame"),
    Box((0, 1, 14.75), (1, 15, 15), "#frame"), Box((15, 1, 14.75), (16, 15, 15), "#frame"),
], {"canvas": "reikos_furniture:block/canvas", "frame": "minecraft:block/birch_planks"}, particle="#canvas")


# Pottery wheel: a wooden bench with a splash pan and a stone wheel; a lump of clay, or a pot taking shape.
def pottery_wheel(running):
    parts = [Box((1, 0, 1), (15, 6, 15), "#wood"),
             Box((2, 6, 2), (14, 8, 14), "#pan"),
             Box((3, 8, 3), (13, 8.5, 13), "#pan"),
             Box((4, 8, 4), (12, 9, 12), "#wheel")]
    if running:
        parts += [Box((5.5, 9, 5.5), (10.5, 12, 10.5), "#clay"), Box((6, 12, 6), (10, 13.5, 10), "#clay"),
                  Box((6.5, 13.5, 6.5), (9.5, 14, 9.5), "#clay")]
    else:
        parts += [Box((6, 9, 6), (10, 11, 10), "#clay")]
    return parts


WHEEL = {"wood": "minecraft:block/spruce_planks", "pan": "minecraft:block/terracotta", "wheel": "minecraft:block/smooth_stone",
         "clay": "minecraft:block/clay"}
write(ROOM, "pottery_wheel", pottery_wheel(False), WHEEL, particle="minecraft:block/spruce_planks")
write(ROOM, "pottery_wheel_on", pottery_wheel(True), WHEEL, particle="minecraft:block/spruce_planks")

# Vase: round body, narrow neck and a lip.
write(ROOM, "vase", [
    Box((5.5, 0, 5.5), (10.5, 1, 10.5), "#pot"),
    Box((5, 1, 5), (11, 6, 11), "#pot"),
    Box((5.5, 6, 5.5), (10.5, 7, 10.5), "#pot"),
    Box((6.5, 7, 6.5), (9.5, 9, 9.5), "#pot"),
    Box((6, 9, 6), (10, 10, 10), "#pot"),
    Box((7, 8.5, 7), (9, 9.75, 9), "#soil"),
], {"pot": "minecraft:block/terracotta", "soil": "minecraft:block/rooted_dirt"}, particle="#pot")


# Sewing machine: a white body with an arm over the needle plate, a hand wheel and a spool of thread.
def sewing_machine(running):
    needle_y = 5 if running else 6
    return [Box((2, 0, 4), (14, 3, 12), "#body"),
            Box((10, 3, 5), (13.5, 10, 11), "#body"),
            Box((3, 8, 5.5), (10, 10.5, 10.5), "#body"),
            Box((3, 6.5, 6.5), (5, 8, 9.5), "#body"),
            Box((3.75, needle_y, 7.75), (4.25, 6.5, 8.25), "#metal"),
            Box((13.5, 5, 6.5), (14.5, 9, 10.5), "#metal"),
            Box((11, 10.5, 7), (12.5, 12.5, 8.5), "#spool"),
            Box((2.5, 3, 5), (9, 3.25, 11), "#fabric"),
            Box((2, 2.75, 3.5), (14, 3, 4), "#metal")]


SEWING = {"body": "minecraft:block/white_concrete", "metal": "reikos_furniture:block/appliance/steel",
          "spool": "minecraft:block/red_wool", "fabric": "reikos_furniture:block/fabric/light_blue"}
write(ROOM, "sewing_machine", sewing_machine(False), SEWING, particle="minecraft:block/white_concrete")
write(ROOM, "sewing_machine_on", sewing_machine(True), SEWING, particle="minecraft:block/white_concrete")

# Floor cushion: a thick square cushion with a button in the middle and piping round the edge.
write(ROOM, "floor_cushion", [
    Box((1, 0, 1), (15, 3.5, 15), "#wool"),
    Box((1.5, 3.5, 1.5), (14.5, 4, 14.5), "#wool"),
    Box((7.25, 4, 7.25), (8.75, 4.25, 8.75), "#button"),
], {"button": "minecraft:block/black_wool"}, particle="#wool")

# Paint pots: three open tins of paint and a brush laid across one of them.
pots = []
for x, z, h in ((2.5, 2.5, 6), (8.5, 3.5, 5), (4, 9, 4.5)):
    pots += [Box((x, 0, z), (x + 5, h, z + 5), "#tin", skip=("up",)),
             Box((x + 0.5, h - 0.5, z + 0.5), (x + 4.5, h - 0.25, z + 4.5), "#paint"),
             Box((x, h - 0.25, z), (x + 5, h, z + 0.5), "#tin"), Box((x, h - 0.25, z + 4.5), (x + 5, h, z + 5), "#tin"),
             Box((x, h - 0.25, z + 0.5), (x + 0.5, h, z + 4.5), "#tin"), Box((x + 4.5, h - 0.25, z + 0.5), (x + 5, h, z + 4.5), "#tin")]
pots += [Box((1.5, 6, 4.75), (9, 6.75, 5.5), "#handle"), Box((9, 5.75, 4.5), (11, 6.75, 5.75), "#paint")]
pots += [Box((x, 0, 11), (x + 1, 0.5, 13), "#paint") for x in (10, 12)]
write(ROOM, "paint_pots", pots, {"tin": "minecraft:block/iron_block", "handle": "minecraft:block/stripped_oak_log",
                                 "paint": "minecraft:block/white_concrete"}, particle="#paint")
