"""More style pieces: desk, bookshelf, table lamp, floor lamp, counter (with its corners) and upper
cabinet, for every style.

Each style is described by a small kit (its legs, handles, cabinet body and door fronts, counter top,
lamp and floor lamp), and the pieces are put together from the kit, so the eight styles stay consistent.
Templates are written to template/style/<style>/ next to the ones from styles.py.
"""
from modelkit import *  # noqa: F401,F403

P, L, S = "#planks", "#log", "#stripped_log"
LIT = "minecraft:block/ochre_froglight_side"


def drawer_h(x1, x2, y1, y2, front, tray, handle, opened, zf=1, zb=1.5, pull=5.0):
    """A drawer with a handle (hx1, hx2, hy1, hy2, texture) in front of it (as in styles.py)."""
    d = pull if opened else 0
    parts = drawer(x1, x2, y1, y2, zf, zb, front, tray, opened, pull)
    hx1, hx2, hy1, hy2, tex = handle
    parts.append(Box((hx1, hy1, zf - 0.5 - d), (hx2, hy2, zf - d), tex))
    return parts


def mirror_z(boxes):
    """Mirror boxes front to back (north becomes south)."""
    return [Box((b.f[0], b.f[1], 16 - b.t[2]), (b.t[0], b.t[1], 16 - b.f[2]), b.tex, glow=b.glow) for b in boxes]


def desk(k):
    leg = k["leg"](14)
    parts = [Box((0, 14, 0), (16, 16, 16), k["desk_top"])] + leg + mirror_z(leg)
    parts += [Box((9, 0, 1), (15, 14, 15), k["body"]), Box((3, 6, 13.5), (9, 14, 14.5), k["body"])]
    for y1, y2 in ((8, 13), (2, 7)):
        mid = (y1 + y2) / 2
        parts += drawer_h(9.5, 14.5, y1, y2, k["front"], k["body"], (11, 13, mid - 0.4, mid + 0.4, k["handle"]), False, zf=0.5, zb=1)
    return parts + k.get("desk_extra", [])


def bookshelf(k):
    F = k["case"]
    parts = [Box((0, 0, 0), (1, 16, 16), F), Box((15, 0, 0), (16, 16, 16), F),
             Box((1, 15, 0), (15, 16, 16), F), Box((1, 0, 0), (15, 1, 16), F),
             Box((1, 1, 1), (15, 15, 16), F, faces={"north": "#books"})]
    return parts + k.get("shelf_extra", [])


def cabinet_doors(k, y1, y2, zf, hy1, hy2, opened):
    """Two doors and their handles on a front at zf + 0.5, with the style's decoration when closed."""
    zb = zf + 0.5
    parts = (door(0.75, 7.85, y1, y2, zf, zb, k["front"], opened, "left")
             + door(8.15, 15.25, y1, y2, zf, zb, k["front"], opened, "right")
             + door_handle(6.5, 7.25, hy1, hy2, zf - 0.5, zf, 0.75, 7.85, zf, k["handle"], opened, "left")
             + door_handle(8.75, 9.5, hy1, hy2, zf - 0.5, zf, 8.15, 15.25, zf, k["handle"], opened, "right"))
    if not opened and "door_extra" in k:
        parts += k["door_extra"](y1, y2, zf)
    return parts


def counter_base(k, opened):
    parts = [Box((0, 0, 2), (16, 1, 16), k["plinth"]), Box((0, 1, 1), (16, 14, 16), k["body"])]
    parts += cabinet_doors(k, 1.75, 13.25, 0.5, 10, 12.5, opened)
    if opened:
        parts += [Box((0.75, 1.75, 0.9), (15.25, 13.25, 1), k["body"]), Box((0.75, 7.25, 0.5), (15.25, 7.75, 1), k["body"])]
    return parts


def outer_counter(k, side, opened):
    doors = cabinet_doors(k, 1.75, 13.25, 0.5, 10, 12.5, opened)
    if side == "left":
        side_doors = rotate_ccw(cabinet_doors(k, 1.75, 13.25, 0.5, 10, 12.5, opened))
        body = [Box((2, 0, 2), (16, 1, 16), k["plinth"]), Box((1, 1, 1), (16, 14, 16), k["body"])]
    else:
        side_doors = rotate_cw(cabinet_doors(k, 1.75, 13.25, 0.5, 10, 12.5, opened))
        body = [Box((0, 0, 2), (14, 1, 16), k["plinth"]), Box((0, 1, 1), (15, 14, 16), k["body"])]
    shift = 0.5 if side == "left" else -0.5
    return body + [b.moved(dx=shift) for b in doors] + side_doors + k["counter_top"]


def upper_cabinet(k, opened):
    parts = [Box((0, 4, 8), (16, 16, 16), k["body"])] + cabinet_doors(k, 4.75, 15.25, 7.5, 5.5, 8, opened)
    if opened:
        parts += [Box((0.75, 4.75, 7.9), (15.25, 15.25, 8), k["body"]), Box((0.75, 9.75, 7.5), (15.25, 10.25, 8), k["body"])]
    return parts


def write_pieces(style, k):
    room = "style/" + style
    tex = {"books": "minecraft:block/bookshelf", **k["tex"]}
    write(room, "desk", desk(k), tex)
    write(room, "bookshelf", bookshelf(k), tex)
    write(room, "table_lamp", k["lamp"](False), tex, render_type=k.get("render_type"))
    write(room, "table_lamp_on", k["lamp"](True), tex, render_type=k.get("render_type"))
    lower, upper_off = k["floor_lamp"](False)
    _, upper_on = k["floor_lamp"](True)
    for b in lower:
        if b.t[1] >= 16:
            b.skip.add("up")
    for part in (upper_off, upper_on):
        for b in part:
            if b.f[1] <= 0:
                b.skip.add("down")
    write_tall(room, "floor_lamp", lower, upper_off, tex, render_type=k.get("render_type"))
    write(room, "floor_lamp_upper_on", upper_on, tex, render_type=k.get("render_type"))
    for suffix, opened in (("", False), ("_open", True)):
        write(room, "counter" + suffix, counter_base(k, opened) + k["counter_top"], tex)
        write(room, "upper_cabinet" + suffix, upper_cabinet(k, opened), tex)
        for side in ("left", "right"):
            write(room, f"counter_outer_{side}{suffix}", outer_counter(k, side, opened), tex)
    write(room, "counter_inner", [Box((0, 0, 2), (16, 1, 16), k["plinth"]), Box((0, 1, 1), (16, 14, 16), k["body"]),
                                  Box((0.75, 1.75, 0.5), (15.25, 13.25, 1), k["front"])] + k["counter_top"], tex)


def simple_lamp(base, stem, shade, on, shade_box=((4.5, 6, 4.5), (11.5, 11, 11.5))):
    return base + stem + [Box(shade_box[0], shade_box[1], shade, glow=on)]


def simple_floor_lamp(base, pole, shade, on, shade_box=((3.5, 6, 3.5), (12.5, 14, 12.5))):
    lower = base + [Box((7.5, 1, 7.5), (8.5, 16, 8.5), pole)]
    upper = [Box((7.5, 0, 7.5), (8.5, 6, 8.5), pole), Box(shade_box[0], shade_box[1], shade, glow=on)]
    return lower, upper


# ---- The kits ---------------------------------------------------------------------------------

def rustic():
    write_pieces("rustic", {
        "tex": {"shade": "reikos_furniture:block/fabric/brown"},
        "leg": lambda h: [Box((1, 0, 1), (3.5, h, 3.5), L)],
        "handle": L, "body": P, "front": P, "plinth": L, "case": P, "desk_top": P,
        "counter_top": [Box((0, 14, 0), (16, 16, 16), P)],
        "door_extra": lambda y1, y2, zf: [Box((1.5, (y1 + y2) / 2 - 0.5, zf - 0.25), (14.5, (y1 + y2) / 2 + 0.5, zf), L)],
        "lamp": lambda on: simple_lamp([Box((5, 0, 5), (11, 2, 11), L)], [Box((7.25, 2, 7.25), (8.75, 6, 8.75), L)], "#shade", on),
        "floor_lamp": lambda on: simple_floor_lamp([Box((4.5, 0, 4.5), (11.5, 1.5, 11.5), L)], L, "#shade", on),
    })


def modern():
    M, B = "#metal", "#body"
    write_pieces("modern", {
        "tex": {"metal": "minecraft:block/black_concrete", "body": "minecraft:block/white_concrete",
                "counter": "minecraft:block/quartz_block_top", "shade": "minecraft:block/white_concrete"},
        "leg": lambda h: [Box((1, 0, 1), (2.5, h, 2.5), M)],
        "handle": M, "body": B, "front": P, "plinth": M, "case": B, "desk_top": P,
        "counter_top": [Box((0, 14, 0), (16, 15.5, 16), "#counter"), Box((0, 15.5, 0), (16, 16, 16), "#counter")],
        "lamp": lambda on: [Box((5.5, 0, 5.5), (10.5, 0.5, 10.5), M), Box((7.75, 0.5, 7.75), (8.25, 7, 8.25), M),
                            Box((5, 7, 5), (11, 10, 11), "#shade", glow=on)],
        "floor_lamp": lambda on: ([Box((5, 0, 5), (11, 0.75, 11), M), Box((7.75, 0.75, 7.75), (8.25, 16, 8.25), M)],
                                  [Box((7.75, 0, 7.75), (8.25, 12, 8.25), M), Box((8.25, 11.5, 7.75), (13, 12, 8.25), M),
                                   Box((11, 8, 6), (15, 11.5, 10), "#shade", glow=on)]),
    })


def royal():
    G = "#gold"
    write_pieces("royal", {
        "tex": {"gold": "minecraft:block/gold_block", "shade": "reikos_furniture:block/fabric/red",
                "marble": "minecraft:block/polished_diorite"},
        "leg": lambda h: [Box((1, 0, 1), (3.5, h, 3.5), S), Box((0.75, 0, 0.75), (3.75, 0.75, 3.75), G)],
        "handle": G, "body": P, "front": S, "plinth": S, "case": P, "desk_top": P,
        "counter_top": [Box((0, 14, 0), (16, 16, 16), "#marble"), Box((0, 13.5, 0.25), (16, 14, 0.5), G)],
        "shelf_extra": [Box((0, 15.5, -0.25), (16, 16, 0), G), Box((0, 0, -0.25), (16, 0.5, 0), G)],
        "desk_extra": [Box((0, 13.5, -0.25), (16, 14, 0), G)],
        "lamp": lambda on: [Box((5, 0, 5), (11, 1, 11), G), Box((7.25, 1, 7.25), (8.75, 3, 8.75), G),
                            Box((7.5, 3, 7.5), (8.5, 6, 8.5), G), Box((4.5, 6, 4.5), (11.5, 11, 11.5), "#shade", glow=on),
                            Box((4.25, 6, 4.25), (11.75, 6.5, 11.75), G)],
        "floor_lamp": lambda on: ([Box((4.5, 0, 4.5), (11.5, 1, 11.5), G), Box((6.5, 1, 6.5), (9.5, 2, 9.5), G),
                                   Box((7.5, 2, 7.5), (8.5, 16, 8.5), G)],
                                  [Box((7.5, 0, 7.5), (8.5, 6, 8.5), G), Box((3.5, 6, 3.5), (12.5, 14, 12.5), "#shade", glow=on),
                                   Box((3.25, 6, 3.25), (12.75, 6.5, 12.75), G), Box((7.5, 14, 7.5), (8.5, 15, 8.5), G)]),
    })


def japanese():
    PA, I = "#paper", "#iron"

    def andon(on, h, y0=0):
        """A paper lantern: wooden corner posts with washi panels that glow."""
        x1, x2 = 5, 11
        return [Box((x1 + 0.5, y0 + 1, x1 + 0.5), (x2 - 0.5, y0 + h - 0.5, x2 - 0.5), PA, glow=on)] + \
            [Box((x, y0, z), (x + 0.75, y0 + h, z + 0.75), S) for x in (x1, x2 - 0.75) for z in (x1, x2 - 0.75)] + \
            [Box((x1, y0 + h - 0.5, x1), (x2, y0 + h, x2), S)]
    write_pieces("japanese", {
        "tex": {"paper": "reikos_furniture:block/style/washi", "iron": "minecraft:block/anvil"},
        "leg": lambda h: [Box((1.5, 0, 1.5), (3, h, 3), S)],
        "handle": I, "body": P, "front": S, "plinth": S, "case": P, "desk_top": P,
        "counter_top": [Box((0, 14, 0), (16, 16, 16), P)],
        "door_extra": lambda y1, y2, zf: [Box((1.5, y, zf - 0.25), (14.5, y + 0.5, zf), S) for y in (y1 + 3, y2 - 3)],
        "lamp": lambda on: andon(on, 10),
        "floor_lamp": lambda on: ([Box((4.5, 0, 4.5), (11.5, 1, 11.5), P), Box((7.25, 1, 7.25), (8.75, 16, 8.75), S)],
                                  [Box((7.25, 0, 7.25), (8.75, 3, 8.75), S)] + andon(on, 11, 3)),
    })


def medieval():
    I = "#iron"

    def candle(on, y):
        parts = [Box((7.25, y, 7.25), (8.75, y + 3, 8.75), "#candle")]
        if on:
            parts.append(Box((7.6, y + 3, 7.6), (8.4, y + 4.25, 8.4), "#flame", glow=True))
        return parts
    write_pieces("medieval", {
        "tex": {"iron": "reikos_furniture:block/style/wrought_iron", "candle": "minecraft:block/white_wool",
                "flame": "minecraft:block/yellow_concrete"},
        "leg": lambda h: [Box((1, 0, 1), (4, h, 4), L)],
        "handle": I, "body": P, "front": P, "plinth": L, "case": P, "desk_top": P,
        "counter_top": [Box((0, 13.5, 0), (16, 16, 16), P)],
        "door_extra": lambda y1, y2, zf: [Box((1, y, zf - 0.25), (7, y + 1, zf), I) for y in (y1 + 1.5, y2 - 2.5)]
                                         + [Box((9, y, zf - 0.25), (15, y + 1, zf), I) for y in (y1 + 1.5, y2 - 2.5)],
        "shelf_extra": [Box((0, 0, -0.25), (1.5, 16, 0), I), Box((14.5, 0, -0.25), (16, 16, 0), I)],
        "lamp": lambda on: [Box((5.5, 0, 5.5), (10.5, 0.75, 10.5), I), Box((7.5, 0.75, 7.5), (8.5, 3, 8.5), I),
                            Box((6, 3, 6), (10, 3.75, 10), I)] + candle(on, 3.75),
        "floor_lamp": lambda on: ([Box((5, 0, 5), (11, 1, 11), I), Box((7.5, 1, 7.5), (8.5, 16, 8.5), I)],
                                  [Box((7.5, 0, 7.5), (8.5, 9, 8.5), I), Box((4, 9, 4), (12, 9.75, 12), I)]
                                  + [b.moved(dx=dx, dy=0, dz=dz) for dx, dz in ((-2.5, 0), (2.5, 0), (0, -2.5), (0, 2.5))
                                     for b in candle(on, 9.75)]),
        "render_type": None,
    })


def victorian():
    B = "#brass"

    def turned(h):
        return [Box((1.5, 0, 1.5), (3.5, 1, 3.5), S), Box((1, 1, 1), (4, 2.5, 4), S),
                Box((1.75, 2.5, 1.75), (3.25, h - 2, 3.25), S), Box((1.25, h - 2, 1.25), (3.75, h, 3.75), S)]

    def fringe_shade(x1, y1, x2, y2, on):
        return [Box((x1, y1, x1), (x2, y2, x2), "#shade", glow=on)] + \
            [Box((x, y1 - 1, x1 - 0.1), (x + 0.5, y1, x1), B) for x in range(int(x1) + 1, int(x2), 2)]
    write_pieces("victorian", {
        "tex": {"brass": "minecraft:block/gold_block", "marble": "minecraft:block/calcite",
                "shade": "reikos_furniture:block/fabric/green"},
        "leg": turned,
        "handle": B, "body": P, "front": S, "plinth": S, "case": P, "desk_top": P,
        "counter_top": [Box((0, 14, 0), (16, 16, 16), "#marble")],
        "door_extra": lambda y1, y2, zf: [Box((2, y1 + 1.5, zf - 0.25), (6.75, y2 - 1.5, zf), S),
                                         Box((9.25, y1 + 1.5, zf - 0.25), (14, y2 - 1.5, zf), S)],
        "shelf_extra": [Box((0, 15, -0.5), (16, 16, 0), S), Box((5, 15.5, -0.75), (11, 16, -0.5), S)],
        "lamp": lambda on: [Box((5.5, 0, 5.5), (10.5, 1, 10.5), B), Box((7, 1, 7), (9, 2.5, 9), B),
                            Box((7.5, 2.5, 7.5), (8.5, 6, 8.5), B)] + fringe_shade(4.5, 6, 11.5, 11, on),
        "floor_lamp": lambda on: ([Box((5, 0, 5), (11, 1, 11), B), Box((6.5, 1, 6.5), (9.5, 2.5, 9.5), B),
                                   Box((7.5, 2.5, 7.5), (8.5, 16, 8.5), B)],
                                  [Box((7.5, 0, 7.5), (8.5, 6, 8.5), B)] + fringe_shade(3.5, 6, 12.5, 14, on)),
    })


def futuristic():
    SH, CH, GL = "#shell", "#chrome", "#glow"

    def glow(f, t):
        return Box(f, t, GL, glow=True)
    write_pieces("futuristic", {
        "tex": {"shell": "minecraft:block/white_concrete", "chrome": "reikos_furniture:block/style/chrome",
                "glow": "reikos_furniture:block/style/glow_strip"},
        "leg": lambda h: [Box((1, 0, 1), (2.5, h, 2.5), CH)],
        "handle": CH, "body": SH, "front": P, "plinth": CH, "case": SH, "desk_top": SH,
        "counter_top": [Box((0, 14, 0.25), (16, 16, 16), SH), Box((0, 15, 0), (16, 16, 0.25), SH),
                        Box((0, 14, 0), (16, 14.5, 0.25), SH), glow((0, 14.5, 0), (16, 15, 0.25))],
        "door_extra": lambda y1, y2, zf: [glow((7.85, y1, zf), (8.15, y2, zf + 0.5))],
        "shelf_extra": [glow((1, 7.75, 0.5), (15, 8.25, 1))],
        "desk_extra": [glow((0, 13.5, 0), (16, 14, 0.5))],
        "lamp": lambda on: [Box((5.5, 0, 5.5), (10.5, 1, 10.5), CH), Box((7.5, 1, 7.5), (8.5, 5, 8.5), CH),
                            Box((5.5, 5, 5.5), (10.5, 10, 10.5), GL if on else SH, glow=on)],
        "floor_lamp": lambda on: ([Box((5, 0, 5), (11, 1, 11), CH), Box((7.5, 1, 7.5), (8.5, 16, 8.5), CH)],
                                  [Box((7.5, 0, 7.5), (8.5, 6, 8.5), CH), Box((6.5, 6, 6.5), (9.5, 15, 9.5), GL if on else SH, glow=on),
                                   Box((7, 15, 7), (9, 16, 9), CH)]),
    })


def beach():
    WH, R = "#white", "#rope"
    write_pieces("beach", {
        "tex": {"white": "reikos_furniture:block/style/whitewash", "rope": "reikos_furniture:block/style/rope",
                "shade": "reikos_furniture:block/fabric/white"},
        "leg": lambda h: [Box((1, 0, 1), (3, h, 3), WH)],
        "handle": R, "body": WH, "front": P, "plinth": WH, "case": WH, "desk_top": P,
        "counter_top": [Box((0, 14, 0), (16, 16, 16), P)],
        "door_extra": lambda y1, y2, zf: [Box((1.5, y, zf - 0.25), (7.4, y + 0.5, zf), WH) for y in range(int(y1) + 1, int(y2), 2)]
                                         + [Box((8.6, y, zf - 0.25), (14.5, y + 0.5, zf), WH) for y in range(int(y1) + 1, int(y2), 2)],
        "shelf_extra": [Box((-0.25, 14, -0.25), (16.25, 15, 0.25), R)],
        "lamp": lambda on: simple_lamp([Box((5.5, 0, 5.5), (10.5, 1, 10.5), WH)],
                                       [Box((6.5, 1, 6.5), (9.5, 6, 9.5), R)], "#shade", on),
        "floor_lamp": lambda on: simple_floor_lamp([Box((5, 0, 5), (11, 1, 11), WH), Box((7, 1, 7), (9, 3, 9), R)], L,
                                                   "#shade", on),
    })


for build in (rustic, modern, royal, japanese, medieval, victorian, futuristic, beach):
    build()
