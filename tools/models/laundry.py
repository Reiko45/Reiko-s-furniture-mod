"""Model templates for the laundry room section."""
from modelkit import *  # noqa: F401,F403

ROOM = "laundry"
P, L, S = "#planks", "#log", "#stripped_log"


def machine(front):
    return [
        Box((0.5, 0, 1), (15.5, 15, 15.5), "#body", faces={"north": "#front"}, uvs={"north": [0, 0, 16, 16]}),
        Box((0.5, 15, 1), (15.5, 16, 15.5), "#top"),
    ]

for kind in ("washing_machine", "dryer"):
    for suffix, on in (("", False), ("_on", True)):
        write(ROOM, kind + suffix, machine(None), {
            "body": "reikos_furniture:block/appliance/fridge_body",
            "top": "reikos_furniture:block/appliance/steel",
            "front": f"reikos_furniture:block/appliance/{kind}_front{'_on' if on else ''}",
        }, particle="reikos_furniture:block/appliance/fridge_body")

# Laundry basket: woven basket with clothes peeking out.
write(ROOM, "laundry_basket", [
    Box((2, 0, 3), (14, 1, 13), S),
    Box((2, 1, 3), (14, 9, 4), S), Box((2, 1, 12), (14, 9, 13), S),
    Box((2, 1, 4), (3, 9, 12), S), Box((13, 1, 4), (14, 9, 12), S),
    Box((3, 1, 4), (13, 8, 12), "#clothes", faces={"up": "#clothes2"}),
    Box((1.5, 7, 7), (2, 8.5, 9), L), Box((14, 7, 7), (14.5, 8.5, 9), L),
], {"clothes": "reikos_furniture:block/fabric/light_blue", "clothes2": "reikos_furniture:block/fabric/white"})
