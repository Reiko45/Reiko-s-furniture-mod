"""Builds the furniture maker's workshop for each village type as a structure file (.nbt).

Usage: python3 tools/structures/generate_workshops.py
Writes src/main/resources/data/reikos_furniture/structure/village/<type>/furniture_workshop.nbt.
The mod adds these to the villages' house pools at server start (see villager/VillageWorkshops.java).

Every workshop has the same layout in its village's materials: a 7x7 house with the door on the south
side, a furniture workbench (the furniture maker's job site), a workshop bench, a pegboard and toolbox,
a dining set in the village's wood, an armchair in a furniture style, a lit floor lamp, a rug and a
bed. Like the vanilla houses it has two jigsaws: the entrance that joins the street, and one that
spawns a villager.
"""
import os

import nbt
from nbt import Int, List

HERE = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(HERE, "..", "..", "src", "main", "resources", "data", "reikos_furniture", "structure", "village")
DATA_VERSION = 3955  # Minecraft 1.21.1
M = "reikos_furniture:"

# Size: 7 wide (x), 7 tall (y), 8 deep (z: the house is z 0..6, z 7 is the doorstep in front of the door).
SX, SY, SZ = 7, 7, 8

VILLAGES = {
    "plains": dict(foundation="minecraft:cobblestone", floor="minecraft:oak_planks", wall="minecraft:oak_planks",
                   corner="minecraft:oak_log", beam="minecraft:oak_log", roof="minecraft:oak_slab",
                   door="minecraft:oak_door", step="minecraft:cobblestone", wood="oak", style="rustic",
                   colors=("red", "brown")),
    "desert": dict(foundation="minecraft:sandstone", floor="minecraft:smooth_sandstone", wall="minecraft:smooth_sandstone",
                   corner="minecraft:cut_sandstone", beam="minecraft:cut_sandstone", roof="minecraft:smooth_sandstone_slab",
                   door="minecraft:jungle_door", step="minecraft:sandstone", wood="jungle", style="modern",
                   colors=("cyan", "orange")),
    "savanna": dict(foundation="minecraft:orange_terracotta", floor="minecraft:acacia_planks", wall="minecraft:acacia_planks",
                    corner="minecraft:acacia_log", beam="minecraft:acacia_log", roof="minecraft:acacia_slab",
                    door="minecraft:acacia_door", step="minecraft:orange_terracotta", wood="acacia", style="royal",
                    colors=("yellow", "red")),
    "snowy": dict(foundation="minecraft:stone_bricks", floor="minecraft:spruce_planks", wall="minecraft:white_terracotta",
                  corner="minecraft:stripped_spruce_log", beam="minecraft:stripped_spruce_log", roof="minecraft:snow_block",
                  door="minecraft:spruce_door", step="minecraft:stone_bricks", wood="spruce", style="rustic",
                  colors=("light_blue", "white")),
    "taiga": dict(foundation="minecraft:mossy_cobblestone", floor="minecraft:spruce_planks", wall="minecraft:spruce_planks",
                  corner="minecraft:spruce_log", beam="minecraft:spruce_log", roof="minecraft:spruce_slab",
                  door="minecraft:spruce_door", step="minecraft:cobblestone", wood="spruce", style="japanese",
                  colors=("green", "brown")),
}


class Structure:
    def __init__(self):
        self.palette = []
        self.index = {}
        self.blocks = {}

    def set(self, x, y, z, name, nbt_data=None, **props):
        key = (name, tuple(sorted(props.items())))
        if key not in self.index:
            entry = {"Name": name}
            if props:
                entry["Properties"] = {k: str(v).lower() for k, v in sorted(props.items())}
            self.index[key] = len(self.palette)
            self.palette.append(entry)
        self.blocks[(x, y, z)] = (self.index[key], nbt_data)

    def nbt(self):
        blocks = []
        for (x, y, z), (state, data) in sorted(self.blocks.items(), key=lambda item: (item[0][1], item[0][2], item[0][0])):
            block = {"pos": List([Int(x), Int(y), Int(z)]), "state": Int(state)}
            if data:
                block["nbt"] = data
            blocks.append(block)
        return {
            "size": List([Int(SX), Int(SY), Int(SZ)]),
            "entities": List([], nbt.COMPOUND),
            "blocks": List(blocks, nbt.COMPOUND),
            "palette": List(self.palette, nbt.COMPOUND),
            "DataVersion": Int(DATA_VERSION),
        }


def jigsaw(name, target, pool, final_state, joint):
    return {"id": "minecraft:jigsaw", "name": name, "target": target, "pool": pool,
            "final_state": final_state, "joint": joint,
            "placement_priority": Int(0), "selection_priority": Int(0)}


def workshop(village, v):
    s = Structure()
    wood, style = v["wood"], v["style"]
    main, accent = v["colors"]
    # Floor (y 0): foundation ring, wooden floor inside, doorstep in front of the door.
    for x in range(SX):
        for z in range(7):
            edge = x in (0, 6) or z in (0, 6)
            s.set(x, 0, z, v["foundation"] if edge else v["floor"])
    s.set(3, 0, 7, v["step"])
    # Walls (y 1-4) with corner pillars, air inside.
    for y in range(1, 5):
        for x in range(SX):
            for z in range(7):
                corner = x in (0, 6) and z in (0, 6)
                edge = x in (0, 6) or z in (0, 6)
                if corner:
                    s.set(x, y, z, v["corner"], axis="y") if "log" in v["corner"] else s.set(x, y, z, v["corner"])
                elif edge:
                    s.set(x, y, z, v["wall"])
                else:
                    s.set(x, y, z, "minecraft:air")
    # Roof (y 5): beams around the edge, planks inside; (y 6) a slab or snow layer on top.
    for x in range(SX):
        for z in range(7):
            edge = x in (0, 6) or z in (0, 6)
            if edge and "log" in v["beam"]:
                s.set(x, 5, z, v["beam"], axis="x" if z in (0, 6) else "z")
            elif edge:
                s.set(x, 5, z, v["beam"])
            else:
                s.set(x, 5, z, v["floor"])
    for x in range(1, 6):
        for z in range(1, 6):
            if v["roof"].endswith("_slab"):
                s.set(x, 6, z, v["roof"], type="bottom", waterlogged=False)
            else:
                s.set(x, 6, z, v["roof"])
    # Windows: two in front, one on each side and one at the back.
    for x in (1, 5):
        s.set(x, 2, 6, "minecraft:glass_pane", east=True, west=True)
    s.set(3, 2, 0, "minecraft:glass_pane", east=True, west=True)
    for z in (2, 4):
        s.set(0, 2, z, "minecraft:glass_pane", north=True, south=True)
        s.set(6, 2, z, "minecraft:glass_pane", north=True, south=True)
    # Door on the south side; the entrance jigsaw stands on the doorstep in front of it.
    for half in ("lower", "upper"):
        s.set(3, 1 if half == "lower" else 2, 6, v["door"], facing="south", half=half, hinge="left", open=False, powered=False)
    s.set(3, 1, 7, "minecraft:jigsaw", jigsaw("minecraft:building_entrance", "minecraft:building_entrance", "minecraft:empty",
                                              "minecraft:structure_void", "aligned"), orientation="south_up")
    # A villager spawns on the floor in the middle.
    s.set(3, 0, 3, "minecraft:jigsaw", jigsaw("minecraft:bottom", "minecraft:bottom", f"minecraft:village/{village}/villagers",
                                              v["floor"], "rollable"), orientation="up_north")

    # Furniture. Pieces against the back wall face south (into the room).
    s.set(1, 1, 1, M + "furniture_workbench", facing="south")
    s.set(2, 1, 1, M + f"{wood}_workshop_bench", facing="south")
    s.set(2, 2, 1, M + f"{wood}_pegboard", facing="south")
    s.set(3, 1, 1, M + f"{main}_toolbox", facing="south")
    s.set(5, 1, 1, M + f"{wood}_floor_lamp", facing="south", half="lower", lit=True)
    s.set(5, 2, 1, M + f"{wood}_floor_lamp", facing="south", half="upper", lit=True)
    s.set(1, 1, 3, M + f"{wood}_dining_chair", facing="east")
    s.set(2, 1, 3, M + f"{wood}_dining_table")
    s.set(2, 1, 4, M + f"{wood}_dining_chair", facing="north")
    s.set(1, 1, 5, M + f"{accent}_{style}_armchair", facing="east")
    s.set(3, 1, 4, M + f"{main}_rug")
    # The villager's bed along the east wall (a vanilla bed, so villagers can claim it as a home).
    s.set(5, 1, 4, "minecraft:red_bed", facing="north", part="head", occupied=False)
    s.set(5, 1, 5, "minecraft:red_bed", facing="north", part="foot", occupied=False)
    return s


if __name__ == "__main__":
    for village, v in VILLAGES.items():
        path = os.path.join(OUT, village, "furniture_workshop.nbt")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        with open(path, "wb") as fh:
            fh.write(nbt.dump(workshop(village, v).nbt()))
        print("wrote", os.path.normpath(path))
