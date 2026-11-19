"""Model templates for the outdoor section. Run tools/models/generate_all.py to rewrite every template."""
from modelkit import *  # noqa: F401,F403

# ---- from outdoor.py ----
P, L, S = "#planks", "#log", "#stripped_log"
ROOM = "outdoor"

# Patio table: slatted top (joins like the dining table), log legs.
slats = [Box((0, 14, z), (16, 15.5, z + 3.4), S) for z in (0, 4.2, 8.4, 12.6)] + [
    Box((1, 13, 1), (15, 14, 3), P), Box((1, 13, 13), (15, 14, 15), P)]
leg = Box((1, 0, 1), (3, 13, 3), L)
write(ROOM, "patio_table_top", slats)
write(ROOM, "patio_table_leg", [leg])
write(ROOM, "patio_table_item", slats + [leg.moved(dx=dx, dz=dz) for dx in (0, 12) for dz in (0, 12)])

# Patio chair: slatted seat and back, armrests.
write(ROOM, "patio_chair", [
    Box((2, 0, 2), (3.5, 8, 3.5), L), Box((12.5, 0, 2), (14, 8, 3.5), L),
    Box((2, 0, 12.5), (3.5, 15, 14), L), Box((12.5, 0, 12.5), (14, 15, 14), L),
] + [Box((3.5, 7, z), (12.5, 8, z + 2.2)) if False else Box((3.5, 7, z), (12.5, 8, z + 2.2), S) for z in (2, 4.7, 7.4, 10.1)] + [
    Box((3.5, 9.5, 12.75), (12.5, 11, 13.75), S),
    Box((3.5, 12.5, 12.75), (12.5, 14, 13.75), S),
    Box((2, 10, 2), (3.5, 11, 14), P), Box((12.5, 10, 2), (14, 11, 14), P),
    Box((2, 8, 2), (3.5, 10, 3.5), L), Box((12.5, 8, 2), (14, 10, 3.5), L),
])

# Garden bench: long slatted seat with a backrest and log armrests.
write(ROOM, "garden_bench", [
    Box((0.5, 0, 2), (2.5, 7, 4), L), Box((13.5, 0, 2), (15.5, 7, 4), L),
    Box((0.5, 0, 12), (2.5, 15, 14), L), Box((13.5, 0, 12), (15.5, 15, 14), L),
] + [Box((0, 6, z), (16, 7, z + 2.2), S) for z in (2.5, 5.2, 7.9, 10.6)] + [
    Box((2.5, 9, 12.5), (13.5, 10.5, 13.5), S),
    Box((2.5, 12, 12.5), (13.5, 13.5, 13.5), S),
    Box((0.5, 9.5, 2), (2.5, 10.5, 14), P), Box((13.5, 9.5, 2), (15.5, 10.5, 14), P),
    Box((0.5, 7, 2), (2.5, 9.5, 4), L), Box((13.5, 7, 2), (15.5, 9.5, 4), L),
])

# Planter box: wooden box full of soil; plants and crops grow on it.
write(ROOM, "planter_box", [
    Box((0, 0, 0), (16, 16, 1), P), Box((0, 0, 15), (16, 16, 16), P),
    Box((0, 0, 1), (1, 16, 15), P), Box((15, 0, 1), (16, 16, 15), P),
    Box((1, 0, 1), (15, 15, 15), "#dirt", faces={"up": "#soil"}),
] + [Box((x, 0, z), (x + 2, 16, z + 2), L) for x in (-0.25, 14.25) for z in (-0.25, 14.25)],
    {"dirt": "minecraft:block/dirt", "soil": "minecraft:block/farmland_moist"})


# Grill: food goes on the grate; the coals glow while it is lit.
def grill(lit):
    return [Box((x, 0, z), (x + 1, 8, z + 1), "#body") for x in (2, 13) for z in (2, 13)] + [
        Box((1.5, 8, 1.5), (14.5, 9, 14.5), "#body"),
        Box((1.5, 9, 1.5), (14.5, 11, 2.5), "#body"),
        Box((1.5, 9, 13.5), (14.5, 11, 14.5), "#body"),
        Box((1.5, 9, 2.5), (2.5, 11, 13.5), "#body"),
        Box((13.5, 9, 2.5), (14.5, 11, 13.5), "#body"),
        Box((2.5, 9, 2.5), (13.5, 9.75, 13.5), "#coal", glow=lit),
        Box((2.5, 10.75, 2.5), (13.5, 10.75, 13.5), "#grate", skip=("north", "south", "east", "west")),
        Box((5, 9.5, 0.5), (11, 10.25, 1.5), "#metal"),
        Box((14.5, 9.5, 4), (16, 10, 12), "#shelf"),
    ]

GRILL = {"body": "minecraft:block/black_concrete", "grate": "minecraft:block/iron_bars",
         "metal": "minecraft:block/iron_block", "shelf": "minecraft:block/stripped_spruce_log"}
write(ROOM, "grill", grill(False), {**GRILL, "coal": "minecraft:block/coal_block"},
      particle="minecraft:block/black_concrete", render_type="minecraft:cutout")
write(ROOM, "grill_on", grill(True), {**GRILL, "coal": "minecraft:block/magma"},
      particle="minecraft:block/black_concrete", render_type="minecraft:cutout")


# ---- 1.2: shutters, doorbell ----

def shutter_panel(x1, x2):
    """A louvered panel between x1 and x2 (closed, in front of the window)."""
    parts = [Box((x1, 0, 14), (x2, 1, 15.5), P), Box((x1, 15, 14), (x2, 16, 15.5), P),
             Box((x1, 1, 14), (x1 + 1, 15, 15.5), P), Box((x2 - 1, 1, 14), (x2, 15, 15.5), P)]
    parts += [Box((x1 + 1, y, 14.4), (x2 - 1, y + 1, 15.1), S) for y in range(2, 15, 2)]
    return parts


write(ROOM, "shutters", shutter_panel(0, 8) + shutter_panel(8, 16))
# Open: the panels fold out to the sides, flat against the wall.
write(ROOM, "shutters_open", [b.moved(dx=-8) for b in shutter_panel(0, 8)] + [b.moved(dx=8) for b in shutter_panel(8, 16)],
      cull=False)


# Doorbell: a small plate with a gold button (floor orientation, like vanilla buttons).
def doorbell(pressed):
    return [
        Box((5, 0, 5), (11, 1, 11), "#plate"),
        Box((6.5, 1, 6.5), (9.5, 1.5 if pressed else 2, 9.5), "#button"),
    ]

BELL = {"plate": "minecraft:block/iron_block", "button": "minecraft:block/gold_block"}
write(ROOM, "doorbell", doorbell(False), BELL, particle="minecraft:block/iron_block")
write(ROOM, "doorbell_pressed", doorbell(True), BELL, particle="minecraft:block/iron_block")

# Mailbox on a post (front faces north). The red flag on the right side is up when there is mail.
def mailbox(flag_up):
    parts = [
        Box((7, 0, 7), (9, 10, 9), S),
        Box((4, 10, 3), (12, 14, 13), P),
        Box((4.5, 14, 3), (11.5, 15, 13), P),
        Box((4.5, 10.5, 2.5), (11.5, 14.5, 3), S),
        Box((7.25, 12, 2), (8.75, 12.75, 2.5), L),
    ]
    if flag_up:
        parts += [Box((12, 11, 9), (12.5, 16, 9.75), "#flag"), Box((12, 14, 9.75), (12.5, 16, 12), "#flag")]
    else:
        parts += [Box((12, 11, 7), (12.5, 11.75, 12), "#flag"), Box((12, 11, 12), (12.5, 13, 12.75), "#flag")]
    return parts

write(ROOM, "mailbox", mailbox(False), {"flag": "minecraft:block/red_concrete"})
write(ROOM, "mailbox_flag", mailbox(True), {"flag": "minecraft:block/red_concrete"})
