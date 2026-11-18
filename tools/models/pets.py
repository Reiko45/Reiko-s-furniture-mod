"""Model templates for the pet corner section."""
from modelkit import *  # noqa: F401,F403

ROOM = "pets"
P, L, S = "#planks", "#log", "#stripped_log"
W = "#wool"

# Pet bed: a round-ish cushion with raised sides.
write(ROOM, "pet_bed", [
    Box((1, 0, 1), (15, 2, 15), W),
    Box((1, 2, 1), (15, 5, 3), W), Box((1, 2, 13), (15, 5, 15), W),
    Box((1, 2, 3), (3, 5, 13), W), Box((13, 2, 3), (15, 5, 13), W),
    Box((3, 2, 3), (13, 3, 13), "#inner"),
], {"inner": "reikos_furniture:block/fabric/white"}, particle=W)

# Cat tree: two blocks tall, carpeted wooden platforms on sisal posts.
CARPET = {"carpet": "reikos_furniture:block/fabric/light_gray", "rope": "minecraft:block/hay_block_side"}
write_tall(ROOM, "cat_tree",
    lower=[
        Box((0, 0, 0), (16, 2, 16), P, faces={"up": "#carpet"}),
        Box((6, 2, 6), (10, 16, 10), "#rope", skip=("up",)),
        Box((1, 10, 1), (8, 12, 8), P, faces={"up": "#carpet"}),
        Box((3, 2, 3), (5, 10, 5), "#rope"),
    ],
    upper=[
        Box((6, 0, 6), (10, 8, 10), "#rope", skip=("down",)),
        Box((1, 8, 1), (15, 10, 15), P, faces={"up": "#carpet"}),
        Box((1, 10, 1), (15, 12, 2), S), Box((1, 10, 14), (15, 12, 15), S),
        Box((1, 10, 2), (2, 12, 14), S), Box((14, 10, 2), (15, 12, 14), S),
    ], textures=CARPET)

# Feeding bowl: a colored bowl; the food inside is drawn by the display renderer.
write(ROOM, "feeding_bowl", [
    Box((4, 0, 4), (12, 1, 12), W),
    Box((3, 0, 3), (13, 3, 4), W), Box((3, 0, 12), (13, 3, 13), W),
    Box((3, 0, 4), (4, 3, 12), W), Box((12, 0, 4), (13, 3, 12), W),
], particle=W)
