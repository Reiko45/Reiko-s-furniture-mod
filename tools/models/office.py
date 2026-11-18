"""Model templates for the office section. Run tools/models/generate_all.py to rewrite every template."""
from modelkit import *  # noqa: F401,F403

# ---- from office.py ----
P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "office"
MET = {"metal": "minecraft:block/iron_block", "dark": "minecraft:block/gray_concrete", "black": "minecraft:block/black_concrete"}

# Office chair: five-star base on wheels, gas lift, cushioned seat and back.
write(ROOM, "office_chair", [
    Box((2, 0.75, 7.25), (14, 1.75, 8.75), "#dark"),
    Box((7.25, 0.75, 2), (8.75, 1.75, 14), "#dark"),
] + [Box((x, 0, z), (x + 1.5, 0.75, z + 1.5), "#black") for x, z in ((1.5, 7.25), (13, 7.25), (7.25, 1.5), (7.25, 13))] + [
    Box((7.5, 1.75, 7.5), (8.5, 6, 8.5), "#metal"),
    Box((2, 6, 2), (14, 9, 14), "#wool"),
    Box((7.5, 7, 13), (8.5, 10.5, 14), "#metal"),
    Box((2.5, 10.5, 12.5), (13.5, 16, 14), "#wool"),
], MET, particle="#wool")


# Filing cabinet: three tall drawers; the top one pulls out when open.
def filing_cabinet(opened):
    parts = [
        Box((1, 0, 1.5), (15, 16, 15.5), P),
    ]
    for i, (y1, y2) in enumerate(((11, 15.5), (6, 10.5), (1, 5.5))):
        d = 6 if (opened and i == 0) else 0
        parts += drawer(1.75, 14.25, y1, y2, 1, 1.5, S, P, opened and i == 0, 6)
        mid = (y1 + y2) / 2
        parts.append(Box((6, mid + 0.5, 0.5 - d), (10, mid + 1.25, 1 - d), L))
        parts.append(Box((7, mid - 1.25, 0.9 - d), (9, mid, 1 - d), "#label"))
    return parts

LABEL = {"label": "minecraft:block/white_wool"}
write(ROOM, "filing_cabinet", filing_cabinet(False), LABEL)
write(ROOM, "filing_cabinet_open", filing_cabinet(True), LABEL)


# Computer: monitor on a stand with a keyboard in front. The screen lights up when on.
def computer(on):
    return [
        Box((5, 0, 9), (11, 0.5, 13), "#black"),
        Box((7, 0.5, 10.5), (9, 3, 11.5), "#black"),
        Box((2, 3, 9.5), (14, 12, 10.5), "#black"),
        Box((2.5, 3.5, 9.25), (13.5, 11.5, 9.5), "#screen", glow=on, uvs={"north": [0, 0, 16, 16]}),
        Box((2.5, 0, 2), (12.5, 0.75, 6), "#dark", faces={"up": "#keys"}),
        Box((13, 0, 3), (14.5, 0.75, 5), "#dark"),
    ]

write(ROOM, "computer", computer(False), {**MET, "screen": "minecraft:block/gray_concrete",
                                          "keys": "minecraft:block/gray_concrete_powder"}, particle="minecraft:block/black_concrete")
write(ROOM, "computer_on", computer(True), {**MET, "screen": "reikos_furniture:block/screen/computer",
                                            "keys": "minecraft:block/gray_concrete_powder"}, particle="minecraft:block/black_concrete")


# Desk lamp: weighted base, bent arm and a shade with a glowing bulb.
def desk_lamp(on):
    return [
        Box((5, 0, 7), (11, 1, 13), "#dark"),
        Box((7.5, 1, 9.5), (8.5, 9, 10.5), "#metal"),
        Box((7.5, 8, 5), (8.5, 9, 10.5), "#metal"),
        Box((5.5, 6, 2.5), (10.5, 9, 7), "#dark"),
        Box((6, 5.9, 3), (10, 6, 6.5), "#bulb", glow=on),
    ]

write(ROOM, "desk_lamp", desk_lamp(False), {**MET, "bulb": "minecraft:block/white_concrete"}, particle="minecraft:block/gray_concrete")
write(ROOM, "desk_lamp_on", desk_lamp(True), {**MET, "bulb": "minecraft:block/ochre_froglight_side"}, particle="minecraft:block/gray_concrete")

