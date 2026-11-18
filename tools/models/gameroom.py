"""Model templates for the game room section."""
from modelkit import *  # noqa: F401,F403

ROOM = "gameroom"
P, L, S = "#planks", "#log", "#stripped_log"

# Pool table: felt top, wooden rails on the outer sides (added per open side), thick legs.
write(ROOM, "pool_table_top", [
    Box((0, 12, 0), (16, 13, 16), P),
    Box((0, 13, 0), (16, 13.5, 16), "#felt"),
], {"felt": "reikos_furniture:block/fabric/green"})
write(ROOM, "pool_table_rail", [Box((0, 13, 0), (16, 15, 1.5), P)])
write(ROOM, "pool_table_leg", [Box((1, 0, 1), (4, 12, 4), L)])
write(ROOM, "pool_table_item", [
    Box((0, 12, 0), (16, 13, 16), P), Box((0, 13, 0), (16, 13.5, 16), "#felt"),
    Box((0, 13, 0), (16, 15, 1.5), P), Box((0, 13, 14.5), (16, 15, 16), P),
    Box((0, 13, 1.5), (1.5, 15, 14.5), P), Box((14.5, 13, 1.5), (16, 15, 14.5), P),
    Box((1, 0, 1), (4, 12, 4), L), Box((12, 0, 1), (15, 12, 4), L),
    Box((1, 0, 12), (4, 12, 15), L), Box((12, 0, 12), (15, 12, 15), L),
    Box((6, 13.5, 7), (7, 14.5, 8), "#ball1"), Box((9, 13.5, 9), (10, 14.5, 10), "#ball2"),
], {"felt": "reikos_furniture:block/fabric/green", "ball1": "minecraft:block/white_concrete",
    "ball2": "minecraft:block/red_concrete"})


# Arcade cabinet: two blocks tall with a glowing screen and controls.
def arcade(on):
    lower = [
        Box((1, 0, 3), (15, 16, 15), "#body", faces={"east": "#art", "west": "#art"}, skip=("up",)),
        Box((1, 12, 1), (15, 16, 3), "#body"),
        Box((3, 16, 1.5), (4.5, 17.5, 3), "#red"), Box((6, 16, 1.5), (7.5, 16.5, 3), "#red"),
        Box((9, 16, 1.5), (10.5, 16.5, 3), "#yellow"),
    ]
    upper = [
        Box((1, 0, 4), (15, 15, 15), "#body", faces={"east": "#art", "west": "#art"}, skip=("down",)),
        Box((2, 2, 3.75), (14, 12, 4), "#screen", glow=on, uvs={"north": [0, 0, 16, 16]}),
        Box((1, 12, 2), (15, 16, 4), "#marquee", glow=on),
    ]
    return lower, upper

ARCADE = {"body": "minecraft:block/black_concrete", "art": "minecraft:block/purple_concrete",
          "red": "minecraft:block/red_concrete", "yellow": "minecraft:block/yellow_concrete"}
lo, up = arcade(False)
write_tall(ROOM, "arcade_cabinet", lo, up, {**ARCADE, "screen": "minecraft:block/black_concrete_powder",
                                            "marquee": "minecraft:block/magenta_concrete"}, particle="minecraft:block/black_concrete")
lo, up = arcade(True)
write(ROOM, "arcade_cabinet_upper_on", up, {**ARCADE, "screen": "reikos_furniture:block/screen/arcade",
                                            "marquee": "minecraft:block/magenta_glazed_terracotta"}, particle="minecraft:block/black_concrete")

# Dartboard: hangs on the wall.
write(ROOM, "dartboard", [
    Box((2, 2, 15), (14, 14, 16), "#board", faces={"north": "#face"}, uvs={"north": [0, 0, 16, 16]}),
], {"board": "minecraft:block/black_concrete", "face": "reikos_furniture:block/dartboard"},
    particle="reikos_furniture:block/dartboard")

# Gaming chair: racing-style seat with a high back, on a star base.
write(ROOM, "gaming_chair", [
    Box((2, 0.75, 7.25), (14, 1.75, 8.75), "#black"), Box((7.25, 0.75, 2), (8.75, 1.75, 14), "#black"),
    Box((7.5, 1.75, 7.5), (8.5, 6, 8.5), "#metal"),
    Box((2.5, 6, 2), (13.5, 9, 13), "#black", faces={"up": "#wool"}),
    Box((2.5, 9, 12), (13.5, 16, 14), "#black", faces={"north": "#wool"}),
    Box((1.5, 6, 3), (2.5, 11, 12), "#wool"), Box((13.5, 6, 3), (14.5, 11, 12), "#wool"),
], {"black": "minecraft:block/black_concrete", "metal": "minecraft:block/iron_block"}, particle="#wool")
