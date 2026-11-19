"""Model templates for the home theater section."""
from modelkit import *  # noqa: F401,F403

ROOM = "theater"
P, L, S = "#planks", "#log", "#stripped_log"

# ---- Projector screen: three blocks wide and two tall --------------------------------------------
# Each part is a block of its own. The film texture is 48x32 pixels; each part's surface shows its
# third of the width and half of the height. Column 0 is on the audience's left, which is the +x side
# of a north-facing template (the audience looks at the north face).
SCREEN_TEX = {"back": "minecraft:block/black_concrete", "frame": "minecraft:block/gray_concrete"}
PARTS = {"bottom_left": (0, 0), "bottom_middle": (1, 0), "bottom_right": (2, 0),
         "top_left": (0, 1), "top_middle": (1, 1), "top_right": (2, 1)}


def screen_part(column, row, lit):
    u1, u2 = column * 16 / 3, (column + 1) * 16 / 3
    v1, v2 = (0, 8) if row == 1 else (8, 16)
    parts = [Box((0, 0, 15.5), (16, 16, 16), "#back"),
             Box((0, 0, 15), (16, 16, 15.5), "#back", faces={"north": "#screen"}, uvs={"north": [u1, v1, u2, v2]},
                 glow=lit)]
    if column == 0:
        parts.append(Box((15.25, 0, 14.5), (16, 16, 15), "#frame"))
    if column == 2:
        parts.append(Box((0, 0, 14.5), (0.75, 16, 15), "#frame"))
    if row == 1:
        # The roller housing along the top, with hooks for the wall.
        parts.append(Box((0, 14.5, 13.5), (16, 16, 16), "#frame"))
        parts += [Box((x, 15.25, 13), (x + 1, 16, 13.5), "#back") for x in (3, 12)]
    else:
        # The weighted bar along the bottom.
        parts.append(Box((0, 0, 14.25), (16, 1, 15), "#frame"))
    return parts


for name, (column, row) in PARTS.items():
    write(ROOM, "projector_screen_" + name, screen_part(column, row, False),
          {**SCREEN_TEX, "screen": "minecraft:block/white_concrete"}, particle="minecraft:block/white_concrete")
    for film in (1, 2, 3):
        write(ROOM, f"projector_screen_{name}_film_{film}", screen_part(column, row, True),
              {**SCREEN_TEX, "screen": f"reikos_furniture:block/screen/film_{film}"},
              particle="minecraft:block/white_concrete")

# The item: the whole screen in miniature.
write(ROOM, "projector_screen_item", [
    Box((1, 3, 7.5), (15, 12.5, 8.5), "#back", faces={"north": "#screen", "south": "#screen"}),
    Box((0.5, 12.5, 7), (15.5, 14, 9), "#frame"),
    Box((1, 2.5, 7.25), (15, 3, 8.75), "#frame"),
], {**SCREEN_TEX, "screen": "minecraft:block/white_concrete"}, particle="minecraft:block/white_concrete")


# ---- Projector: the lens points to the back (+z), away from whoever placed it ---------------------
def projector(on):
    parts = [Box((x, 0, z), (x + 1.5, 1, z + 1.5), "#dark") for x in (3.5, 11) for z in (3.5, 11)]
    parts += [Box((3, 1, 3), (13, 8, 13), "#body"),
              Box((3.5, 8, 4), (12.5, 8.5, 12), "#dark"),
              Box((7.25, 8, 2.5), (8.75, 13.5, 8), "#reel"), Box((7.25, 8, 8.5), (8.75, 13.5, 14), "#reel"),
              Box((7, 10, 4.5), (9, 11.5, 6), "#dark"), Box((7, 10, 10.5), (9, 11.5, 12), "#dark"),
              Box((6.5, 2.5, 13), (9.5, 5.5, 15.5), "#dark"),
              Box((7, 3, 15.5), (9, 5, 15.75), "#lens", glow=on),
              Box((3, 3, 2.5), (5, 5, 3), "#dark"), Box((11, 3, 2.5), (13, 5, 3), "#dark")]
    return parts


PROJECTOR = {"body": "reikos_furniture:block/appliance/steel", "dark": "minecraft:block/gray_concrete",
             "reel": "minecraft:block/black_concrete"}
write(ROOM, "projector", projector(False), {**PROJECTOR, "lens": "minecraft:block/light_blue_concrete"},
      particle="reikos_furniture:block/appliance/steel")
write(ROOM, "projector_on", projector(True), {**PROJECTOR, "lens": "minecraft:block/white_concrete"},
      particle="reikos_furniture:block/appliance/steel")


# ---- Recliner: upright, or tilted back with the footrest out ---------------------------------------
def recliner(reclined):
    W = "#wool"
    parts = [Box((1, 0, 1.5), (15, 3, 15.5), "#base"),
             Box((2.5, 3, 1), (13.5, 8, 12.5), W),
             Box((0, 3, 1), (2.5, 11, 15.5), W), Box((13.5, 3, 1), (16, 11, 15.5), W),
             Box((0, 11, 1.5), (2.5, 11.5, 15), W), Box((13.5, 11, 1.5), (16, 11.5, 15), W)]
    if reclined:
        tilt = {"origin": [8, 8, 14], "axis": "x", "angle": 22.5}
        parts += [Box((2.5, 8, 12.5), (13.5, 19, 16), W, rotation=tilt),
                  Box((4, 15.5, 11.5), (12, 18.5, 12.5), W, rotation=tilt)]
        parts += [Box((2.5, 6.5, -5), (13.5, 8, 1), W), Box((3, 3, 0), (4, 6.5, 1), "#base"),
                  Box((12, 3, 0), (13, 6.5, 1), "#base")]
    else:
        parts += [Box((2.5, 3, 12.5), (13.5, 16, 16), W), Box((4, 12, 11.5), (12, 15, 12.5), W),
                  Box((2.5, 1, 0.5), (13.5, 7, 1), W)]
    return parts


RECLINER = {"base": "minecraft:block/dark_oak_planks"}
write(ROOM, "recliner", recliner(False), RECLINER, particle="#wool")
write(ROOM, "recliner_reclined", recliner(True), RECLINER, particle="#wool")

# ---- Speaker: a wooden cabinet with a woofer and a tweeter ----------------------------------------
write(ROOM, "speaker", [
    Box((3, 0, 4), (13, 15.5, 12), P),
    Box((2.75, 15.5, 3.75), (13.25, 16, 12.25), S),
    Box((3.5, 0.5, 3.75), (12.5, 15, 4), "#grille"),
    Box((4.5, 2, 3.5), (11.5, 9, 3.75), "#cone"), Box((7, 4.5, 3.25), (9, 6.5, 3.5), "#cap"),
    Box((6.25, 10.5, 3.5), (9.75, 14, 3.75), "#cone"), Box((7.5, 11.75, 3.25), (8.5, 12.75, 3.5), "#cap"),
], {"grille": "minecraft:block/black_concrete", "cone": "minecraft:block/gray_concrete",
    "cap": "minecraft:block/light_gray_concrete"})


# ---- Popcorn machine: a red cart with a glass case full of popcorn ---------------------------------
def popcorn_machine(running):
    level = 10.5 if running else 8
    return [Box((3, 0, 3), (13, 6, 13), "#red"),
            Box((2.75, 5.5, 2.75), (13.25, 6.5, 13.25), "#gold"),
            Box((3.5, 6.5, 3.5), (12.5, level, 12.5), "#popcorn"),
            Box((7.5, 12, 7.5), (8.5, 14, 8.5), "#steel"),
            Box((6, 10, 6), (10, 12, 10), "#steel"),
            Box((3.25, 6.5, 3.25), (12.75, 14, 12.75), "#glass"),
            Box((3, 14, 3), (13, 15.5, 13), "#red"),
            Box((2.75, 14, 2.75), (13.25, 14.5, 13.25), "#gold"),
            Box((7, 15.5, 7), (9, 16, 9), "#gold")]


POPCORN = {"red": "minecraft:block/red_concrete", "gold": "minecraft:block/gold_block",
           "popcorn": "reikos_furniture:block/popcorn", "steel": "reikos_furniture:block/appliance/steel",
           "glass": "reikos_furniture:block/window_glass"}
write(ROOM, "popcorn_machine", popcorn_machine(False), POPCORN, particle="minecraft:block/red_concrete",
      render_type="minecraft:translucent")
write(ROOM, "popcorn_machine_on", popcorn_machine(True), POPCORN, particle="minecraft:block/red_concrete",
      render_type="minecraft:translucent")

# ---- Movie poster: a framed poster on the wall; four designs -------------------------------------
for design in range(4):
    write(ROOM, f"movie_poster_{design}", [
        Box((2, 0, 15.25), (14, 16, 16), "#frame"),
        Box((2.5, 0.5, 15), (13.5, 15.5, 15.25), "#frame", faces={"north": "#poster"}),
    ], {"frame": "minecraft:block/black_concrete", "poster": f"reikos_furniture:block/poster/movie_{design + 1}"},
        particle="#poster")
