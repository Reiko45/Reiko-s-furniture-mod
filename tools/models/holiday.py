"""Model templates for the holiday decorations section."""
from modelkit import *  # noqa: F401,F403

ROOM = "holiday"
CUTOUT = "minecraft:cutout"
TRANSLUCENT = "minecraft:translucent"


def grow(box, d):
    """A copy of a box made bigger by d on every side (for the light strings around the tree)."""
    return [box.f[0] - d, box.f[1] - d, box.f[2] - d], [box.t[0] + d, box.t[1] + d, box.t[2] + d]


# ---- Christmas tree: layers of branches getting smaller, lights wound round them, a star on top ----
def tree(lit):
    lower_layers = [Box((1, 3, 1), (15, 8, 15), "#leaves"), Box((2, 8, 2), (14, 13, 14), "#leaves"),
                    Box((3, 13, 3), (13, 16, 13), "#leaves")]
    upper_layers = [Box((4, 0, 4), (12, 4, 12), "#leaves"), Box((5, 4, 5), (11, 8, 11), "#leaves"),
                    Box((6, 8, 6), (10, 11, 10), "#leaves"), Box((7, 11, 7), (9, 13, 9), "#leaves")]
    lower = [Box((4.5, 0, 4.5), (11.5, 2, 11.5), "#stand"), Box((7, 2, 7), (9, 3, 9), "#trunk")] + lower_layers
    upper = list(upper_layers)
    for layers, out in ((lower_layers, lower), (upper_layers, upper)):
        for layer in layers:
            f, t = grow(layer, 0.15)
            out.append(Box(f, t, "#lights", glow=lit, skip=("down",)))
    upper += [Box((7.25, 13, 7.5), (8.75, 15.5, 8.5), "#star", glow=lit),
              Box((6.5, 13.75, 7.5), (9.5, 14.75, 8.5), "#star", glow=lit)]
    for b in lower:
        if b.t[1] >= 16:
            b.skip.add("up")
    for b in upper:
        if b.f[1] <= 0:
            b.skip.add("down")
    return lower, upper


TREE = {"leaves": "minecraft:block/azalea_leaves", "stand": "minecraft:block/red_concrete",
        "trunk": "minecraft:block/spruce_log", "star": "minecraft:block/gold_block"}
lo, up = tree(False)
write_tall(ROOM, "christmas_tree", lo, up, {**TREE, "lights": "reikos_furniture:block/holiday/tree_lights_off"},
           particle="minecraft:block/azalea_leaves", render_type=CUTOUT)
lo, up = tree(True)
write(ROOM, "christmas_tree_lower_on", lo, {**TREE, "lights": "reikos_furniture:block/holiday/tree_lights"},
      particle="minecraft:block/azalea_leaves", render_type=CUTOUT)
write(ROOM, "christmas_tree_upper_on", up, {**TREE, "lights": "reikos_furniture:block/holiday/tree_lights"},
      particle="minecraft:block/azalea_leaves", render_type=CUTOUT)


# ---- Present: a box wrapped in colored paper with a gold ribbon and bow; open, the lid is lifted -----
def present(opened):
    lid_y = 14 if opened else 10
    parts = [Box((2, 0, 2), (14, 10, 14), "#wool"),
             Box((7, 0, 1.9), (9, 10, 2), "#ribbon"), Box((7, 0, 14), (9, 10, 14.1), "#ribbon"),
             Box((1.9, 0, 7), (2, 10, 9), "#ribbon"), Box((14, 0, 7), (14.1, 10, 9), "#ribbon"),
             Box((1.5, lid_y, 1.5), (14.5, lid_y + 2, 14.5), "#wool"),
             Box((7, lid_y, 1.4), (9, lid_y + 2.1, 14.6), "#ribbon"), Box((1.4, lid_y, 7), (14.6, lid_y + 2.1, 9), "#ribbon"),
             Box((5, lid_y + 2, 7.25), (7.5, lid_y + 4, 8.75), "#ribbon"), Box((8.5, lid_y + 2, 7.25), (11, lid_y + 4, 8.75), "#ribbon"),
             Box((7.25, lid_y + 2, 7.25), (8.75, lid_y + 3, 8.75), "#ribbon")]
    if opened:
        parts.append(Box((3, 9.5, 3), (13, 10, 13), "#inside"))
    return parts


PRESENT = {"ribbon": "minecraft:block/gold_block", "inside": "minecraft:block/white_wool"}
write(ROOM, "present", present(False), PRESENT, particle="#wool")
write(ROOM, "present_open", present(True), PRESENT, particle="#wool")


# ---- String lights: a sagging wire with three bulbs; hooks where a strand ends ----------------------
def strand(lit, ceiling):
    if ceiling:
        y_hi, y_lo, z1, z2 = 15.5, 14.5, 7.75, 8.25
    else:
        y_hi, y_lo, z1, z2 = 13, 12, 15.25, 15.75
    parts = [Box((0, y_hi, z1), (5, y_hi + 0.5, z2), "#wire"), Box((5, y_lo, z1), (11, y_lo + 0.5, z2), "#wire"),
             Box((11, y_hi, z1), (16, y_hi + 0.5, z2), "#wire"),
             Box((4.75, y_lo, z1), (5.25, y_hi + 0.5, z2), "#wire"), Box((10.75, y_lo, z1), (11.25, y_hi + 0.5, z2), "#wire")]
    for x, bulb, y in ((2.5, "#b1", y_hi), (8, "#b2", y_lo), (13.5, "#b3", y_hi)):
        parts += [Box((x - 0.5, y - 0.75, z1 - 0.25), (x + 0.5, y, z2 + 0.25), "#wire"),
                  Box((x - 0.75, y - 2.75, z1 - 0.5), (x + 0.75, y - 0.75, z2 + 0.5), bulb, glow=lit)]
    return parts


def hook(east, ceiling):
    x = 15 if east else 0
    if ceiling:
        return [Box((x, 15, 7.5), (x + 1, 16, 8.5), "#wire")]
    return [Box((x, 12.5, 15), (x + 1, 14, 16), "#wire")]


LIGHTS = {"wire": "minecraft:block/green_concrete", "b1": "minecraft:block/light_gray_concrete",
          "b2": "minecraft:block/light_gray_concrete", "b3": "minecraft:block/light_gray_concrete"}
for kind, ceiling in (("wall", False), ("ceiling", True)):
    write(ROOM, f"string_lights_{kind}", strand(False, ceiling), LIGHTS, particle="#wire", cull=False)
    write(ROOM, f"string_lights_{kind}_on", strand(True, ceiling), LIGHTS, particle="#wire", cull=False)
    write(ROOM, f"string_lights_{kind}_hook_west", hook(False, ceiling), LIGHTS, particle="#wire", cull=False)
    write(ROOM, f"string_lights_{kind}_hook_east", hook(True, ceiling), LIGHTS, particle="#wire", cull=False)

# ---- Wreath: a ring of leaves with a red bow and berries ----------------------------------------
wreath = [Box((4, 12, 15), (12, 14, 16), "#leaves"), Box((4, 2, 15), (12, 4, 16), "#leaves"),
          Box((2, 4, 15), (4, 12, 16), "#leaves"), Box((12, 4, 15), (14, 12, 16), "#leaves"),
          Box((3, 3, 15), (5, 5, 16), "#leaves"), Box((11, 3, 15), (13, 5, 16), "#leaves"),
          Box((3, 11, 15), (5, 13, 16), "#leaves"), Box((11, 11, 15), (13, 13, 16), "#leaves"),
          Box((5.5, 1.5, 14.5), (7.5, 3.5, 15), "#bow"), Box((8.5, 1.5, 14.5), (10.5, 3.5, 15), "#bow"),
          Box((7.25, 1.75, 14.25), (8.75, 3.25, 15), "#bow"), Box((6.5, 0.5, 14.75), (7.25, 2, 15), "#bow"),
          Box((8.75, 0.5, 14.75), (9.5, 2, 15), "#bow")]
wreath += [Box((x, y, 14.75), (x + 1, y + 1, 15), "#berry") for x, y in ((3, 8), (12, 6), (6, 12.5), (10, 12), (12.5, 10))]
write(ROOM, "wreath", wreath, {"leaves": "minecraft:block/azalea_leaves", "bow": "minecraft:block/red_wool",
                               "berry": "minecraft:block/red_concrete"}, particle="#leaves", render_type=CUTOUT)

# ---- Stocking: a sock hanging from a hook, with a white cuff ---------------------------------------
write(ROOM, "stocking", [
    Box((5, 4, 15), (9, 11.5, 16), "#wool"), Box((5, 2, 15), (11, 5, 16), "#wool"),
    Box((9, 2.5, 15), (11.5, 4.5, 16), "#wool"),
    Box((4.5, 11.5, 14.75), (9.5, 14, 16), "#cuff"),
    Box((6.75, 14, 15.25), (7.25, 15, 16), "#hook"),
], {"cuff": "minecraft:block/white_wool", "hook": "minecraft:block/gold_block"}, particle="#wool")

# ---- Snow globe: a wooden base, a little snowy scene and a glass dome -----------------------------
write(ROOM, "snow_globe", [
    Box((4, 0, 4), (12, 1.5, 12), "#base"), Box((4.5, 1.5, 4.5), (11.5, 2, 11.5), "#base"),
    Box((5, 2, 5), (11, 2.75, 11), "#snow"),
    Box((6.5, 2.75, 6.5), (8.5, 4.5, 8.5), "#house"), Box((6.25, 4.5, 6.25), (8.75, 5.25, 8.75), "#snow"),
    Box((9, 2.75, 8.5), (10, 5.5, 9.5), "#tree"), Box((9.25, 5.5, 8.75), (9.75, 6.25, 9.25), "#tree"),
    Box((5, 2, 5), (11, 9, 11), "#glass"), Box((5.5, 9, 5.5), (10.5, 9.5, 10.5), "#glass"),
], {"base": "minecraft:block/dark_oak_planks", "snow": "minecraft:block/snow", "house": "minecraft:block/red_terracotta",
    "tree": "minecraft:block/green_concrete", "glass": "reikos_furniture:block/window_glass"},
    particle="minecraft:block/glass", render_type=TRANSLUCENT)

# ---- Halloween ------------------------------------------------------------------------------
write(ROOM, "gravestone", [
    Box((3, 0, 5.5), (13, 1.5, 10.5), "#stone"),
    Box((4, 1.5, 7), (12, 12, 9), "#stone"), Box((5, 12, 7), (11, 13.5, 9), "#stone"), Box((6.5, 13.5, 7), (9.5, 14, 9), "#stone"),
    Box((7.5, 6, 6.9), (8.5, 11, 7), "#carved"), Box((6, 9, 6.9), (10, 10, 7), "#carved"),
    Box((5.5, 3, 6.9), (10.5, 4, 7), "#carved"),
    Box((3.5, 1.5, 6), (5, 2.5, 7), "#moss"), Box((10, 1.5, 9), (12.5, 2.25, 10), "#moss"),
], {"stone": "minecraft:block/stone", "carved": "minecraft:block/deepslate", "moss": "minecraft:block/moss_block"},
    particle="minecraft:block/stone")


def bat(x, z, drop):
    top = 16 - drop
    return [Box((x - 0.15, top, z - 0.15), (x + 0.15, 16, z + 0.15), "#string"),
            Box((x - 1, top - 3, z - 0.75), (x + 1, top, z + 0.75), "#bat"),
            Box((x - 0.75, top - 4, z - 0.6), (x + 0.75, top - 3, z + 0.6), "#bat"),
            Box((x - 3, top - 2.5, z - 0.1), (x - 1, top, z + 0.1), "#bat"),
            Box((x + 1, top - 2.5, z - 0.1), (x + 3, top, z + 0.1), "#bat"),
            Box((x - 0.5, top - 3.75, z - 0.8), (x - 0.2, top - 3.45, z - 0.6), "#eye"),
            Box((x + 0.2, top - 3.75, z - 0.8), (x + 0.5, top - 3.45, z - 0.6), "#eye")]


write(ROOM, "hanging_bats", bat(5, 5, 2) + bat(11, 7, 4) + bat(7, 11, 3),
      {"bat": "minecraft:block/black_wool", "string": "minecraft:block/gray_wool", "eye": "minecraft:block/red_concrete"},
      particle="#bat", cull=False)

write(ROOM, "candy_bowl", [
    Box((5, 0, 5), (11, 1, 11), "#bowl"), Box((3.5, 1, 3.5), (12.5, 2, 12.5), "#bowl"),
    Box((3, 2, 3), (13, 5, 4), "#bowl"), Box((3, 2, 12), (13, 5, 13), "#bowl"),
    Box((3, 2, 4), (4, 5, 12), "#bowl"), Box((12, 2, 4), (13, 5, 12), "#bowl"),
    Box((4, 2, 4), (12, 3, 12), "#inside"),
    Box((6, 4.5, 2.9), (7, 5.5, 3), "#face"), Box((9, 4.5, 2.9), (10, 5.5, 3), "#face"),
    Box((6, 2.5, 2.9), (10, 3.25, 3), "#face"),
], {"bowl": "minecraft:block/orange_terracotta", "inside": "minecraft:block/black_terracotta",
    "face": "minecraft:block/black_concrete"}, particle="#bowl")

write(ROOM, "witch_cauldron", [
    Box((1, 0, 1), (4, 3, 4), "#iron"), Box((12, 0, 1), (15, 3, 4), "#iron"),
    Box((1, 0, 12), (4, 3, 15), "#iron"), Box((12, 0, 12), (15, 3, 15), "#iron"),
    Box((1, 3, 1), (15, 4, 15), "#iron"),
    Box((1, 4, 1), (15, 13, 3), "#iron"), Box((1, 4, 13), (15, 13, 15), "#iron"),
    Box((1, 4, 3), (3, 13, 13), "#iron"), Box((13, 4, 3), (15, 13, 13), "#iron"),
    Box((3, 4, 3), (13, 11, 13), "#brew", glow=True),
    Box((5, 11, 6), (6.5, 11.5, 7.5), "#brew", glow=True), Box((9, 11, 9), (10, 11.5, 10), "#brew", glow=True),
], {"iron": "minecraft:block/cauldron_side", "brew": "minecraft:block/lime_concrete"},
    particle="minecraft:block/cauldron_side")
