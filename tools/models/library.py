"""Model templates for the library section."""
from modelkit import *  # noqa: F401,F403

ROOM = "library"
P, L, S = "#planks", "#log", "#stripped_log"
BOOKS = {"books": "minecraft:block/bookshelf"}


# Tall bookcase: two blocks tall, three shelves of books. The open model is the same (no doors).
def books(y1, y2):
    return Box((1, y1, 1.5), (15, y2, 15), P, faces={"north": "#books"}, uvs={"north": [0, 1, 16, 1 + (y2 - y1)]})

lower = [Box((0, 0, 0), (1, 16, 16), P, skip=("up",)), Box((15, 0, 0), (16, 16, 16), P, skip=("up",)),
         Box((1, 0, 15), (15, 16, 16), P), Box((1, 0, 0), (15, 1, 16), P), Box((1, 8.5, 0), (15, 9.5, 16), P),
         books(1, 7.5), books(9.5, 16)]
upper = [Box((0, 0, 0), (1, 16, 16), P, skip=("down",)), Box((15, 0, 0), (16, 16, 16), P, skip=("down",)),
         Box((1, 0, 15), (15, 16, 16), P), Box((1, 6, 0), (15, 7, 16), P), Box((-0.5, 15, -0.5), (16.5, 16, 16), S),
         books(0, 5.5), books(7, 14.5)]
write_tall(ROOM, "tall_bookcase", lower, upper, BOOKS)
write(ROOM, "tall_bookcase_lower_open", lower, BOOKS)
write(ROOM, "tall_bookcase_upper_open", upper, BOOKS)

# Library ladder: leans on the bookcase behind it (back of the block).
write(ROOM, "library_ladder", [
    Box((1.5, 0, 13), (3, 16, 14.5), L), Box((13, 0, 13), (14.5, 16, 14.5), L),
] + [Box((3, y, 13.25), (13, y + 1, 14.25), S) for y in (1.5, 5.5, 9.5, 13.5)]
  + [Box((2, 15, 14.5), (2.5, 16, 16), "#metal"), Box((13.5, 15, 14.5), (14, 16, 16), "#metal")],
  {"metal": "minecraft:block/gold_block"})


# Reading lamp: brass base and stem with a green glass shade.
def reading_lamp(on):
    return [
        Box((4, 0, 6), (12, 1, 10), "#brass"),
        Box((7.5, 1, 7.5), (8.5, 6, 8.5), "#brass"),
        Box((3, 6, 6), (13, 8.5, 10), "#shade"),
        Box((4, 5.9, 7), (12, 6, 9), "#bulb", glow=on),
    ]

LAMP = {"brass": "minecraft:block/gold_block", "shade": "minecraft:block/green_stained_glass"}
write(ROOM, "reading_lamp", reading_lamp(False), {**LAMP, "bulb": "minecraft:block/white_concrete"},
      particle="minecraft:block/gold_block", render_type="minecraft:translucent")
write(ROOM, "reading_lamp_on", reading_lamp(True), {**LAMP, "bulb": "minecraft:block/ochre_froglight_side"},
      particle="minecraft:block/gold_block", render_type="minecraft:translucent")


# Globe on a wooden stand. The ball is drawn turned by 0, 22.5, 45 or -22.5 degrees (the blockstate
# adds quarter turns), so spinning it steps through 16 angles.
def globe(angle):
    turn = {"origin": [8, 9, 8], "axis": "y", "angle": angle} if angle else None
    ball = [Box((4, 5.5, 5), (12, 12.5, 11), "#globe", rotation=turn),
            Box((5, 4.5, 4), (11, 13.5, 12), "#globe", rotation=turn),
            Box((4.5, 5, 4.5), (11.5, 13, 11.5), "#globe", rotation=turn)]
    return [Box((5, 0, 5), (11, 1, 11), P), Box((7.5, 1, 7.5), (8.5, 4.5, 8.5), L),
            Box((7.5, 13.5, 7.5), (8.5, 14.5, 8.5), "#brass")] + ball

GLOBE = {"globe": "reikos_furniture:block/globe", "brass": "minecraft:block/gold_block"}
for name, angle in (("0", 0), ("22", 22.5), ("45", 45), ("m22", -22.5)):
    write(ROOM, "globe_" + name, globe(angle), GLOBE)

# Book stand: a small slanted lectern for one book.
write(ROOM, "book_stand", [
    Box((4, 0, 4), (12, 1, 12), P), Box((7.5, 1, 7.5), (8.5, 8, 8.5), L),
    Box((3, 8, 4), (13, 9, 12), P, rotation={"origin": [8, 8.5, 8], "axis": "x", "angle": -22.5}),
    Box((3, 8.5, 3.5), (13, 10, 4.5), S, rotation={"origin": [8, 8.5, 8], "axis": "x", "angle": -22.5}),
])


# Writing desk: a desk with a drawer, a sheet of paper, an inkwell and a quill.
def writing_desk(opened):
    parts = [
        Box((0, 12, 1), (16, 13.5, 15), P),
        Box((1, 0, 2), (2.5, 12, 3.5), L), Box((13.5, 0, 2), (15, 12, 3.5), L),
        Box((1, 0, 12.5), (2.5, 12, 14), L), Box((13.5, 0, 12.5), (15, 12, 14), L),
        Box((9, 9, 2), (15, 12, 14), P),
        Box((4, 13.5, 4), (9, 13.6, 10), "#paper"),
        Box((12, 13.5, 10), (13.5, 15, 11.5), "#ink"),
        Box((12.5, 15, 10.5), (13, 18.5, 11), "#feather", rotation={"origin": [12.75, 15, 10.75], "axis": "z", "angle": -22.5}),
    ]
    parts += drawer(9.5, 14.5, 9.5, 11.75, 1.5, 2, S, P, opened, 5)
    return parts

DESK = {"paper": "minecraft:block/white_wool", "ink": "minecraft:block/black_concrete", "feather": "minecraft:block/white_concrete"}
write(ROOM, "writing_desk", writing_desk(False), DESK)
write(ROOM, "writing_desk_open", writing_desk(True), DESK)
