"""Model templates for the music room section."""
from modelkit import *  # noqa: F401,F403

ROOM = "music"
P, L, S = "#planks", "#log", "#stripped_log"

# Upright piano: body at the back, keyboard shelf at the front.
write(ROOM, "piano", [
    Box((0, 0, 8), (16, 16, 16), P),
    Box((0, 0, 7), (1, 9, 8), P), Box((15, 0, 7), (16, 9, 8), P),
    Box((0, 7, 3), (16, 8.5, 8), P, faces={"up": "#keys"}, uvs={"up": [0, 0, 16, 16]}),
    Box((1, 2, 3.5), (2, 7, 4.5), L), Box((14, 2, 3.5), (15, 7, 4.5), L),
    Box((1, 0, 2.5), (2.5, 2, 5), L), Box((13.5, 0, 2.5), (15, 2, 5), L),
    Box((2, 11, 7.5), (14, 15, 8), S),
], {"keys": "reikos_furniture:block/piano_keys"})

# Piano bench: short padded bench.
write(ROOM, "piano_bench", [
    Box((2, 0, 4), (3.5, 7, 5.5), L), Box((12.5, 0, 4), (14, 7, 5.5), L),
    Box((2, 0, 10.5), (3.5, 7, 12), L), Box((12.5, 0, 10.5), (14, 7, 12), L),
    Box((1.5, 7, 3.5), (14.5, 8, 12.5), P),
    Box((2, 8, 4), (14, 9, 12), "#cushion"),
], {"cushion": "reikos_furniture:block/fabric/black"})

# Drum kit: bass drum, snare and a hi-hat cymbal.
write(ROOM, "drum_kit", [
    Box((4, 0, 7), (12, 8, 11), "#drum", faces={"north": "#skin", "south": "#skin"}),
    Box((5, 3, 6.9), (11, 5, 7), "#rim"),
    Box((1, 7, 2), (6, 9, 7), "#drum", faces={"up": "#skin"}),
    Box((3, 0, 4), (4, 7, 5), "#rim"),
    Box((12.5, 0, 3.5), (13.5, 10, 4.5), "#rim"),
    Box((10.5, 10, 1.5), (15.5, 10.5, 6.5), "#cymbal"),
    Box((10.5, 10.75, 1.5), (15.5, 11.25, 6.5), "#cymbal"),
], {"drum": "minecraft:block/red_concrete", "skin": "minecraft:block/white_concrete",
    "rim": "minecraft:block/iron_block", "cymbal": "minecraft:block/gold_block"}, particle="minecraft:block/red_concrete")


# Record player: a wooden cabinet with a turntable; a disc shows while one is playing.
def record_player(has_record):
    parts = [
        Box((1, 0, 2), (15, 5, 14), P),
        Box((2.5, 5, 3.5), (11.5, 5.25, 12.5), "#black"),
        Box((12, 5, 10), (13.5, 6.5, 11.5), "#metal"),
        Box((12.5, 6, 4.5), (13, 6.5, 10), "#metal"),
        Box((1.5, 1.5, 1.5), (14.5, 4, 2), S),
    ]
    if has_record:
        parts += [Box((3, 5.25, 4), (11, 5.5, 12), "#disc"), Box((6, 5.5, 7), (8, 5.6, 9), "#label")]
    return parts

RP = {"black": "minecraft:block/black_concrete", "metal": "minecraft:block/iron_block",
      "disc": "minecraft:block/black_concrete_powder", "label": "minecraft:block/red_concrete"}
write(ROOM, "record_player", record_player(False), RP)
write(ROOM, "record_player_playing", record_player(True), RP)
