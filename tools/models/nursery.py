"""Model templates for the nursery and hobby room section."""
from modelkit import *  # noqa: F401,F403

ROOM = "nursery"
P, L, S = "#planks", "#log", "#stripped_log"

# Crib: four posts, bars on every side and a mattress.
crib = [Box((x, 0, z), (x + 1.5, 13, z + 1.5), L) for x in (0.5, 14) for z in (0.5, 14)]
crib += [Box((2, 3, 2), (14, 4, 14), P), Box((2, 4, 2), (14, 6, 14), "#mattress")]
crib += [Box((2, 12, 0.75), (14, 13, 1.75), S), Box((2, 12, 14.25), (14, 13, 15.25), S),
         Box((0.75, 12, 2), (1.75, 13, 14), S), Box((14.25, 12, 2), (15.25, 13, 14), S)]
crib += [Box((x, 4, 1), (x + 0.75, 12, 1.5), S) for x in range(3, 14, 2)]
crib += [Box((x, 4, 14.5), (x + 0.75, 12, 15), S) for x in range(3, 14, 2)]
crib += [Box((1, 4, z), (1.5, 12, z + 0.75), S) for z in range(3, 14, 2)]
crib += [Box((14.5, 4, z), (15, 12, z + 0.75), S) for z in range(3, 14, 2)]
write(ROOM, "crib", crib, {"mattress": "reikos_furniture:block/fabric/light_blue"})


# Changing table: a cabinet with a padded top between low rails, and a drawer.
def changing_table(opened):
    parts = [Box((0.5, 0, 1.5), (15.5, 12, 15.5), P),
             Box((0.5, 12, 1.5), (15.5, 13, 15.5), S),
             Box((1.5, 13, 2.5), (14.5, 14, 14.5), "#pad"),
             Box((0.5, 13, 14.5), (15.5, 16, 15.5), S), Box((0.5, 13, 1.5), (1.5, 16, 14.5), S),
             Box((14.5, 13, 1.5), (15.5, 16, 14.5), S)]
    parts += drawer(1.5, 14.5, 7, 11, 1, 1.5, S, P, opened, 5)
    parts += drawer(1.5, 14.5, 1.5, 6, 1, 1.5, S, P, False)
    return parts

PAD = {"pad": "reikos_furniture:block/fabric/white"}
write(ROOM, "changing_table", changing_table(False), PAD)
write(ROOM, "changing_table_open", changing_table(True), PAD)


# Baby mobile: hangs from the ceiling; its arms turn by 0, 22.5, 45 or -22.5 degrees (plus quarter turns).
def mobile(angle):
    turn = {"origin": [8, 10, 8], "axis": "y", "angle": angle} if angle else None
    arms = [Box((2.5, 10.5, 7.75), (13.5, 11, 8.25), "#metal", rotation=turn),
            Box((7.75, 10.5, 2.5), (8.25, 11, 13.5), "#metal", rotation=turn)]
    for x, z in ((3, 8), (13, 8), (8, 3), (8, 13)):
        arms.append(Box((x - 0.1, 8.5, z - 0.1), (x + 0.1, 10.5, z + 0.1), "#string", rotation=turn))
        arms.append(Box((x - 1, 6.5, z - 1), (x + 1, 8.5, z + 1), "#wool", rotation=turn))
    return [Box((7, 15, 7), (9, 16, 9), "#metal"), Box((7.9, 11, 7.9), (8.1, 15, 8.1), "#string")] + arms

MOBILE = {"metal": "minecraft:block/white_concrete", "string": "minecraft:block/white_wool"}
for name, angle in (("0", 0), ("22", 22.5), ("45", 45), ("m22", -22.5)):
    write(ROOM, "baby_mobile_" + name, mobile(angle), MOBILE, particle="#wool")

# Aquarium: a glass tank with water and sand on a dark wooden stand; the fish are drawn by a renderer.
write(ROOM, "aquarium", [
    Box((0, 0, 0.5), (16, 1, 15.5), "#frame"),
    Box((1, 1, 1.5), (15, 2, 14.5), "#sand"),
    Box((3, 2, 10), (4, 9, 11), "#plant"), Box((12, 2, 11), (13, 6, 12), "#plant"),
    Box((1, 2, 1.5), (15, 11.5, 14.5), "#water", skip=("down",)),
    Box((0.5, 1, 1), (15.5, 13, 15), "#glass"),
    Box((0, 13, 0.5), (16, 14, 15.5), "#frame"),
], {"frame": "minecraft:block/dark_oak_planks", "sand": "minecraft:block/sand", "plant": "minecraft:block/kelp_plant",
    "water": "reikos_furniture:block/aquarium_water", "glass": "reikos_furniture:block/window_glass"},
    particle="minecraft:block/glass", render_type="minecraft:translucent")

# Bird cage: a gold cage with a perch on a round base; a parrot sits inside (drawn by a renderer).
cage = [Box((4, 0, 4), (12, 1, 12), "#gold"), Box((5, 10, 5), (11, 11, 11), "#gold"), Box((6, 11, 6), (10, 12, 10), "#gold"),
        Box((7.5, 12, 7.5), (8.5, 13.5, 8.5), "#gold"), Box((5, 4, 7.75), (11, 4.5, 8.25), "#perch")]
cage += [Box((x, 1, z), (x + 0.5, 10, z + 0.5), "#gold") for x in (4.5, 6.5, 8.5, 11) for z in (4.5, 11)]
cage += [Box((x, 1, z), (x + 0.5, 10, z + 0.5), "#gold") for x in (4.5, 11) for z in (6.5, 8.5)]
write(ROOM, "bird_cage", cage, {"gold": "minecraft:block/gold_block", "perch": "minecraft:block/stripped_oak_log"},
      particle="minecraft:block/gold_block")
