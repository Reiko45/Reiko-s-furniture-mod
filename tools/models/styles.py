"""Model templates for the furniture styles (Rustic, Modern, Royal, Japanese).

Each style has its own templates in template/style/<style>/ with the same names as the classic pieces
(bed_head, dining_table_top, sofa_base, ...), so data generation fills them in the same way.
Run tools/models/generate_all.py to rewrite every template.
"""
from modelkit import *  # noqa: F401,F403

P, L, S = "#planks", "#log", "#stripped_log"
W, SHEET = "#wool", "#sheet"
BED_TEX = {"sheet": "minecraft:block/white_wool", "wool": "reikos_furniture:block/fabric/red"}

BED_ITEM_DISPLAY = {
    "gui": {"rotation": [30, 160, 0], "translation": [2, 3, 0], "scale": [0.5, 0.5, 0.5]},
    "ground": {"rotation": [0, 0, 0], "translation": [0, 3, 0], "scale": [0.25, 0.25, 0.25]},
    "fixed": {"rotation": [0, 180, 0], "translation": [0, 0, 0], "scale": [0.5, 0.5, 0.5]},
    "thirdperson_righthand": {"rotation": [30, 160, 0], "translation": [0, 3, -2], "scale": [0.375, 0.375, 0.375]},
    "firstperson_righthand": {"rotation": [30, 160, 0], "translation": [0, 0, 0], "scale": [0.375, 0.375, 0.375]},
    "firstperson_lefthand": {"rotation": [30, 160, 0], "translation": [0, 0, 0], "scale": [0.375, 0.375, 0.375]},
}
BUNK_ITEM_DISPLAY = dict(TALL_ITEM_DISPLAY)
BUNK_ITEM_DISPLAY["gui"] = {"rotation": [30, 160, 0], "translation": [1, -3, 0], "scale": [0.36, 0.36, 0.36]}


# ---- Shared helpers -----------------------------------------------------------------------

def corners(leg):
    """A table leg drawn at the north-west corner, copied to all four corners (for item models)."""
    xs = [v for b in leg for v in (b.f[0], b.t[0])]
    zs = [v for b in leg for v in (b.f[2], b.t[2])]
    ox, oz = 16 - min(xs) - max(xs), 16 - min(zs) - max(zs)
    return [b.moved(dx=dx, dz=dz) for dx in (0, ox) for dz in (0, oz) for b in leg]


def feet(x1, x2, z1, z2, tex, h):
    """Four feet, x1..x2 by z1..z2 at the north-west corner, mirrored to the others."""
    return [Box((x, 0, z), (x + x2 - x1, h, z + z2 - z1), tex)
            for x in (x1, 16 - x2) for z in (z1, 16 - z2)]


_SWAP_X = {"west": "east", "east": "west"}


def mirror_x(boxes):
    """Mirror boxes left to right (west becomes east)."""
    return [Box((16 - b.t[0], b.f[1], b.f[2]), (16 - b.f[0], b.t[1], b.t[2]), b.tex,
                {_SWAP_X.get(k, k): v for k, v in b.faces.items()}, b.glow, {_SWAP_X.get(k, k) for k in b.skip})
            for b in boxes]


def mattress(head, y, z1, z2, thick=4, x1=1, x2=15):
    """Sheet-covered mattress lying on y, with a pillow (head half) and the blanket (#wool)."""
    top = y + thick
    parts = [Box((x1, y, z1), (x2, top, z2), SHEET)]
    bz = z1
    if head:
        parts.append(Box((x1 + 2, top, z1 + 1), (x2 - 2, top + 1.5, z1 + 5), SHEET))
        bz = z1 + 6
    parts += [
        Box((x1, top, bz), (x2, top + 0.75, z2), W),
        Box((x1 - 0.25, y + 1, bz), (x1, top + 0.75, z2), W),
        Box((x2, y + 1, bz), (x2 + 0.25, top + 0.75, z2), W),
    ]
    return parts


def drawer_h(x1, x2, y1, y2, front, tray, handle, opened, zf=1, zb=1.5, pull=5.0):
    """A drawer with a handle (hx1, hx2, hy1, hy2, texture) in front of it; both move out when open."""
    d = pull if opened else 0
    parts = drawer(x1, x2, y1, y2, zf, zb, front, tray, opened, pull)
    hx1, hx2, hy1, hy2, tex = handle
    parts.append(Box((hx1, hy1, zf - 0.5 - d), (hx2, hy2, zf - d), tex))
    return parts


def knob(x, y, tex, size=1.0):
    """A square knob centred on (x, y) for drawer_h."""
    return (x - size / 2, x + size / 2, y - size / 2, y + size / 2, tex)


def tall_skip(lower, upper):
    """Hide the faces where the two halves of a tall piece meet."""
    for b in lower:
        if b.t[1] == 16 and b.f[1] > 0:
            b.skip.add("up")
    for b in upper:
        if b.f[1] == 0:
            b.skip.add("down")
    return lower, upper


def bunk_part(head, top, post, pw, rail, board, base=P, extra=None):
    """One quarter of a bunk bed: the head or foot half of the lower bed or the top bunk."""
    zs = (0, pw) if head else (16 - pw, 16)
    post_h = 14 if top else 16
    parts = [Box((0, 0, zs[0]), (pw, post_h, zs[1]), post), Box((16 - pw, 0, zs[0]), (16, post_h, zs[1]), post)]
    y = 4 if top else 3
    rz = (pw, 16) if head else (0, 16 - pw)
    parts += [
        Box((0, y, rz[0]), (1, y + 3, rz[1]), rail),
        Box((15, y, rz[0]), (16, y + 3, rz[1]), rail),
        Box((1, y, rz[0]), (15, y + 1, rz[1]), base),
    ]
    parts += mattress(head, y + 1, rz[0], rz[1])
    if head:
        parts.append(Box((pw, y + 6, 0.5), (16 - pw, y + 10, 1.5), board))
    else:
        parts.append(Box((pw, y + 4, 14.5), (16 - pw, y + 8, 15.5), board))
    if top:
        parts.append(Box((0, y + 5, rz[0]), (1, y + 8, rz[1]), rail))  # safety rail on the left
    if extra:
        parts += extra(head, top, y, pw)
    return parts


def write_style(room, pieces, tex, fabric_tex):
    """Writes every template of one style from its piece builders."""
    bed_tex = {**BED_TEX, **tex}
    head, foot = pieces["bed"]
    write(room, "bed_head", head, bed_tex)
    write(room, "bed_foot", foot, bed_tex)
    write(room, "bed_item", [b.moved(dz=-8) for b in head] + [b.moved(dz=8) for b in foot], bed_tex,
          display=BED_ITEM_DISPLAY, cull=False)

    bunk = pieces["bunk"]
    for top in (False, True):
        for half in (True, False):
            write(room, f"bunk_bed_{'top' if top else 'lower'}_{'head' if half else 'foot'}", bunk(half, top), bed_tex)
    item = ([b.moved(dz=-8) for b in bunk(True, False)] + [b.moved(dz=8) for b in bunk(False, False)]
            + [b.moved(dz=-8, dy=16) for b in bunk(True, True)] + [b.moved(dz=8, dy=16) for b in bunk(False, True)])
    write(room, "bunk_bed_item", item, bed_tex, display=BUNK_ITEM_DISPLAY, cull=False)

    write(room, "dining_chair", pieces["chair"], tex)
    for name in ("dining_table", "coffee_table"):
        top, leg = pieces[name]
        write(room, name + "_top", top, tex)
        write(room, name + "_leg", leg, tex)
        write(room, name + "_item", top + corners(leg), tex)
    for name in ("nightstand", "dresser"):
        write(room, name, pieces[name](False), tex)
        write(room, name + "_open", pieces[name](True), tex)
    lower, upper = tall_skip(*pieces["wardrobe"](False))
    write_tall(room, "wardrobe", lower, upper, tex)
    lower, upper = tall_skip(*pieces["wardrobe"](True))
    write(room, "wardrobe_lower_open", lower, tex)
    write(room, "wardrobe_upper_open", upper, tex)

    base, arm, corner = pieces["sofa"]
    write(room, "sofa_base", base, fabric_tex, particle=W)
    write(room, "sofa_arm_left", arm, fabric_tex, particle=W)
    write(room, "sofa_arm_right", mirror_x(arm), fabric_tex, particle=W)
    write(room, "sofa_item", base + arm + mirror_x(arm), fabric_tex, particle=W)
    write(room, "sofa_corner_left", corner, fabric_tex, particle=W)
    write(room, "sofa_corner_right", mirror_x(corner), fabric_tex, particle=W)
    write(room, "armchair", pieces["armchair"], fabric_tex, particle=W)


def wardrobe_interior(lower, upper, rail_tex, x1=1, x2=15):
    """Back wall and clothes rail that show behind open wardrobe doors."""
    lower.append(Box((x1, 2, 0.9), (x2, 16, 1), S))
    upper.append(Box((x1, 0, 0.9), (x2, 13, 1), S))
    upper.append(Box((x1, 9.5, 0.4), (x2, 10.25, 0.9), rail_tex))


# ---- Rustic: log posts with the bark on, thick planks -------------------------------------

def rustic():
    head = [
        Box((0, 0, 0), (3, 16, 3), L), Box((13, 0, 0), (16, 16, 3), L),
        Box((3, 4, 1), (13, 13, 2), P),
        Box((3, 8, 0.75), (13, 9, 2.25), S),
        Box((3, 13, 0.5), (13, 15, 2.5), L),
        Box((0, 3, 3), (1, 6.5, 16), L), Box((15, 3, 3), (16, 6.5, 16), L),
        Box((1, 3, 3), (15, 4, 16), P),
    ] + mattress(True, 4, 3, 16)
    foot = [
        Box((0, 0, 13), (3, 11, 16), L), Box((13, 0, 13), (16, 11, 16), L),
        Box((3, 4, 13.5), (13, 9, 14.5), P),
        Box((3, 9, 13), (13, 11, 15), L),
        Box((0, 3, 0), (1, 6.5, 13), L), Box((15, 3, 0), (16, 6.5, 13), L),
        Box((1, 3, 0), (15, 4, 13), P),
    ] + mattress(False, 4, 0, 13.5)

    def bunk(head_half, top):
        return bunk_part(head_half, top, L, 3, L, P)

    chair = feet(2, 4, 2, 4, L, 7) + [
        Box((2, 7, 2), (14, 9, 14), P),
        Box((2, 9, 12), (4, 16, 14), L), Box((12, 9, 12), (14, 16, 14), L),
        Box((4, 11, 12.5), (12, 12.5, 13.5), L), Box((4, 14, 12.5), (12, 15.5, 13.5), L),
    ]

    def posts(h, x1=1, x2=3.5):
        return [Box((x, 0, z), (x + x2 - x1, h, z + x2 - x1), L) for x in (x1, 16 - x2) for z in (x1, 16 - x2)]

    def nightstand(opened):
        return posts(14) + [
            Box((1.5, 1, 1.5), (14.5, 14, 14.5), P),
            Box((0, 14, 0), (16, 16, 16), P),
        ] + drawer_h(3.5, 12.5, 8, 13, S, P, knob(8, 10.5, L, 1.5), opened) \
          + drawer_h(3.5, 12.5, 2, 7, S, P, knob(8, 4.5, L, 1.5), False)

    def dresser(opened):
        parts = posts(14, 0.5, 3) + [
            Box((1, 1, 1.5), (15, 14, 15.5), P),
            Box((0, 14, 0), (16, 16, 16), P),
        ]
        for row, (y1, y2) in enumerate(((9.75, 13.5), (5.75, 9.25), (1.5, 5.25))):
            mid = (y1 + y2) / 2
            parts += drawer_h(3, 13, y1, y2, S, P, (5, 6.5, mid - 0.75, mid + 0.75, L), opened and row == 0)
            if not (opened and row == 0):
                parts.append(Box((9.5, mid - 0.75, 0.5), (11, mid + 0.75, 1), L))
            else:
                parts.append(Box((9.5, mid - 0.75, -4.5), (11, mid + 0.75, -4), L))
        return parts

    def wardrobe(opened):
        lower = [
            Box((0, 0, 0.5), (16, 2, 16), S),
            Box((0, 0, 0.5), (2, 16, 2.5), L), Box((14, 0, 0.5), (16, 16, 2.5), L),
            Box((0.5, 2, 1), (15.5, 16, 16), P),
        ]
        lower += door(2, 7.95, 2, 16, 0.5, 1, P, opened, "left") + door(8.05, 14, 2, 16, 0.5, 1, P, opened, "right")
        upper = [
            Box((0, 0, 0.5), (2, 14, 2.5), L), Box((14, 0, 0.5), (16, 14, 2.5), L),
            Box((0.5, 0, 1), (15.5, 14, 16), P),
            Box((0, 14, 0), (16, 16, 16), L),
        ]
        upper += door(2, 7.95, 0, 13, 0.5, 1, P, opened, "left") + door(8.05, 14, 0, 13, 0.5, 1, P, opened, "right")
        upper += door_handle(6.25, 7.25, 0.5, 4.5, 0, 0.5, 2, 7.95, 0.5, L, opened, "left")
        upper += door_handle(8.75, 9.75, 0.5, 4.5, 0, 0.5, 8.05, 14, 0.5, L, opened, "right")
        if opened:
            wardrobe_interior(lower, upper, L, 2, 14)
        return lower, upper

    F = "#frame"
    sofa_base = [
        Box((0, 0, 1), (16, 3, 16), F),
        Box((0, 3, 1.5), (16, 8, 11.5), W),
        Box((0, 3, 11.5), (16, 14, 15), W),
        Box((0, 3, 15), (16, 15, 16), F),
    ]
    sofa_arm = [Box((0, 3, 1), (2.5, 11, 11.5), F)]
    sofa_corner = [Box((0, 8, 0), (4.5, 14, 11.5), W)]
    armchair = [
        Box((1, 0, 1), (15, 3, 15), F),
        Box((3, 3, 1.5), (13, 7, 12), W),
        Box((3, 3, 12), (13, 14, 14.5), W),
        Box((1, 3, 14.5), (15, 15, 15), F),
        Box((1, 3, 1), (3, 10, 14.5), F), Box((13, 3, 1), (15, 10, 14.5), F),
    ]
    write_style("style/rustic", {
        "bed": (head, foot), "bunk": bunk, "chair": chair,
        "dining_table": ([Box((0, 13, 0), (16, 16, 16), P)], [Box((1, 0, 1), (4, 13, 4), L)]),
        "coffee_table": ([Box((0, 6, 0), (16, 9, 16), P)], [Box((1, 0, 1), (4, 6, 4), L)]),
        "nightstand": nightstand, "dresser": dresser, "wardrobe": wardrobe,
        "sofa": (sofa_base, sofa_arm, sofa_corner), "armchair": armchair,
    }, {}, {"frame": "minecraft:block/spruce_log"})


# ---- Modern: black metal legs, flat white fronts, thin tops -------------------------------

def modern():
    M, B = "#metal", "#body"
    head = [
        Box((1, 0, 1), (15, 1.5, 16), M),
        Box((0, 1.5, 0), (16, 3.5, 16), P),
        Box((0, 3.5, 0), (16, 12, 1.5), P),
        Box((1, 5, 1.5), (15, 11, 2), W),
    ] + mattress(True, 3.5, 2, 16, x1=0.5, x2=15.5)
    foot = [
        Box((1, 0, 0), (15, 1.5, 15), M),
        Box((0, 1.5, 0), (16, 3.5, 16), P),
    ] + mattress(False, 3.5, 0, 15.5, x1=0.5, x2=15.5)

    def bunk(head_half, top):
        return bunk_part(head_half, top, M, 1.5, M, P)

    chair = feet(3, 4, 3, 4, M, 7.5) + [
        Box((2.5, 7.5, 2.5), (13.5, 9, 13.5), P),
        Box((3, 9, 12.5), (4, 11, 13.5), M), Box((12, 9, 12.5), (13, 11, 13.5), M),
        Box((2.5, 11, 12.5), (13.5, 16, 13.5), P),
    ]

    def bar(x1, x2, y):
        return (x1, x2, y - 0.25, y + 0.25, M)

    def nightstand(opened):
        return feet(1.5, 2.5, 2, 3, M, 3) + [
            Box((1, 3, 1.5), (15, 13, 15), B),
        ] + drawer_h(1.5, 14.5, 8.25, 12.5, P, B, bar(4, 12, 11.5), opened) \
          + drawer_h(1.5, 14.5, 3.5, 7.75, P, B, bar(4, 12, 6.75), False)

    def dresser(opened):
        parts = [Box((1, 0, 2), (15, 1.5, 15), M), Box((0, 1.5, 1), (16, 13, 16), B)]
        for row, (y1, y2) in enumerate(((9.25, 12.5), (5.5, 8.75), (2, 5.25))):
            parts += drawer_h(0.5, 15.5, y1, y2, P, B, bar(4, 12, y2 - 0.75), opened and row == 0, zf=0.5, zb=1)
        return parts

    def wardrobe(opened):
        lower = [Box((0.5, 0, 1), (15.5, 1.5, 15.5), M), Box((0, 1.5, 1), (16, 16, 16), B)]
        lower += door(0.5, 7.95, 1.5, 16, 0.5, 1, P, opened, "left") + door(8.05, 15.5, 1.5, 16, 0.5, 1, P, opened, "right")
        lower += door_handle(7, 7.5, 10, 16, 0, 0.5, 0.5, 7.95, 0.5, M, opened, "left")
        lower += door_handle(8.5, 9, 10, 16, 0, 0.5, 8.05, 15.5, 0.5, M, opened, "right")
        upper = [Box((0, 0, 1), (16, 16, 16), B)]
        upper += door(0.5, 7.95, 0, 15.5, 0.5, 1, P, opened, "left") + door(8.05, 15.5, 0, 15.5, 0.5, 1, P, opened, "right")
        upper += door_handle(7, 7.5, 0, 6, 0, 0.5, 0.5, 7.95, 0.5, M, opened, "left")
        upper += door_handle(8.5, 9, 0, 6, 0, 0.5, 8.05, 15.5, 0.5, M, opened, "right")
        if opened:
            wardrobe_interior(lower, upper, M)
        return lower, upper

    F = "#frame"
    sofa_base = feet(1, 2, 2, 3, F, 1.5) + [
        Box((0, 1.5, 1), (16, 4, 16), W),
        Box((0, 4, 1.5), (16, 7, 12), W),
        Box((0, 4, 12), (16, 14, 16), W),
    ]
    sofa_arm = [Box((0, 4, 1), (2, 10, 12), W)]
    sofa_corner = [Box((0, 7, 0), (4.5, 14, 12), W)]
    armchair = feet(2, 3, 2, 3, F, 2) + [
        Box((1, 2, 1), (15, 4.5, 15), W),
        Box((2.5, 4.5, 1.5), (13.5, 7, 12), W),
        Box((1, 4.5, 12), (15, 14, 15), W),
        Box((1, 4.5, 1), (2.5, 9, 12), W), Box((13.5, 4.5, 1), (15, 9, 12), W),
    ]
    write_style("style/modern", {
        "bed": (head, foot), "bunk": bunk, "chair": chair,
        "dining_table": ([Box((0, 15, 0), (16, 16, 16), P)], [Box((1, 0, 1), (2.5, 15, 2.5), M)]),
        "coffee_table": ([Box((0, 6, 0), (16, 7.5, 16), P)], [Box((1, 0, 1), (2.5, 6, 2.5), M)]),
        "nightstand": nightstand, "dresser": dresser, "wardrobe": wardrobe,
        "sofa": (sofa_base, sofa_arm, sofa_corner), "armchair": armchair,
    }, {"metal": "minecraft:block/black_concrete", "body": "minecraft:block/white_concrete"},
        {"frame": "minecraft:block/black_concrete"})


# ---- Royal: carved legs, gold trim, velvet ------------------------------------------------

def royal():
    G, V = "#gold", "#velvet"
    head = [
        Box((0, 0, 0), (2.5, 15, 2.5), S), Box((13.5, 0, 0), (16, 15, 2.5), S),
        Box((0.5, 15, 0.5), (2, 16, 2), G), Box((14, 15, 0.5), (15.5, 16, 2), G),
        Box((2.5, 4, 0.75), (13.5, 14, 1.75), P),
        Box((3.5, 6, 1.75), (12.5, 13, 2.25), V),
        Box((5, 14, 0.75), (11, 15.5, 1.75), P),
        Box((7, 15.5, 0.75), (9, 16, 1.75), G),
        Box((0, 3, 2.5), (1, 6, 16), P), Box((15, 3, 2.5), (16, 6, 16), P),
        Box((0, 6, 2.5), (1, 6.5, 16), G), Box((15, 6, 2.5), (16, 6.5, 16), G),
        Box((1, 3, 2.5), (15, 4, 16), P),
    ] + mattress(True, 4, 2.5, 16)
    foot = [
        Box((0, 0, 13.5), (2.5, 11, 16), S), Box((13.5, 0, 13.5), (16, 11, 16), S),
        Box((0.5, 11, 14), (2, 12, 15.5), G), Box((14, 11, 14), (15.5, 12, 15.5), G),
        Box((2.5, 4, 14.25), (13.5, 10, 15.25), P),
        Box((3.5, 5.5, 15.25), (12.5, 9, 15.75), V),
        Box((0, 3, 0), (1, 6, 13.5), P), Box((15, 3, 0), (16, 6, 13.5), P),
        Box((0, 6, 0), (1, 6.5, 13.5), G), Box((15, 6, 0), (16, 6.5, 13.5), G),
        Box((1, 3, 0), (15, 4, 13.5), P),
    ] + mattress(False, 4, 0, 14)

    def bunk_extra(head_half, top, y, pw):
        zs = (0.5, pw - 0.5) if head_half else (16.5 - pw, 15.5)
        h = 14 if top else 16
        parts = []
        if top:  # gold finials on the top bunk's posts
            parts += [Box((0.5, h, zs[0]), (pw - 0.5, h + 1, zs[1]), G), Box((16.5 - pw, h, zs[0]), (15.5, h + 1, zs[1]), G)]
        bz = (0.25, 0.5) if head_half else (15.5, 15.75)
        by = y + 9.5 if head_half else y + 7.5
        parts.append(Box((pw, by, bz[0]), (16 - pw, by + 0.5, bz[1]), G))
        return parts

    def bunk(head_half, top):
        return bunk_part(head_half, top, S, 2.5, P, P, extra=bunk_extra)

    chair = feet(2.5, 4, 2.5, 4, S, 7) + feet(2.25, 4.25, 2.25, 4.25, G, 0.75) + [
        Box((2, 7, 2), (14, 8.5, 14), P),
        Box((2.5, 8.5, 2.5), (13.5, 9.5, 12.5), V),
        Box((2.5, 8.5, 12.5), (4, 16, 14), S), Box((12, 8.5, 12.5), (13.5, 16, 14), S),
        Box((4, 10, 12.75), (12, 15, 13.75), V),
        Box((4, 15, 12.5), (12, 16, 14), P),
        Box((7, 15.25, 12.25), (9, 15.75, 12.5), G),
    ]

    def legs(h):
        return feet(1, 3, 1, 3, S, h) + feet(0.75, 3.25, 0.75, 3.25, G, 0.75)

    def nightstand(opened):
        return legs(3) + [
            Box((1, 3, 1.5), (15, 13.5, 15), P),
            Box((1, 13.5, 1.25), (15, 14, 15), G),
            Box((0, 14, 0), (16, 16, 16), P),
        ] + drawer_h(2, 14, 8.75, 13, S, P, knob(8, 10.875, G, 1.5), opened) \
          + drawer_h(2, 14, 4, 8.25, S, P, knob(8, 6.125, G, 1.5), False)

    def dresser(opened):
        parts = legs(3) + [
            Box((0.5, 3, 1.5), (15.5, 13.5, 15.5), P),
            Box((0.5, 13.5, 1.25), (15.5, 14, 15.5), G),
            Box((0, 14, 0), (16, 16, 16), P),
        ]
        for row, (y1, y2) in enumerate(((10, 13), (6.75, 9.75), (3.5, 6.5))):
            for col, (x1, x2) in enumerate(((1.25, 7.75), (8.25, 14.75))):
                parts += drawer_h(x1, x2, y1, y2, S, P, knob((x1 + x2) / 2, (y1 + y2) / 2, G), opened and row == 0 and col == 0)
        return parts

    def wardrobe(opened):
        lower = [
            Box((0, 0, 0.5), (16, 2, 16), P),
            Box((0, 2, 0.25), (16, 2.5, 0.5), G),
            Box((0.5, 2, 1), (15.5, 16, 16), P),
        ]
        lower += door(1, 7.9, 2.5, 16, 0.5, 1, S, opened, "left") + door(8.1, 15, 2.5, 16, 0.5, 1, S, opened, "right")
        upper = [
            Box((0.5, 0, 1), (15.5, 14, 16), P),
            Box((0.5, 13, 0.5), (15.5, 13.5, 1), G),
            Box((0, 14, 0), (16, 16, 16), P),
            Box((0, 14.5, -0.25), (16, 15, 0), G),
        ]
        upper += door(1, 7.9, 0, 12.5, 0.5, 1, S, opened, "left") + door(8.1, 15, 0, 12.5, 0.5, 1, S, opened, "right")
        upper += door_handle(6.5, 7.5, 1.5, 3, 0, 0.5, 1, 7.9, 0.5, G, opened, "left")
        upper += door_handle(8.5, 9.5, 1.5, 3, 0, 0.5, 8.1, 15, 0.5, G, opened, "right")
        if opened:
            wardrobe_interior(lower, upper, G)
        return lower, upper

    F = "#frame"
    sofa_base = feet(1, 2.5, 1.5, 3, G, 2) + [
        Box((0, 2, 1), (16, 4, 16), F),
        Box((0, 3.5, 0.75), (16, 4, 1), G),
        Box((0, 4, 1.5), (16, 8, 11.5), W),
        Box((0, 4, 11.5), (16, 14, 15.5), W),
        Box((0, 4, 15.5), (16, 15, 16), F),
        Box((0, 14, 11.5), (16, 15, 15.5), F),
        Box((0, 14.5, 11.25), (16, 15, 11.5), G),
    ] + [Box((x, y, 11.25), (x + 0.5, y + 0.5, 11.5), G) for x in (3.75, 7.75, 11.75) for y in (9, 12)]
    sofa_arm = [Box((0, 4, 1), (2.5, 11, 11.5), W), Box((0.5, 5, 0.75), (2, 10, 1), G)]
    sofa_corner = [Box((0, 8, 0), (4.5, 14, 11.5), W)]
    armchair = feet(1.5, 3, 1.5, 3, F, 2) + feet(1.25, 3.25, 1.25, 3.25, G, 0.5) + [
        Box((1, 2, 1), (15, 4, 15), F),
        Box((1, 3.5, 0.75), (15, 4, 1), G),
        Box((2.5, 4, 1.5), (13.5, 7, 11.5), W),
        Box((2.5, 4, 11.5), (13.5, 16, 15), W),
        Box((1, 7, 10), (2.5, 16, 15), W), Box((13.5, 7, 10), (15, 16, 15), W),
        Box((1, 4, 1), (2.5, 10, 10), W), Box((13.5, 4, 1), (15, 10, 10), W),
    ] + [Box((x, y, 11.25), (x + 0.5, y + 0.5, 11.5), G) for x, y in ((5, 10.5), (10.5, 10.5), (7.75, 13.5))]
    write_style("style/royal", {
        "bed": (head, foot), "bunk": bunk, "chair": chair,
        "dining_table": ([Box((0, 14, 0), (16, 16, 16), P)], [
            Box((1, 0, 1), (3.5, 12.5, 3.5), S), Box((0.5, 12.5, 0.5), (4, 14, 4), S),
            Box((0.75, 9, 0.75), (3.75, 9.75, 3.75), G), Box((0.75, 0, 0.75), (3.75, 1, 3.75), G)]),
        "coffee_table": ([Box((0, 7, 0), (16, 9, 16), P)], [
            Box((1, 0, 1), (3.5, 6, 3.5), S), Box((0.5, 6, 0.5), (4, 7, 4), S),
            Box((0.75, 0, 0.75), (3.75, 0.75, 3.75), G)]),
        "nightstand": nightstand, "dresser": dresser, "wardrobe": wardrobe,
        "sofa": (sofa_base, sofa_arm, sofa_corner), "armchair": armchair,
    }, {"gold": "minecraft:block/gold_block", "velvet": "reikos_furniture:block/fabric/red"},
        {"frame": "minecraft:block/dark_oak_planks", "gold": "minecraft:block/gold_block"})


# ---- Japanese: futon on tatami, floor chairs, low tables, tansu, shoji ---------------------

def japanese():
    PA, T, I, C = "#paper", "#tatami", "#iron", "#cushion"
    head = [
        Box((0, 0, 0), (16, 2, 16), P),
        Box((0.5, 2, 0.5), (15.5, 2.5, 16), T),
        Box((0, 2, 0), (16, 5, 1), S),
    ] + mattress(True, 2.5, 1.5, 16, thick=3, x1=1.5, x2=14.5)
    foot = [
        Box((0, 0, 0), (16, 2, 16), P),
        Box((0.5, 2, 0), (15.5, 2.5, 15.5), T),
    ] + mattress(False, 2.5, 0, 14.5, thick=3, x1=1.5, x2=14.5)

    def bunk_extra(head_half, top, y, pw):
        z = (0.25, 0.5) if head_half else (15.5, 15.75)
        y1, y2 = (y + 6, y + 10) if head_half else (y + 4, y + 8)
        return [Box((7.5, y1, z[0]), (8.5, y2, z[1]), S), Box((pw, (y1 + y2) / 2 - 0.25, z[0]), (16 - pw, (y1 + y2) / 2 + 0.25, z[1]), S)]

    def bunk(head_half, top):
        return bunk_part(head_half, top, S, 2, S, PA, extra=bunk_extra)

    chair = [
        Box((2, 0, 2), (14, 1.5, 14), P),
        Box((2.5, 1.5, 2.5), (13.5, 4, 11.5), C),
        Box((2, 1.5, 11.5), (14, 12, 13.5), P),
        Box((3, 4, 11), (13, 10.5, 11.5), C),
    ]

    def pull(x, y):
        return (x - 1.5, x + 1.5, y - 0.5, y + 0.5, I)

    def nightstand(opened):
        return [Box((1, 0, 1.5), (15, 10, 15), P)] \
            + drawer_h(1.5, 14.5, 5.25, 9.5, S, P, pull(8, 7.375), opened) \
            + drawer_h(1.5, 14.5, 0.5, 4.75, S, P, pull(8, 2.625), False)

    def dresser(opened):
        parts = [Box((0, 0, 1), (16, 10, 16), P), Box((8, 10, 1), (16, 16, 16), P)]
        for row, (y1, y2) in enumerate(((5.25, 9.5), (0.5, 4.75))):
            mid = (y1 + y2) / 2
            parts += drawer_h(0.5, 7.75, y1, y2, S, P, pull(4.125, mid), opened and row == 0, zf=0.5, zb=1)
            parts += drawer_h(8.25, 15.5, y1, y2, S, P, pull(11.875, mid), False, zf=0.5, zb=1)
        parts += drawer_h(8.5, 15.5, 10.5, 15.5, S, P, pull(12, 13), False, zf=0.5, zb=1)
        return parts

    def shoji(x1, x2, y1, y2, z, rails):
        """A sliding paper panel on the track at depth z..z+0.5, with its wooden lattice."""
        parts = [Box((x1, y1, z + 0.25), (x2, y2, z + 0.5), PA),
                 Box((x1, y1, z), (x1 + 0.75, y2, z + 0.5), S), Box((x2 - 0.75, y1, z), (x2, y2, z + 0.5), S),
                 Box(((x1 + x2) / 2 - 0.25, y1, z), ((x1 + x2) / 2 + 0.25, y2, z + 0.25), S)]
        parts += [Box((x1 + 0.75, r, z), (x2 - 0.75, r + 0.5, z + 0.25), S) for r in rails]
        return parts

    def wardrobe(opened):
        left = 7 if opened else 0  # the left panel slides over the right one
        lower = [Box((0, 0, 0), (16, 1.5, 16), S), Box((0.5, 1.5, 1), (15.5, 16, 16), P)]
        lower += shoji(1 + left, 8 + left, 1.5, 16, 0, (1.5, 6, 11))
        lower += shoji(8, 15, 1.5, 16, 0.5, (1.5, 6, 11))
        upper = [Box((0.5, 0, 1), (15.5, 14, 16), P), Box((0, 14, 0), (16, 16, 16), S)]
        upper += shoji(1 + left, 8 + left, 0, 14, 0, (3, 8, 13.5))
        upper += shoji(8, 15, 0, 14, 0.5, (3, 8, 13.5))
        if opened:
            lower.append(Box((1, 1.5, 0.9), (8, 16, 1), S))
            upper.append(Box((1, 0, 0.9), (8, 14, 1), S))
            upper.append(Box((1, 10, 0.4), (8, 10.75, 0.9), S))
        return lower, upper

    F = "#frame"
    sofa_base = [
        Box((0, 0, 1), (16, 2, 16), F),
        Box((0, 2, 1.5), (16, 5, 12), W),
        Box((0, 2, 12), (16, 12, 15.5), W),
        Box((0, 2, 15.5), (16, 12, 16), F),
    ]
    sofa_arm = [Box((0, 2, 1), (2, 8, 12), F)]
    sofa_corner = [Box((0, 5, 0), (4.5, 12, 12), W)]
    armchair = [
        Box((1, 0, 1), (15, 2, 15), F),
        Box((2, 2, 1.5), (14, 5, 12), W),
        Box((2, 2, 12), (14, 11.5, 14.5), W),
        Box((1, 2, 14.5), (15, 12, 15), F),
        Box((1, 2, 1), (2, 7, 14.5), F), Box((14, 2, 1), (15, 7, 14.5), F),
    ]
    write_style("style/japanese", {
        "bed": (head, foot), "bunk": bunk, "chair": chair,
        "dining_table": ([Box((0, 7, 0), (16, 8.5, 16), P)], [Box((1.5, 0, 1.5), (3, 7, 3), S)]),
        "coffee_table": ([Box((0, 4.5, 0), (16, 6, 16), P)], [Box((0.5, 0, 0.5), (3, 4.5, 3), S)]),
        "nightstand": nightstand, "dresser": dresser, "wardrobe": wardrobe,
        "sofa": (sofa_base, sofa_arm, sofa_corner), "armchair": armchair,
    }, {"paper": "reikos_furniture:block/style/washi", "tatami": "reikos_furniture:block/style/tatami",
        "iron": "minecraft:block/anvil", "cushion": "reikos_furniture:block/fabric/blue"},
        {"frame": "minecraft:block/bamboo_planks"})


rustic()
modern()
royal()
japanese()
