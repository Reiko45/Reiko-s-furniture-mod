"""Model templates for the garage section."""
from modelkit import *  # noqa: F401,F403

ROOM = "garage"
P, L, S = "#planks", "#log", "#stripped_log"
METAL = {"metal": "minecraft:block/iron_block", "dark": "minecraft:block/gray_concrete",
         "black": "minecraft:block/black_concrete"}

# Pegboard: a holed board in a wooden frame on the wall; up to six tools hang on it.
write(ROOM, "pegboard", [
    Box((1, 1, 15.25), (15, 15, 16), "#board"),
    Box((0, 0, 14.75), (16, 1, 16), P), Box((0, 15, 14.75), (16, 16, 16), P),
    Box((0, 1, 14.75), (1, 15, 16), P), Box((15, 1, 14.75), (16, 15, 16), P),
], {"board": "reikos_furniture:block/pegboard"})

# Toolbox: a painted metal box with a handle and two latches.
write(ROOM, "toolbox", [
    Box((3, 0, 5), (13, 4.5, 11), "#paint"),
    Box((2.75, 4.5, 4.75), (13.25, 6, 11.25), "#paint"),
    Box((6, 6, 7.5), (6.75, 7.5, 8.5), "#metal"), Box((9.25, 6, 7.5), (10, 7.5, 8.5), "#metal"),
    Box((6, 7.5, 7.5), (10, 8.25, 8.5), "#black"),
    Box((4.5, 3.75, 4.5), (5.5, 5, 5), "#metal"), Box((10.5, 3.75, 4.5), (11.5, 5, 5), "#metal"),
], METAL, particle="#paint")


# Workshop bench: a heavy table with a vise on the front corner, a shelf below and a few tools.
write(ROOM, "workshop_bench", [
    Box((0, 13, 1), (16, 16, 15), P),
    Box((1, 0, 2), (3, 13, 4), L), Box((13, 0, 2), (15, 13, 4), L),
    Box((1, 0, 12), (3, 13, 14), L), Box((13, 0, 12), (15, 13, 14), L),
    Box((1, 3, 3), (15, 4, 13), S),
    Box((1.5, 12, -1), (5.5, 15.5, 1), "#dark"), Box((1.5, 15.5, -1), (5.5, 16, 3), "#metal"),
    Box((3, 13, -2.5), (4, 14, -1), "#metal"), Box((1.5, 13, -2.75), (5.5, 14, -2.5), "#metal"),
    Box((8, 16, 5), (13, 16.75, 6), "#dark"), Box((12, 16, 4), (13.5, 17, 7), "#metal"),
    Box((9, 16, 9), (10, 16.5, 13), S), Box((8.5, 16, 12.5), (10.5, 16.75, 13.5), "#dark"),
], METAL)

# Metal shelving: four posts, three shelves, a few crates.
shelving = [Box((x, 0, z), (x + 1, 16, z + 1), "#metal") for x in (1, 14) for z in (2, 13)]
shelving += [Box((1, y, 2), (15, y + 1, 14), "#dark") for y in (1, 7, 13)]
shelving += [Box((2, 2, 4), (7, 6, 12), "#crate"), Box((9, 8, 5), (14, 12, 12), "#crate"), Box((3, 14, 4), (9, 16, 11), "#crate")]
CRATE = {**METAL, "crate": "minecraft:block/stripped_birch_log"}
write(ROOM, "metal_shelving", shelving, CRATE, particle="minecraft:block/iron_block")
write(ROOM, "metal_shelving_open", shelving, CRATE, particle="minecraft:block/iron_block")


# Oil drum: an octagonal drum with two bands and a lid.
def drum():
    return [
        Box((3.5, 0, 1.5), (12.5, 15, 14.5), "#paint"), Box((1.5, 0, 3.5), (14.5, 15, 12.5), "#paint"),
        Box((3, 4, 1), (13, 5, 15), "#metal"), Box((1, 4, 3), (15, 5, 13), "#metal"),
        Box((3, 10, 1), (13, 11, 15), "#metal"), Box((1, 10, 3), (15, 11, 13), "#metal"),
        Box((3.5, 15, 2), (12.5, 15.5, 14), "#metal"), Box((2, 15, 3.5), (14, 15.5, 12.5), "#metal"),
        Box((9, 15.5, 4), (11, 16, 6), "#black"),
    ]

write(ROOM, "oil_drum", drum(), METAL, particle="#paint")
write(ROOM, "oil_drum_open", drum()[:-3] + [Box((9, 15, 4), (11, 15.25, 6), "#black")], METAL, particle="#paint")
write(ROOM, "toolbox_open", [
    Box((3, 0, 5), (13, 4.5, 11), "#paint"),
    Box((2.75, 4.5, 11), (13.25, 11, 12.5), "#paint"),
    Box((3.5, 3.5, 5.5), (12.5, 4.5, 10.5), "#dark"),
    Box((5, 4.5, 6), (9, 5, 7), "#metal"), Box((8, 4.5, 8.5), (11, 5.25, 9.5), "#black"),
], METAL, particle="#paint")
