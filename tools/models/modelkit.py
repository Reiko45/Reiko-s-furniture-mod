"""Tiny helper for writing Minecraft block model templates from boxes (pixel units, front = north)."""
import json, math, os

FACES = ["down", "up", "north", "south", "west", "east"]
# Faces with the wood textures get tint index 1, so wooden furniture can be stained (tinted) in game.
STAINABLE = {"#planks", "#log", "#stripped_log"}
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "src", "main", "resources", "assets",
                   "reikos_furniture", "models", "block", "template")


def _shift(a, b):
    """Offset that brings [a, b] into the 0..16 range for UV purposes."""
    off = math.floor(a / 16.0) * 16
    if b - off > 16:
        off = 0
    return off


def _r(v):
    v = round(v, 4)
    return int(v) if v == int(v) else v


def uv(face, f, t):
    x1, y1, z1 = f
    x2, y2, z2 = t
    ox, oy, oz = _shift(x1, x2), _shift(y1, y2), _shift(z1, z2)
    x1, x2, y1, y2, z1, z2 = x1 - ox, x2 - ox, y1 - oy, y2 - oy, z1 - oz, z2 - oz
    clamp = lambda v: max(0, min(16, v))
    x1, x2, y1, y2, z1, z2 = map(clamp, (x1, x2, y1, y2, z1, z2))
    return [_r(v) for v in {
        "down": [x1, 16 - z2, x2, 16 - z1],
        "up": [x1, z1, x2, z2],
        "north": [16 - x2, 16 - y2, 16 - x1, 16 - y1],
        "south": [x1, 16 - y2, x2, 16 - y1],
        "west": [z1, 16 - y2, z2, 16 - y1],
        "east": [16 - z2, 16 - y2, 16 - z1, 16 - y1],
    }[face]]


def cull(face, f, t):
    return {"down": f[1] == 0, "up": t[1] == 16, "north": f[2] == 0,
            "south": t[2] == 16, "west": f[0] == 0, "east": t[0] == 16}[face]


class Box:
    def __init__(self, f, t, tex, faces=None, glow=False, skip=(), uvs=None, tint=False, rot=None, rotation=None):
        self.f, self.t, self.tex, self.faces, self.glow, self.skip = list(f), list(t), tex, faces or {}, glow, set(skip)
        self.uvs = uvs or {}
        self.tint = tint
        self.rot = rot or {}  # texture rotation per face (0, 90, 180, 270)
        self.rotation = rotation  # element rotation: {"origin": [x, y, z], "axis": "x", "angle": 22.5}

    def moved(self, dx=0, dy=0, dz=0):
        b = Box(self.f, self.t, self.tex, self.faces, self.glow, self.skip, self.uvs, self.tint, self.rot, self.rotation)
        b.f = [self.f[0] + dx, self.f[1] + dy, self.f[2] + dz]
        b.t = [self.t[0] + dx, self.t[1] + dy, self.t[2] + dz]
        b._uv_from = (self.f, self.t)
        return b

    def json(self, allow_cull=True):
        uf, ut = getattr(self, "_uv_from", (self.f, self.t))
        faces = {}
        for face in FACES:
            if face in self.skip:
                continue
            tex = self.faces.get(face, self.tex)
            entry = {"uv": self.uvs.get(face) or uv(face, uf, ut), "texture": tex}
            if allow_cull and cull(face, self.f, self.t):
                entry["cullface"] = face
            if self.tint:
                entry["tintindex"] = 0
            elif tex in STAINABLE:
                entry["tintindex"] = 1
            if self.rot.get(face):
                entry["rotation"] = self.rot[face]
            faces[face] = entry
        el = {"from": [_r(v) for v in self.f], "to": [_r(v) for v in self.t], "faces": faces}
        if self.rotation:
            el["rotation"] = self.rotation
        if self.f[2] == self.t[2] or self.f[0] == self.t[0]:
            el["shade"] = False
        if self.glow:
            el["shade"] = False
            el["neoforge_data"] = {"block_light": 15, "sky_light": 15}
        return el


TALL_ITEM_DISPLAY = {
    "gui": {"rotation": [30, 225, 0], "translation": [0, -3.5, 0], "scale": [0.42, 0.42, 0.42]},
    "ground": {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.18, 0.18, 0.18]},
    "fixed": {"rotation": [0, 0, 0], "translation": [0, -4, 0], "scale": [0.4, 0.4, 0.4]},
    "thirdperson_righthand": {"rotation": [75, 45, 0], "translation": [0, 1, 0], "scale": [0.25, 0.25, 0.25]},
    "firstperson_righthand": {"rotation": [0, 45, 0], "translation": [0, -2, 0], "scale": [0.3, 0.3, 0.3]},
    "firstperson_lefthand": {"rotation": [0, 225, 0], "translation": [0, -2, 0], "scale": [0.3, 0.3, 0.3]},
}


def write(room, name, boxes, textures=None, display=None, cull=True, particle="#planks", render_type=None):
    model = {"parent": "minecraft:block/block", "textures": {"particle": particle}}
    if render_type:
        model["render_type"] = render_type
    if textures:
        model["textures"].update(textures)
    if display:
        model["display"] = display
    model["elements"] = [b.json(cull) for b in boxes]
    path = os.path.join(OUT, room, name + ".json")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w") as fh:
        json.dump(model, fh, indent=2)
        fh.write("\n")


def write_tall(room, name, lower, upper, textures=None, particle="#planks", render_type=None):
    write(room, name + "_lower", lower, textures, particle=particle, render_type=render_type)
    write(room, name + "_upper", upper, textures, particle=particle, render_type=render_type)
    write(room, name + "_item", lower + [b.moved(dy=16) for b in upper], textures,
          display=TALL_ITEM_DISPLAY, cull=False, particle=particle, render_type=render_type)


# ---- Doors and drawers ------------------------------------------------------------------

def door(x1, x2, y1, y2, zf, zb, tex, opened=False, hinge="left"):
    """A door on the front of a cabinet. Closed: x1..x2 at depth zf..zb.
    Open: swung 90 degrees outward around the hinge side."""
    if not opened:
        return [Box((x1, y1, zf), (x2, y2, zb), tex)]
    t, w = zb - zf, x2 - x1
    if hinge == "left":
        return [Box((x1, y1, zf - w), (x1 + t, y2, zf), tex)]
    return [Box((x2 - t, y1, zf - w), (x2, y2, zf), tex)]


def door_handle(x1, x2, y1, y2, zf, zb, door_x1, door_x2, door_zf, tex, opened=False, hinge="left"):
    """Handle on a door (given the door's x range and front z). Moves with the door when open."""
    if not opened:
        return [Box((x1, y1, zf), (x2, y2, zb), tex)]
    w = door_x2 - door_x1
    depth = zb - zf
    if hinge == "left":
        # Free edge of the door is now far out in front; the handle sits on the door's outer (west) face.
        far = door_zf - w + 1
        return [Box((door_x1 - depth, y1, far), (door_x1, y2, far + (x2 - x1)), tex)]
    far = door_zf - w + 1
    return [Box((door_x2, y1, far), (door_x2 + depth, y2, far + (x2 - x1)), tex)]


def drawer(x1, x2, y1, y2, zf, zb, front_tex, tray_tex, opened=False, pull=5.0):
    """A drawer front at depth zf..zb. Open: pulled out by `pull` pixels with the tray showing."""
    if not opened:
        return [Box((x1, y1, zf), (x2, y2, zb), front_tex)]
    return [
        Box((x1, y1, zf - pull), (x2, y2, zb - pull), front_tex),
        Box((x1 + 0.5, y1 + 0.5, zb - pull), (x2 - 0.5, y2 - 1, zb), tray_tex),
    ]


# ---- Rotations (for pieces with parts on their sides) -----------------------------------

def rotate_ccw(boxes):
    """Rotate boxes 90 degrees counter-clockwise seen from above (north side becomes west side)."""
    out = []
    for b in boxes:
        nb = Box((b.f[2], b.f[1], 16 - b.t[0]), (b.t[2], b.t[1], 16 - b.f[0]), b.tex,
                 {_ccw_face(k): v for k, v in b.faces.items()}, b.glow, {_ccw_face(k) for k in b.skip})
        out.append(nb)
    return out


def rotate_cw(boxes):
    """Rotate boxes 90 degrees clockwise seen from above (north side becomes east side)."""
    out = []
    for b in boxes:
        nb = Box((16 - b.t[2], b.f[1], b.f[0]), (16 - b.f[2], b.t[1], b.t[0]), b.tex,
                 {_cw_face(k): v for k, v in b.faces.items()}, b.glow, {_cw_face(k) for k in b.skip})
        out.append(nb)
    return out


_CCW = {"north": "west", "west": "south", "south": "east", "east": "north"}
_CW = {v: k for k, v in _CCW.items()}


def _ccw_face(face):
    return _CCW.get(face, face)


def _cw_face(face):
    return _CW.get(face, face)
