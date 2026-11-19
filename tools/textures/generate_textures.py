"""Draws the mod's own textures (16x16 pixel art) with Pillow.

Usage: python3 tools/textures/generate_textures.py
Writes PNGs (and .mcmeta files for animated ones) into src/main/resources/assets/reikos_furniture/textures/.
Everything is drawn from code with a fixed random seed, so running it again gives the same images.
"""
import json
import os
import random

from PIL import Image

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "src", "main", "resources",
                    "assets", "reikos_furniture", "textures")

# Dye colors (the vanilla dye "diffuse" colors), used for fabric.
DYES = {
    "white": (0xE9, 0xEC, 0xEC), "orange": (0xF0, 0x76, 0x13), "magenta": (0xBD, 0x44, 0xB3),
    "light_blue": (0x3A, 0xAF, 0xD9), "yellow": (0xF8, 0xC6, 0x27), "lime": (0x70, 0xB9, 0x19),
    "pink": (0xED, 0x8D, 0xAC), "gray": (0x3E, 0x44, 0x47), "light_gray": (0x8E, 0x8E, 0x86),
    "cyan": (0x15, 0x89, 0x91), "purple": (0x79, 0x2A, 0xAC), "blue": (0x35, 0x39, 0x9D),
    "brown": (0x72, 0x47, 0x28), "green": (0x54, 0x6D, 0x1B), "red": (0xA1, 0x27, 0x22),
    "black": (0x14, 0x15, 0x19),
}


def clamp(v):
    return max(0, min(255, int(round(v))))


def shade(rgb, f):
    return tuple(clamp(c * f) for c in rgb)


def mix(a, b, t):
    return tuple(clamp(a[i] + (b[i] - a[i]) * t) for i in range(3))


def save(img, path, frametime=None):
    full = os.path.join(ROOT, path)
    os.makedirs(os.path.dirname(full), exist_ok=True)
    img.save(full)
    if frametime is not None:
        with open(full + ".mcmeta", "w") as fh:
            json.dump({"animation": {"frametime": frametime}}, fh, indent=2)
            fh.write("\n")


def new(w=16, h=16, color=(0, 0, 0)):
    return Image.new("RGBA", (w, h), color + (255,))


# ---- Fabric -------------------------------------------------------------------------------

def fabric(name, base):
    """Woven upholstery: a fine basket weave with soft noise."""
    rnd = random.Random("fabric-" + name)
    img = new()
    px = img.load()
    for y in range(16):
        for x in range(16):
            weave = 1.04 if ((x // 2) + (y // 2)) % 2 == 0 else 0.96
            thread = 1.02 if (x + y) % 2 == 0 else 0.99
            noise = 1 + rnd.uniform(-0.035, 0.035)
            px[x, y] = shade(base, weave * thread * noise) + (255,)
    save(img, f"block/fabric/{name}.png")


# ---- Metal and appliances -------------------------------------------------------------------

STEEL = (0xB9, 0xBE, 0xC2)
WHITE = (0xEE, 0xEF, 0xEF)
DARK = (0x22, 0x24, 0x27)


def brushed(rnd, base, img=None, box=(0, 0, 16, 16)):
    img = img or new()
    px = img.load()
    x0, y0, x1, y1 = box
    for y in range(y0, y1):
        row = 1 + rnd.uniform(-0.03, 0.03)
        for x in range(x0, x1):
            px[x, y] = shade(base, row * (1 + rnd.uniform(-0.02, 0.02))) + (255,)
    return img


def rect(img, x0, y0, x1, y1, color):
    """Fills a rectangle, clipped to the image."""
    px = img.load()
    w, h = img.size
    for y in range(max(0, y0), min(h, y1)):
        for x in range(max(0, x0), min(w, x1)):
            px[x, y] = color + (255,)


def frame(img, x0, y0, x1, y1, color):
    for x in range(x0, x1):
        img.load()[x, y0] = color + (255,)
        img.load()[x, y1 - 1] = color + (255,)
    for y in range(y0, y1):
        img.load()[x0, y] = color + (255,)
        img.load()[x1 - 1, y] = color + (255,)


def appliance_textures():
    rnd = random.Random("appliances")
    # Brushed steel for appliance sides and tops.
    save(brushed(rnd, STEEL), "block/appliance/steel.png")

    # Fridge: white enamel body and a door with a raised panel edge.
    body = new(color=WHITE)
    for y in range(16):
        for x in range(16):
            body.load()[x, y] = shade(WHITE, 1 + rnd.uniform(-0.015, 0.015)) + (255,)
    save(body, "block/appliance/fridge_body.png")
    door = body.copy()
    frame(door, 0, 0, 16, 16, shade(WHITE, 0.88))
    for x in range(1, 15):
        door.load()[x, 1] = shade(WHITE, 1.04) + (255,)
    save(door, "block/appliance/fridge_door.png")

    # Oven front: steel with knobs along the top and a dark glass window; glows orange when on.
    for on in (False, True):
        img = brushed(random.Random("oven"), STEEL)
        rect(img, 0, 0, 16, 3, shade(STEEL, 0.8))
        for kx in (2, 5, 10, 13):
            rect(img, kx, 1, kx + 1, 2, DARK)
        rect(img, 2, 4, 14, 5, shade(STEEL, 1.15))      # handle
        frame(img, 2, 6, 14, 15, shade(STEEL, 0.7))
        for y in range(7, 14):
            for x in range(3, 13):
                if on:
                    d = abs(x - 7.5) / 5 + abs(y - 10) / 4
                    c = mix((0xFF, 0xB0, 0x40), (0x8A, 0x2A, 0x08), min(1, d))
                else:
                    c = shade(DARK, 1 + 0.25 * (1 if x - y == -4 else 0))
                img.load()[x, y] = c + (255,)
        save(img, f"block/appliance/oven_front{'_on' if on else ''}.png")

    # Microwave front: window on the left, keypad on the right.
    for on in (False, True):
        img = new(color=WHITE)
        frame(img, 0, 0, 16, 16, shade(WHITE, 0.9))
        for y in range(2, 14):
            for x in range(1, 11):
                if on:
                    d = ((x - 5.5) ** 2 + (y - 8) ** 2) ** 0.5 / 7
                    c = mix((0xFF, 0xE0, 0x8A), (0xB0, 0x80, 0x30), min(1, d))
                else:
                    c = shade(DARK, 1.2 if (x + y) % 7 == 0 else 1.0)
                img.load()[x, y] = c + (255,)
        rect(img, 11, 2, 15, 14, (0x5A, 0x5E, 0x63))
        rect(img, 11, 3, 15, 5, (0x1A, 0x40, 0x20) if not on else (0x30, 0xE0, 0x60))   # display
        for ky in (6, 8, 10, 12):
            for kx in (12, 14):
                img.load()[kx, ky] = (0xD0, 0xD0, 0xD0, 255)
        save(img, f"block/appliance/microwave_front{'_on' if on else ''}.png")

    # Washing machine and dryer fronts: round door with a control strip on top.
    for kind, glass in (("washing_machine", (0x5A, 0x8F, 0xC8)), ("dryer", (0x55, 0x58, 0x5C))):
        for on in (False, True):
            img = new(color=WHITE)
            rect(img, 0, 0, 16, 3, shade(WHITE, 0.9))
            rect(img, 2, 1, 3, 2, (0x30, 0xD0, 0x60) if on else (0x40, 0x40, 0x40))
            rect(img, 11, 1, 14, 2, DARK)
            for y in range(3, 16):
                for x in range(16):
                    d = ((x - 7.5) ** 2 + (y - 9.5) ** 2) ** 0.5
                    if d < 5.8:
                        if d > 4.6:
                            c = shade(STEEL, 0.9)
                        else:
                            swirl = (x * 3 + y * 5) % 7 < 3
                            c = shade(glass, 1.15 if (on and swirl) else 0.95)
                        img.load()[x, y] = c + (255,)
            save(img, f"block/appliance/{kind}_front{'_on' if on else ''}.png")


# ---- Screens (animated) ----------------------------------------------------------------------

def frames(n, draw):
    img = Image.new("RGBA", (16, 16 * n), (0, 0, 0, 255))
    for i in range(n):
        f = draw(i)
        img.paste(f, (0, 16 * i))
    return img


def tv_channels():
    # Channel 1: a meadow with a drifting cloud and the sun.
    def nature(i):
        f = new(color=(0x7E, 0xC0, 0xF0))
        rect(f, 0, 11, 16, 16, (0x4C, 0x9A, 0x2A))
        rect(f, 0, 11, 16, 12, (0x6A, 0xB8, 0x3A))
        rect(f, 12, 2, 15, 5, (0xFF, 0xE2, 0x5A))
        cx = (i * 2) % 22 - 6
        for dx, dy in ((0, 0), (1, 0), (2, 0), (3, 0), (1, -1), (2, -1)):
            if 0 <= cx + dx < 16:
                f.load()[cx + dx, 5 + dy] = (0xFF, 0xFF, 0xFF, 255)
        return f
    save(frames(11, nature), "block/screen/tv_channel_1.png", frametime=6)

    # Channel 2: news with a scrolling ticker.
    def news(i):
        f = new(color=(0x1E, 0x3C, 0x78))
        rect(f, 6, 3, 10, 7, (0xE8, 0xB8, 0x90))          # head
        rect(f, 4, 7, 12, 11, (0x30, 0x30, 0x38))         # suit
        rect(f, 7, 7, 9, 9, (0xEE, 0xEE, 0xEE))
        rect(f, 0, 12, 16, 16, (0xC0, 0x20, 0x20))
        for x in range(16):
            if (x + i) % 4 != 0:
                f.load()[x, 14] = (0xFF, 0xFF, 0xFF, 255)
        return f
    save(frames(4, news), "block/screen/tv_channel_2.png", frametime=5)

    # Channel 3: a cartoon ball bouncing.
    heights = [11, 8, 6, 5, 6, 8, 11, 12]
    def cartoon(i):
        f = new(color=(0xFF, 0xF0, 0xB0))
        rect(f, 0, 14, 16, 16, (0xF0, 0x80, 0x30))
        x = 2 + i
        y = heights[i % len(heights)]
        rect(f, x, y, x + 3, y + 3, (0xE0, 0x30, 0x60))
        f.load()[x, y] = (0xFF, 0xA0, 0xC0, 255)
        return f
    save(frames(8, cartoon), "block/screen/tv_channel_3.png", frametime=3)

    # Channel 4: static.
    def static(i):
        rnd = random.Random(f"static-{i}")
        f = new()
        for y in range(16):
            for x in range(16):
                v = rnd.randint(30, 230)
                f.load()[x, y] = (v, v, v, 255)
        return f
    save(frames(4, static), "block/screen/tv_channel_4.png", frametime=1)


def computer_screen():
    # A desktop with a window and a blinking cursor.
    def desktop(i):
        f = new(color=(0x2E, 0x6F, 0xC0))
        rect(f, 0, 14, 16, 16, (0x20, 0x20, 0x28))
        rect(f, 1, 14, 3, 16, (0x40, 0xA0, 0x40))
        rect(f, 3, 3, 13, 11, (0xF4, 0xF4, 0xF4))
        rect(f, 3, 3, 13, 4, (0x40, 0x40, 0x50))
        for row, length in ((5, 7), (7, 5), (9, 6 if i % 2 == 0 else 3)):
            rect(f, 4, row, 4 + length, row + 1, (0x50, 0x50, 0x58))
        if i % 2 == 0:
            rect(f, 4 + 6, 9, 4 + 7, 10, (0x10, 0x10, 0x10))
        return f
    save(frames(2, desktop), "block/screen/computer.png", frametime=10)


def arcade_screen():
    # Little aliens marching and a ship firing.
    def game(i):
        f = new(color=(0x05, 0x05, 0x10))
        ox = [0, 1, 2, 1][i % 4]
        for row, color in ((2, (0xF0, 0x40, 0x60)), (5, (0x40, 0xE0, 0x60))):
            for col in range(4):
                x = 1 + ox + col * 4
                rect(f, x, row, x + 2, row + 2, color)
        sx = 6 + [0, 1, 2, 1][i % 4]
        rect(f, sx, 14, sx + 3, 15, (0x60, 0xC0, 0xFF))
        f.load()[sx + 1, 13] = (0x60, 0xC0, 0xFF, 255)
        by = 12 - (i % 4) * 2
        f.load()[sx + 1, by] = (0xFF, 0xFF, 0x80, 255)
        return f
    save(frames(4, game), "block/screen/arcade.png", frametime=4)


# ---- Food and drink items ------------------------------------------------------------------

def item_canvas():
    return Image.new("RGBA", (16, 16), (0, 0, 0, 0))


def mug(name, drink, foam=None):
    """A white mug (handle on the right) filled with a drink."""
    img = item_canvas()
    body = (0xF2, 0xF2, 0xEE)
    edge = (0x9A, 0x9A, 0x96)
    rect(img, 3, 5, 11, 14, body)
    for y in range(5, 14):
        img.load()[3, y] = edge + (255,)
        img.load()[10, y] = shade(body, 0.85) + (255,)
    rect(img, 4, 13, 10, 14, shade(body, 0.85))
    rect(img, 3, 4, 11, 5, edge)
    rect(img, 4, 5, 10, 7, drink)
    if foam:
        rect(img, 5, 5, 9, 6, foam)
    for y in (7, 8, 9, 10):
        img.load()[12, y] = edge + (255,)
    img.load()[11, 7] = edge + (255,)
    img.load()[11, 10] = edge + (255,)
    # steam
    for x, y in ((5, 2), (6, 1), (8, 2), (9, 1)):
        img.load()[x, y] = (0xDD, 0xDD, 0xDD, 160)
    img.save(os.path.join(ROOT, "item", name + ".png"))


def toast():
    img = item_canvas()
    crust = (0x9A, 0x5A, 0x22)
    inside = (0xE0, 0xA8, 0x55)
    rect(img, 3, 3, 13, 14, crust)
    rect(img, 4, 4, 12, 13, inside)
    rect(img, 4, 2, 12, 3, crust)
    rect(img, 5, 1, 11, 2, crust)
    rnd = random.Random("toast")
    for _ in range(10):
        x, y = rnd.randint(4, 11), rnd.randint(4, 12)
        img.load()[x, y] = shade(inside, 0.85) + (255,)
    img.save(os.path.join(ROOT, "item", "toast.png"))


def smoothie():
    """A glass with a pink smoothie, a straw and a slice of fruit."""
    img = item_canvas()
    glass = (0xD8, 0xEE, 0xF2)
    pink = (0xE8, 0x6A, 0x9A)
    for y in range(4, 15):
        inset = 0 if y < 13 else 1
        for x in range(4 + inset, 12 - inset):
            img.load()[x, y] = (pink if y >= 6 else glass) + (255,)
        img.load()[4 + inset, y] = glass + (255,)
        img.load()[11 - inset, y] = glass + (255,)
    for y in range(6, 15):
        img.load()[6, y] = shade(pink, 1.15) + (255,)
    for i, (x, y) in enumerate(((9, 1), (9, 2), (9, 3), (9, 4), (9, 5))):
        img.load()[x, y] = (0x40, 0xB0, 0xE0, 255)
    rect(img, 10, 3, 13, 5, (0x7C, 0xC8, 0x3C))
    img.load()[11, 3] = (0xF4, 0xE9, 0x8C, 255)
    img.save(os.path.join(ROOT, "item", "fruit_smoothie.png"))


def food_items():
    os.makedirs(os.path.join(ROOT, "item"), exist_ok=True)
    mug("coffee", (0x4A, 0x2A, 0x14), foam=(0xB0, 0x80, 0x50))
    mug("hot_cocoa", (0x6B, 0x3A, 0x1E), foam=(0xFA, 0xF5, 0xEC))
    toast()
    smoothie()


def dartboard():
    """Classic dartboard rings: black/cream sectors, red/green rings, bullseye."""
    img = new(color=(0x18, 0x18, 0x18))
    import math
    for y in range(16):
        for x in range(16):
            dx, dy = x - 7.5, y - 7.5
            r = (dx * dx + dy * dy) ** 0.5
            sector = int((math.atan2(dy, dx) + math.pi) / (2 * math.pi) * 10) % 2
            if r > 7.6:
                c = (0x18, 0x18, 0x18)
            elif r > 6.6:
                c = (0xC8, 0x20, 0x20) if sector else (0x20, 0x90, 0x30)
            elif r > 4.2:
                c = (0x10, 0x10, 0x10) if sector else (0xF0, 0xE0, 0xC0)
            elif r > 3.3:
                c = (0xC8, 0x20, 0x20) if sector else (0x20, 0x90, 0x30)
            elif r > 1.4:
                c = (0x10, 0x10, 0x10) if sector else (0xF0, 0xE0, 0xC0)
            elif r > 0.8:
                c = (0x20, 0x90, 0x30)
            else:
                c = (0xC8, 0x20, 0x20)
            img.load()[x, y] = c + (255,)
    save(img, "block/dartboard.png")


def piano_keys():
    """Piano keyboard seen from above: white keys with black keys at the back."""
    img = new(color=(0xF4, 0xF4, 0xF0))
    for x in range(0, 16, 2):
        for y in range(16):
            img.load()[x, y] = (0xC8, 0xC8, 0xC2, 255)
    for x in (1, 3, 7, 9, 11, 15):
        for y in range(0, 9):
            img.load()[x, y] = (0x15, 0x15, 0x18, 255)
    save(img, "block/piano_keys.png")


def villager_profession():
    """Furniture maker work clothes, drawn over the villager (64x64, same layout as vanilla professions):
    a leather apron with a pocket and a ruler, a bib strap, and a pencil behind the ear."""
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    px = img.load()
    rnd = random.Random("furniture-maker")
    leather = (0xC4, 0x9A, 0x62)  # tan canvas, stands out on the brown plains robe

    def put(x, y, c):
        px[x, y] = c + (255,)

    def leather_px(x, y):
        put(x, y, shade(leather, 0.92 + rnd.random() * 0.16))

    # Robe (jacket box at 0,38: 8 wide, 20 tall, 6 deep). Front face is x 6-13, y 44-63.
    for y in range(47, 62):
        for x in range(6, 14):
            leather_px(x, y)
    for x in range(6, 14):  # stitched hem
        put(x, 61, shade(leather, 0.7))
    for y in range(44, 47):  # bib above the waist
        for x in range(8, 12):
            leather_px(x, y)
    # Waist ties around the sides and back.
    for x in list(range(0, 6)) + list(range(14, 28)):
        put(x, 47, shade(leather, 0.75))
    # Neck strap on the robe's top face (x 6-13, y 38-43).
    for y in range(38, 44):
        put(8, y, shade(leather, 0.8))
        put(11, y, shade(leather, 0.8))
    # Pocket with a folding ruler and a pencil sticking out.
    for x in range(7, 13):
        put(x, 53, shade(leather, 0.65))
    for y in range(53, 58):
        put(7, y, shade(leather, 0.65))
        put(12, y, shade(leather, 0.65))
    for x in range(7, 13):
        put(x, 57, shade(leather, 0.65))
    for y in range(50, 53):
        put(9, y, (0xE8, 0xC0, 0x40))
    put(9, 50, (0x30, 0x30, 0x30))
    for y in range(51, 53):
        put(11, y, (0xD8, 0xD8, 0xD0))

    # Pencil behind the right ear: on the hat layer's side face (x 32-39, y 8-17).
    for x in range(33, 39):
        put(x, 12, (0xF0, 0xC8, 0x30))
    put(33, 12, (0xF0, 0xD8, 0xA8))
    put(32, 12, (0x30, 0x30, 0x30))

    save(img, "entity/villager/profession/furniture_maker.png")
    save(img, "entity/zombie_villager/profession/furniture_maker.png")


# ---- Decor: wallpaper and tiles ---------------------------------------------------------------

def wallpaper(name, dye):
    """Damask-style wallpaper: a soft diamond lattice with a small motif in each diamond, tileable."""
    rnd = random.Random("wallpaper-" + name)
    base = mix(dye, (0xF4, 0xF0, 0xE6), 0.35) if sum(dye) < 600 else mix(dye, (0xE0, 0xDC, 0xD0), 0.25)
    dark = shade(base, 0.82)
    light = mix(base, (255, 255, 255), 0.25)
    img = new()
    px = img.load()
    for y in range(16):
        for x in range(16):
            c = shade(base, 0.97 + rnd.random() * 0.06)
            if (x + y) % 8 == 0 or (x - y) % 8 == 0:
                c = dark
            px[x, y] = c + (255,)
    # Motif: a small cross in the middle of each diamond.
    for cx, cy in ((4, 0), (12, 8), (4, 8), (12, 0)):
        for dx, dy in ((0, 0), (1, 0), (-1, 0), (0, 1), (0, -1)):
            px[(cx + dx) % 16, (cy + 4 + dy) % 16] = light + (255,)
    # Paper grain: faint vertical stripes.
    for x in range(0, 16, 4):
        for y in range(16):
            r, g, b, a = px[x, y]
            px[x, y] = shade((r, g, b), 0.98) + (255,)
    save(img, f"block/wallpaper/{name}.png")


def tiles():
    """Eight tile blocks: square, checkered, small, subway, hexagon and marble patterns."""
    import math
    grout_light, grout_dark = (0xC8, 0xC6, 0xC0), (0x50, 0x50, 0x54)

    def squares(name, face, grout, size=8, glaze=0.06):
        rnd = random.Random("tiles-" + name)
        img = new()
        px = img.load()
        for y in range(16):
            for x in range(16):
                if x % size == 0 or y % size == 0:
                    c = grout
                else:
                    c = shade(face, 1 - glaze + rnd.random() * glaze)
                    if x % size == 1 or y % size == 1:
                        c = mix(c, (255, 255, 255), 0.18)
                px[x, y] = c + (255,)
        return img

    save(squares("white", (0xF2, 0xF2, 0xEE), grout_light), "block/tiles/white.png")
    save(squares("black", (0x22, 0x22, 0x26), grout_dark), "block/tiles/black.png")
    save(squares("blue", (0x2E, 0x5C, 0xB8), (0xE8, 0xE8, 0xE4), size=4), "block/tiles/blue.png")
    save(squares("terracotta", (0xB8, 0x62, 0x3C), (0xD8, 0xC8, 0xB0)), "block/tiles/terracotta.png")

    # Checkered: 8x8 black and white squares.
    img = new()
    px = img.load()
    for y in range(16):
        for x in range(16):
            white = ((x // 8) + (y // 8)) % 2 == 0
            c = (0xEE, 0xEE, 0xEA) if white else (0x24, 0x24, 0x28)
            px[x, y] = c + (255,)
    save(img, "block/tiles/checkered.png")

    # Green subway tiles: 8x4 bricks, every other row offset by 4.
    rnd = random.Random("tiles-green")
    img = new()
    px = img.load()
    green = (0x3E, 0x8E, 0x5A)
    for y in range(16):
        for x in range(16):
            off = 4 if (y // 4) % 2 else 0
            if y % 4 == 0 or (x + off) % 8 == 0:
                c = (0xDA, 0xDA, 0xD4)
            else:
                c = shade(green, 0.94 + rnd.random() * 0.08)
                if y % 4 == 1:
                    c = mix(c, (255, 255, 255), 0.2)
            px[x, y] = c + (255,)
    save(img, "block/tiles/green.png")

    # Hexagon mosaic: hexagons around a grid of centers (rows offset by half a tile), grout on the cell borders.
    img = new()
    px = img.load()
    centers = [(cx + 16 * i, cy + 16 * j) for i in (-1, 0, 1) for j in (-1, 0, 1)
               for cx, cy in ((4, 4), (12, 4), (0, 12), (8, 12), (16, 12))]
    for y in range(16):
        for x in range(16):
            # Squashing y by 0.866 makes the offset grid a regular hexagonal lattice, whose nearest-center
            # cells are hexagons.
            d = sorted(math.hypot(x + 0.5 - cx, (y + 0.5 - cy) * 0.866) for cx, cy in centers)
            c = (0xC4, 0xC4, 0xC0) if d[1] - d[0] < 0.7 else (0xF6, 0xF6, 0xF2)
            px[x, y] = c + (255,)
    save(img, "block/tiles/hexagon.png")

    # Marble: white with gray veins from tileable sine waves.
    rnd = random.Random("tiles-marble")
    img = new()
    px = img.load()
    for y in range(16):
        for x in range(16):
            t = math.sin(2 * math.pi * (x / 16 + 2 * y / 16) + 1.7 * math.sin(2 * math.pi * (2 * x / 16 - y / 16)))
            v = abs(t)
            c = shade((0xEE, 0xEC, 0xE8), 0.97 + rnd.random() * 0.04)
            if v < 0.12:
                c = (0x8C, 0x8A, 0x8C)
            elif v < 0.3:
                c = mix(c, (0xB4, 0xB2, 0xB4), 0.6)
            px[x, y] = c + (255,)
    save(img, "block/tiles/marble.png")


def window_glass():
    """Clear glass for windows and glass doors: faint blue tint, two diagonal highlights, no border."""
    img = Image.new("RGBA", (16, 16), (0xD6, 0xEA, 0xF2, 70))
    px = img.load()
    for i in range(16):
        for x, y, a in ((i, 11 - i, 150), (i + 1, 11 - i, 110), (i, 15 - i, 120)):
            if 0 <= x < 16 and 0 <= y < 16:
                px[x, y] = (0xF4, 0xFA, 0xFF, a)
    save(img, "block/window_glass.png")


def tool_items():
    """Furniture hammer, and the moving dolly empty and loaded with a crate."""
    wood, wood_dark = (0x9A, 0x6A, 0x3A), (0x6E, 0x48, 0x24)
    iron, iron_dark, iron_light = (0xC8, 0xC8, 0xCC), (0x80, 0x80, 0x88), (0xEE, 0xEE, 0xF2)

    def put(img, x, y, c):
        if 0 <= x < 16 and 0 <= y < 16:
            img.load()[x, y] = c + (255,)

    # Hammer: diagonal handle from bottom left, head at the top right.
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for i in range(10):
        put(img, 2 + i, 13 - i, wood if i % 3 else wood_dark)
        put(img, 3 + i, 13 - i, wood_dark)
    for x, y in ((9, 1), (10, 2), (11, 3), (12, 4), (13, 5), (8, 2), (9, 3), (10, 4), (11, 5), (12, 6), (14, 6), (13, 7)):
        put(img, x, y, iron)
    for x, y in ((10, 1), (11, 2), (12, 3), (13, 4), (14, 5)):
        put(img, x, y, iron_light)
    for x, y in ((8, 3), (9, 4), (10, 5), (11, 6), (12, 7)):
        put(img, x, y, iron_dark)
    save(img, "item/hammer.png")

    def dolly(loaded):
        img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
        for y in range(1, 14):  # the upright frame
            put(img, 4, y, iron_dark)
            put(img, 5, y, iron)
        put(img, 6, 1, iron)
        put(img, 7, 1, iron_dark)
        for x in range(4, 13):  # toe plate
            put(img, x, 13, iron_dark)
            put(img, x, 12, iron)
        for x, y in ((3, 14), (4, 14), (5, 14), (3, 15), (4, 15), (5, 15)):  # wheel
            put(img, x, y, (0x30, 0x30, 0x34))
        put(img, 4, 14, (0x60, 0x60, 0x66))
        if loaded:
            for y in range(4, 12):
                for x in range(6, 14):
                    edge = x in (6, 13) or y in (4, 11)
                    put(img, x, y, wood_dark if edge else wood)
            for x in range(7, 13):
                put(img, x, 7, wood_dark)
        return img

    save(dolly(False), "item/moving_dolly.png")
    save(dolly(True), "item/moving_dolly_loaded.png")


def watering_can():
    """A green watering can: round body, a carrying handle on top and a long spout with a rose."""
    green, green_dark, green_light = (0x4C, 0x9A, 0x3C), (0x2F, 0x6A, 0x26), (0x7C, 0xC4, 0x5E)
    rose, rose_dark = (0xC8, 0xC8, 0xCC), (0x80, 0x80, 0x88)
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()

    def put(x, y, c):
        if 0 <= x < 16 and 0 <= y < 16:
            px[x, y] = c + (255,)

    for y in range(7, 15):  # body, with the corners cut
        for x in range(6, 15):
            if (x, y) in ((6, 7), (14, 7), (6, 14), (14, 14)):
                continue
            c = green_light if x == 7 else green_dark if x >= 13 or y == 14 else green
            put(x, y, c)
    for x in range(7, 14):  # top rim
        put(x, 6, green_dark)
    for x, y in ((8, 5), (8, 4), (9, 3), (10, 3), (11, 3), (12, 4), (12, 5)):  # handle
        put(x, y, green_dark)
    for x, y in ((9, 4), (10, 4), (11, 4)):
        put(x, y, green)
    for i in range(5):  # spout, from the bottom of the body up to the left
        put(5 - i, 11 - i, green)
        put(5 - i, 12 - i, green_dark)
    for x, y in ((0, 5), (1, 5), (0, 6), (1, 6), (0, 7), (1, 4), (2, 5)):  # the rose
        put(x, y, rose)
    for x, y in ((0, 4), (1, 7)):
        put(x, y, rose_dark)
    save(img, "item/watering_can.png")


# ---- Paintings ---------------------------------------------------------------------------------

PAINTINGS = {  # name: (width, height) in blocks
    "sunset": (2, 1), "mountains": (2, 1), "meadow": (1, 1), "still_life": (1, 1), "shapes": (1, 1),
    "cat": (1, 1), "city_lights": (4, 2), "lighthouse": (1, 2), "big_wave": (2, 2), "night_hills": (2, 2),
    "fruit_bowl": (2, 1), "sailboat": (1, 1),
}


def paintings():
    """Twelve paintings for the vanilla painting item, each with a thin dark frame."""
    from PIL import ImageDraw
    import math

    def canvas(name):
        w, h = PAINTINGS[name]
        return Image.new("RGBA", (w * 16, h * 16), (0, 0, 0, 255))

    def gradient(img, top, bottom, y0=0, y1=None):
        y1 = img.height if y1 is None else y1
        for y in range(y0, y1):
            c = mix(top, bottom, (y - y0) / max(1, y1 - y0 - 1))
            for x in range(img.width):
                img.load()[x, y] = c + (255,)

    def frame(img):
        d = ImageDraw.Draw(img)
        d.rectangle([0, 0, img.width - 1, img.height - 1], outline=(0x4A, 0x32, 0x1E, 255))

    def done(img, name):
        frame(img)
        save(img, f"painting/{name}.png")

    rnd = random.Random("paintings")

    # Sunset over the sea.
    img = canvas("sunset")
    gradient(img, (0x6A, 0x3C, 0x8C), (0xFF, 0xA8, 0x5A), 0, 10)
    gradient(img, (0x2C, 0x40, 0x78), (0x16, 0x22, 0x48), 10, 16)
    d = ImageDraw.Draw(img)
    d.ellipse([11, 5, 20, 14], fill=(0xFF, 0xE0, 0x8C, 255))
    gradient_sea = [(0x2C, 0x40, 0x78), (0x16, 0x22, 0x48)]
    for y in range(10, 16):
        for x in range(img.width):
            img.load()[x, y] = mix(*gradient_sea, (y - 10) / 5) + (255,)
        for x in range(13, 19):
            if (x + y) % 2 == 0:
                img.load()[x, y] = (0xFF, 0xB8, 0x6A, 255)
    done(img, "sunset")

    # Snowy mountains over a green valley.
    img = canvas("mountains")
    gradient(img, (0x6E, 0xA8, 0xE8), (0xC8, 0xE4, 0xFA), 0, 12)
    d = ImageDraw.Draw(img)
    d.polygon([(0, 13), (8, 3), (15, 13)], fill=(0x7A, 0x7A, 0x8C, 255))
    d.polygon([(10, 13), (20, 2), (31, 13)], fill=(0x8C, 0x8C, 0x9E, 255))
    d.polygon([(6, 5), (8, 3), (10, 5)], fill=(0xF6, 0xF6, 0xFA, 255))
    d.polygon([(17, 5), (20, 2), (23, 5)], fill=(0xF6, 0xF6, 0xFA, 255))
    d.rectangle([0, 12, 31, 15], fill=(0x4E, 0x8A, 0x3A, 255))
    d.line([(0, 12), (31, 12)], fill=(0x62, 0x9E, 0x48, 255))
    d.line([(5, 14), (12, 14)], fill=(0x5A, 0x9A, 0xD8, 255))
    done(img, "mountains")

    # Meadow with flowers.
    img = canvas("meadow")
    gradient(img, (0x8C, 0xC4, 0xFA), (0xD8, 0xEE, 0xFF), 0, 9)
    for x in range(16):
        top = 8 + round(1.5 * math.sin(x / 3))
        for y in range(top, 16):
            img.load()[x, y] = (0x5C if y > top + 2 else 0x6E, 0xA8 if y > top + 2 else 0xB8, 0x48, 255)
    for x, y, c in ((3, 12, (0xE8, 0x40, 0x40)), (7, 13, (0xFF, 0xE0, 0x40)), (11, 11, (0xF8, 0xF8, 0xF8)),
                    (13, 14, (0xE8, 0x40, 0x40)), (5, 14, (0xC8, 0x70, 0xE8))):
        img.load()[x, y] = c + (255,)
    for x in range(4, 8):
        img.load()[x, 3] = (0xFF, 0xFF, 0xFF, 255)
    done(img, "meadow")

    # Still life: a vase of flowers on a table.
    img = canvas("still_life")
    gradient(img, (0xD8, 0xC0, 0x98), (0xC0, 0xA4, 0x7C))
    d = ImageDraw.Draw(img)
    d.rectangle([0, 12, 15, 15], fill=(0x7A, 0x4E, 0x2C, 255))
    d.rectangle([6, 8, 9, 12], fill=(0x3C, 0x6C, 0xC0, 255))
    d.line([(7, 4), (7, 8)], fill=(0x3E, 0x7A, 0x34, 255))
    d.line([(9, 5), (8, 8)], fill=(0x3E, 0x7A, 0x34, 255))
    for x, y, c in ((7, 3, (0xE8, 0x3C, 0x3C)), (6, 4, (0xE8, 0x3C, 0x3C)), (9, 4, (0xFF, 0xD8, 0x30)), (10, 5, (0xFF, 0xD8, 0x30))):
        img.load()[x, y] = c + (255,)
    done(img, "still_life")

    # Shapes: an abstract composition.
    img = canvas("shapes")
    gradient(img, (0xF2, 0xEE, 0xE2), (0xE6, 0xE0, 0xD0))
    d = ImageDraw.Draw(img)
    d.ellipse([2, 2, 8, 8], fill=(0xD8, 0x3A, 0x3A, 255))
    d.polygon([(8, 14), (14, 14), (11, 6)], fill=(0x2E, 0x5C, 0xC0, 255))
    d.rectangle([2, 10, 6, 13], fill=(0xF0, 0xC0, 0x28, 255))
    d.line([(1, 9), (14, 4)], fill=(0x20, 0x20, 0x24, 255))
    done(img, "shapes")

    # Cat portrait.
    img = canvas("cat")
    gradient(img, (0x3A, 0x8C, 0x8C), (0x2A, 0x6A, 0x6E))
    d = ImageDraw.Draw(img)
    orange, dark = (0xE8, 0x8C, 0x34, 255), (0xB8, 0x64, 0x20, 255)
    d.polygon([(3, 6), (4, 2), (7, 5)], fill=orange)
    d.polygon([(12, 6), (11, 2), (8, 5)], fill=orange)
    d.ellipse([3, 4, 12, 13], fill=orange)
    for x, y in ((5, 6), (10, 6), (7, 4), (8, 4)):
        img.load()[x, y] = dark
    img.load()[5, 8] = (0x40, 0xC0, 0x40, 255)
    img.load()[10, 8] = (0x40, 0xC0, 0x40, 255)
    img.load()[7, 10] = (0xF0, 0x90, 0xA0, 255)
    img.load()[8, 10] = (0xF0, 0x90, 0xA0, 255)
    for y in (10, 11):
        img.load()[2, y] = (0xF0, 0xF0, 0xF0, 255)
        img.load()[13, y] = (0xF0, 0xF0, 0xF0, 255)
    done(img, "cat")

    # City lights at night.
    img = canvas("city_lights")
    gradient(img, (0x0C, 0x10, 0x30), (0x2A, 0x24, 0x58), 0, 24)
    gradient(img, (0x14, 0x18, 0x34), (0x0A, 0x0C, 0x1C), 24, 32)
    d = ImageDraw.Draw(img)
    d.ellipse([50, 3, 56, 9], fill=(0xF4, 0xF0, 0xD0, 255))
    x = 1
    while x < 63:
        w = rnd.randint(4, 8)
        top = rnd.randint(8, 18)
        shade_c = rnd.choice([(0x1C, 0x20, 0x38), (0x24, 0x28, 0x44), (0x18, 0x1A, 0x2C)])
        d.rectangle([x, top, min(62, x + w - 1), 24], fill=shade_c + (255,))
        for wy in range(top + 2, 23, 2):
            for wx in range(x + 1, min(62, x + w - 1), 2):
                if rnd.random() < 0.45:
                    img.load()[wx, wy] = (0xFF, 0xD8, 0x6A, 255)
                    if 26 + (24 - wy) // 3 < 31:
                        img.load()[wx, 26 + (24 - wy) // 3] = (0x8C, 0x78, 0x40, 255)
        x += w + rnd.randint(0, 1)
    done(img, "city_lights")

    # Lighthouse on the rocks.
    img = canvas("lighthouse")
    gradient(img, (0x5A, 0x8C, 0xD8), (0xC8, 0xE0, 0xF4), 0, 24)
    gradient(img, (0x2C, 0x5C, 0x9C), (0x1C, 0x3C, 0x6C), 24, 32)
    d = ImageDraw.Draw(img)
    d.polygon([(2, 31), (4, 24), (12, 23), (14, 31)], fill=(0x6C, 0x64, 0x5C, 255))
    for y in range(8, 24):
        for x in range(6, 10):
            stripe = ((y - 8) // 3) % 2 == 0
            img.load()[x, y] = ((0xD8, 0x30, 0x30) if stripe else (0xF4, 0xF4, 0xF0)) + (255,)
    d.rectangle([5, 5, 10, 7], fill=(0x30, 0x30, 0x34, 255))
    d.rectangle([6, 6, 9, 7], fill=(0xFF, 0xE8, 0x70, 255))
    d.polygon([(5, 4), (8, 2), (10, 4)], fill=(0x30, 0x30, 0x34, 255))
    for i in range(1, 5):
        img.load()[10 + i, 6 - i // 2] = (0xFF, 0xF4, 0xB0, 255)
    done(img, "lighthouse")

    # A big curling wave.
    img = canvas("big_wave")
    gradient(img, (0xE8, 0xE2, 0xCC), (0xD8, 0xD2, 0xBC))
    for y in range(32):
        for x in range(32):
            r = math.hypot(x - 18, y - 16)
            a = math.atan2(y - 16, x - 18)
            band = (r + 3 * a) % 6
            if y > 12 + 3 * math.sin(x / 5) and r < 15 or y > 24:
                c = (0x1E, 0x4A, 0x8C) if band < 3 else (0x2E, 0x6C, 0xB0)
                if band < 0.8:
                    c = (0xF0, 0xF6, 0xFA)
                img.load()[x, y] = c + (255,)
    done(img, "big_wave")

    # Night over the hills, with a crescent moon and a lone tree.
    img = canvas("night_hills")
    gradient(img, (0x0A, 0x0E, 0x2C), (0x30, 0x3C, 0x7A), 0, 22)
    d = ImageDraw.Draw(img)
    for _ in range(26):
        x, y = rnd.randint(1, 30), rnd.randint(1, 17)
        img.load()[x, y] = (0xF8, 0xF4, 0xD0, 255) if rnd.random() < 0.7 else (0xA8, 0xB8, 0xF0, 255)
    d.ellipse([20, 3, 27, 10], fill=(0xF4, 0xF0, 0xC8, 255))
    d.ellipse([22, 2, 29, 9], fill=(0x10, 0x16, 0x38, 255))
    for x in range(32):
        top = 21 + round(3 * math.sin(x / 6 + 1))
        for y in range(top, 32):
            img.load()[x, y] = (0x10, 0x22, 0x18, 255) if y < top + 3 else (0x0A, 0x16, 0x10, 255)
    d.line([(8, 21), (8, 15)], fill=(0x0A, 0x10, 0x0C, 255))
    d.ellipse([5, 10, 11, 16], fill=(0x0C, 0x1A, 0x12, 255))
    done(img, "night_hills")

    # A bowl of fruit.
    img = canvas("fruit_bowl")
    gradient(img, (0x6E, 0x3A, 0x34), (0x52, 0x28, 0x26))
    d = ImageDraw.Draw(img)
    d.rectangle([0, 12, 31, 15], fill=(0x8C, 0x5E, 0x34, 255))
    for cx, cy, c in ((11, 8, (0xD8, 0x2C, 0x2C)), (16, 7, (0xF0, 0x98, 0x28)), (21, 8, (0x80, 0xC0, 0x30)), (13, 6, (0xF0, 0x98, 0x28))):
        d.ellipse([cx - 2, cy - 2, cx + 2, cy + 2], fill=c + (255,))
    for gx, gy in ((18, 9), (19, 10), (20, 9), (19, 8)):
        img.load()[gx, gy] = (0x6A, 0x2C, 0x8C, 255)
    d.arc([9, 3, 24, 9], 200, 330, fill=(0xF8, 0xD8, 0x40, 255))
    d.pieslice([8, 5, 24, 16], 0, 180, fill=(0xEE, 0xEE, 0xE6, 255))
    done(img, "fruit_bowl")

    # A sailboat at sea.
    img = canvas("sailboat")
    gradient(img, (0x7C, 0xB4, 0xEC), (0xD0, 0xE8, 0xFA), 0, 10)
    gradient(img, (0x30, 0x6C, 0xB0), (0x1C, 0x48, 0x84), 10, 16)
    d = ImageDraw.Draw(img)
    d.polygon([(8, 2), (8, 10), (3, 10)], fill=(0xF8, 0xF8, 0xF4, 255))
    d.polygon([(9, 4), (9, 10), (12, 10)], fill=(0xE8, 0x48, 0x48, 255))
    d.polygon([(3, 11), (13, 11), (11, 13), (5, 13)], fill=(0x7A, 0x4A, 0x28, 255))
    for x in range(1, 15, 3):
        img.load()[x, 14] = (0x8C, 0xC0, 0xF0, 255)
    done(img, "sailboat")


def garage_and_library():
    """Pegboard (brown board with a grid of holes) and the globe's world map."""
    import math
    rnd = random.Random("pegboard")
    img = new()
    px = img.load()
    for y in range(16):
        for x in range(16):
            c = shade((0xA8, 0x80, 0x56), 0.95 + rnd.random() * 0.08)
            if x % 4 == 2 and y % 4 == 2:
                c = (0x3A, 0x2A, 0x1C)
            px[x, y] = c + (255,)
    save(img, "block/pegboard.png")

    img = new()
    px = img.load()
    for y in range(16):
        for x in range(16):
            # Tileable blobs: land where a sum of sine waves is high.
            v = (math.sin(2 * math.pi * x / 16 * 2 + 1.3) + math.sin(2 * math.pi * y / 16 * 1 + 0.4)
                 + 0.8 * math.sin(2 * math.pi * (x + y) / 16 * 3))
            if v > 0.9:
                c = (0x6E, 0xA0, 0x48) if v < 1.8 else (0xC8, 0xB0, 0x78)
            else:
                c = (0x3A, 0x6C, 0xB8) if (x + y) % 5 else (0x46, 0x7A, 0xC4)
            if y in (0, 15):
                c = (0xEC, 0xF2, 0xF8)
            px[x, y] = c + (255,)
    save(img, "block/globe.png")


def bar_and_nursery():
    """Espresso and latte, the chalkboard (sign texture and item), aquarium water."""
    mug("espresso", (0x2C, 0x16, 0x0A), foam=(0x9A, 0x62, 0x34))
    mug("latte", (0xC4, 0x94, 0x64), foam=(0xF8, 0xF4, 0xEC))

    # Chalkboard, in the vanilla sign texture layout (64x32): the board's front is at (2, 2), 24x12.
    rnd = random.Random("chalkboard")
    slate, wood, wood_dark = (0x26, 0x32, 0x2C), (0x9A, 0x6A, 0x3A), (0x70, 0x4C, 0x28)
    img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
    px = img.load()
    for y in range(0, 14):
        for x in range(0, 52):
            px[x, y] = wood + (255,)
    for (x0, y0, x1, y1) in ((2, 2, 26, 14), (28, 2, 52, 14)):
        for y in range(y0, y1):
            for x in range(x0, x1):
                edge = x in (x0, x1 - 1) or y in (y0, y1 - 1)
                c = wood_dark if edge else shade(slate, 0.92 + rnd.random() * 0.14)
                px[x, y] = c + (255,)
        for _ in range(6):  # chalk dust
            x, y = rnd.randint(x0 + 2, x1 - 3), rnd.randint(y0 + 2, y1 - 3)
            px[x, y] = (0x5A, 0x66, 0x60, 255)
    os.makedirs(os.path.join(ROOT, "entity", "signs"), exist_ok=True)
    save(img, "entity/signs/chalkboard.png")

    item = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    ip = item.load()
    for y in range(2, 13):
        for x in range(1, 15):
            edge = x in (1, 14) or y in (2, 12)
            ip[x, y] = (wood_dark if edge else slate) + (255,)
    for x, y in ((3, 5), (4, 5), (5, 5), (7, 5), (8, 5), (3, 8), (4, 8), (6, 8), (7, 8), (8, 8), (10, 8)):
        ip[x, y] = (0xEE, 0xEE, 0xE6, 255)
    for x, y in ((4, 13), (4, 14), (11, 13), (11, 14)):
        ip[x, y] = wood + (255,)
    save(item, "item/chalkboard.png")

    water = Image.new("RGBA", (16, 16), (0x4A, 0x9C, 0xD8, 110))
    wp = water.load()
    for x in range(16):
        wp[x, 0] = (0xB8, 0xE0, 0xF8, 160)
        wp[x, 1] = (0x7A, 0xC0, 0xEC, 130)
    save(water, "block/aquarium_water.png")


def style_textures():
    """Washi paper for shoji panels and woven tatami for the Japanese style."""
    rnd = random.Random("washi")
    img = new()
    px = img.load()
    paper = (0xF0, 0xEA, 0xDA)
    for y in range(16):
        for x in range(16):
            px[x, y] = shade(paper, 0.97 + rnd.random() * 0.05) + (255,)
    for _ in range(7):  # long paper fibres
        x, y = rnd.randint(0, 15), rnd.randint(0, 15)
        for k in range(rnd.randint(2, 4)):
            px[(x + k) % 16, (y + (k if rnd.random() < 0.4 else 0)) % 16] = shade(paper, 0.91) + (255,)
    save(img, "block/style/washi.png")

    rnd = random.Random("tatami")
    img = new()
    px = img.load()
    straw = (0xBC, 0xB8, 0x74)
    for y in range(16):
        for x in range(16):
            f = 1.04 if y % 2 == 0 else 0.94  # woven rows
            if x % 8 == 0:
                f *= 0.86  # stitching between the rush bundles
            px[x, y] = shade(straw, f * (0.97 + rnd.random() * 0.06)) + (255,)
    save(img, "block/style/tatami.png")


def catalog_item():
    """The furniture catalog: a brown book with pages showing and a little gold chair on the cover."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    cover, cover_dark, spine = (0x8A, 0x55, 0x2E), (0x5E, 0x38, 0x1C), (0x4A, 0x2A, 0x14)
    page, page_edge = (0xF2, 0xEA, 0xD4), (0xC8, 0xBC, 0xA0)
    gold = (0xF0, 0xC0, 0x40)
    for y in range(2, 15):
        for x in range(3, 14):
            px[x, y] = cover + (255,)
        px[2, y] = spine + (255,)
        px[13, y] = page + (255,) if 3 <= y <= 13 else cover_dark + (255,)
        px[14, y] = page_edge + (255,) if 3 <= y <= 13 else (0, 0, 0, 0)
    for x in range(2, 14):
        px[x, 1] = cover_dark + (255,)
        px[x, 15] = cover_dark + (255,)
    for y in range(3, 14):
        px[3, y] = cover_dark + (255,)
    # A chair on the cover: back, seat and legs.
    for x, y in ((9, 5), (9, 6), (9, 7), (9, 8), (6, 8), (7, 8), (8, 8), (6, 9), (6, 10), (6, 11), (9, 9), (9, 10), (9, 11)):
        px[x, y] = gold + (255,)
    save(img, "item/furniture_catalog.png")

# ---- Home theater ------------------------------------------------------------------------------

def theater():
    """Three films for the projector screen (48x32 frames), four movie posters, and popcorn."""
    W, H = 48, 32

    def film(name, n, frametime, draw):
        img = Image.new("RGBA", (W, H * n), (0, 0, 0, 255))
        for i in range(n):
            f = Image.new("RGBA", (W, H), (0, 0, 0, 255))
            draw(f.load(), i)
            img.paste(f, (0, H * i))
        full = os.path.join(ROOT, "block/screen/" + name + ".png")
        os.makedirs(os.path.dirname(full), exist_ok=True)
        img.save(full)
        with open(full + ".mcmeta", "w") as fh:
            json.dump({"animation": {"frametime": frametime, "width": W, "height": H}}, fh, indent=2)
            fh.write("\n")

    def fill(px, x0, y0, x1, y1, c):
        for y in range(max(0, y0), min(H, y1)):
            for x in range(max(0, x0), min(W, x1)):
                px[x, y] = c + (255,)

    # Film 1: a rocket crossing a starfield past a ringed planet.
    rnd = random.Random("space")
    stars = [(rnd.randrange(W), rnd.randrange(H), rnd.randrange(4)) for _ in range(40)]

    def space(px, i):
        fill(px, 0, 0, W, H, (0x08, 0x0A, 0x22))
        for x, y, phase in stars:
            v = 0xFF if (i + phase) % 4 else 0x80
            px[x, y] = (v, v, v, 255)
        for y in range(H):  # planet
            for x in range(W):
                if (x - 38) ** 2 + (y - 9) ** 2 <= 30:
                    px[x, y] = ((0xD0, 0x80, 0x40) if (y + x // 3) % 4 else (0xB0, 0x60, 0x30)) + (255,)
        fill(px, 31, 9, 46, 10, (0xE8, 0xD0, 0x90))  # ring
        rx = (i * 3) % (W + 16) - 12
        ry = 18 + (1 if i % 6 < 3 else 0)
        fill(px, rx, ry, rx + 8, ry + 3, (0xE8, 0xE8, 0xF0))   # body
        fill(px, rx + 8, ry + 1, rx + 10, ry + 2, (0xD0, 0x30, 0x30))  # nose
        fill(px, rx + 2, ry - 1, rx + 4, ry, (0xD0, 0x30, 0x30))  # fins
        fill(px, rx + 2, ry + 3, rx + 4, ry + 4, (0xD0, 0x30, 0x30))
        fill(px, rx + 5, ry + 1, rx + 6, ry + 2, (0x60, 0xB0, 0xF0))  # window
        flame = (0xFF, 0xC0, 0x30) if i % 2 else (0xFF, 0x70, 0x20)
        fill(px, rx - 3, ry + 1, rx, ry + 2, flame)
        fill(px, rx - 2, ry, rx, ry + 3, flame if i % 2 else (0xFF, 0xA0, 0x20))
    film("film_1", 24, 3, space)

    # Film 2: a sailboat at sea at sunset, with rolling waves.
    def sea(px, i):
        for y in range(18):
            t = y / 18
            px_c = (int(0xF0 - 0x40 * (1 - t)), int(0x90 + 0x40 * t), int(0x70 + 0x30 * (1 - t)))
            fill(px, 0, y, W, y + 1, px_c)
        for y in range(H):  # sun, half under the horizon
            for x in range(W):
                if (x - 12) ** 2 + (y - 18) ** 2 <= 30 and y < 18:
                    px[x, y] = (0xFF, 0xE0, 0x60, 255)
        fill(px, 0, 18, W, H, (0x20, 0x50, 0x90))
        for x in range(W):  # waves move left
            for row, shade in ((19, (0x40, 0x80, 0xC0)), (23, (0x30, 0x68, 0xA8)), (28, (0x40, 0x80, 0xC0))):
                if (x + i * (2 if row == 23 else 1)) % 8 < 3:
                    px[x, row] = shade + (255,)
        bx = (i * 2) % (W + 14) - 12
        by = 15 + (1 if i % 4 < 2 else 0)
        fill(px, bx, by, bx + 12, by + 2, (0x70, 0x40, 0x20))      # hull
        fill(px, bx + 2, by + 2, bx + 10, by + 3, (0x50, 0x2C, 0x14))
        fill(px, bx + 5, by - 9, bx + 6, by, (0x40, 0x28, 0x10))   # mast
        for k in range(8):                                      # sail
            fill(px, bx + 6, by - 9 + k, bx + 7 + k // 2, by - 8 + k, (0xF4, 0xF0, 0xE0))
    film("film_2", 32, 3, sea)

    # Film 3: a cartoon cat chasing a mouse across a lawn.
    def chase(px, i):
        fill(px, 0, 0, W, 22, (0x9C, 0xD8, 0xF8))
        fill(px, 0, 22, W, H, (0x58, 0xA8, 0x38))
        fill(px, 0, 22, W, 23, (0x70, 0xC0, 0x48))
        cloud = (i // 2) % (W + 10) - 8
        fill(px, cloud, 5, cloud + 8, 7, (0xFF, 0xFF, 0xFF))
        fill(px, cloud + 2, 4, cloud + 6, 5, (0xFF, 0xFF, 0xFF))
        hop = 1 if i % 4 < 2 else 0
        mx = (i * 3) % (W + 30) - 4
        fill(px, mx, 19 - hop, mx + 4, 22 - hop, (0x90, 0x90, 0x98))     # mouse
        fill(px, mx + 3, 18 - hop, mx + 4, 19 - hop, (0xF0, 0xA0, 0xB0))  # ear
        fill(px, mx - 3, 21 - hop, mx, 22 - hop, (0xF0, 0xA0, 0xB0))      # tail
        cx = mx - 16
        jump = 2 if i % 4 < 2 else 0
        fill(px, cx, 16 - jump, cx + 10, 21 - jump, (0xF0, 0x90, 0x30))  # cat body
        fill(px, cx + 8, 13 - jump, cx + 13, 18 - jump, (0xF0, 0x90, 0x30))  # head
        fill(px, cx + 8, 12 - jump, cx + 9, 13 - jump, (0xF0, 0x90, 0x30))
        fill(px, cx + 12, 12 - jump, cx + 13, 13 - jump, (0xF0, 0x90, 0x30))
        fill(px, cx + 11, 14 - jump, cx + 12, 15 - jump, (0x20, 0x20, 0x20))  # eye
        for k in range(0, 10, 3):
            fill(px, cx + k, 17 - jump, cx + k + 1, 21 - jump, (0xC8, 0x70, 0x20))  # stripes
        fill(px, cx - 4, 14 - jump, cx, 15 - jump, (0xF0, 0x90, 0x30))  # tail
        fill(px, cx + 1, 21 - jump, cx + 3, 23 - jump, (0xF0, 0x90, 0x30))  # legs
        fill(px, cx + 7, 21 - jump, cx + 9, 23 - jump, (0xF0, 0x90, 0x30))
    film("film_3", 32, 2, chase)

    # Movie posters: art in the middle 12 columns (the frame covers the edges), a title bar at the bottom.
    def poster(n, bg, art, title):
        img = new(color=bg)
        px = img.load()
        art(px)
        for x in range(3, 13):
            for y in (12, 13):
                px[x, y] = title + (255,)
        for x in range(4, 12, 2):
            px[x, 14] = title + (255,)
        save(img, f"block/poster/movie_{n}.png")

    def rocket(px):
        rnd = random.Random("poster")
        for _ in range(10):
            px[rnd.randrange(3, 13), rnd.randrange(1, 11)] = (0xFF, 0xFF, 0xFF, 255)
        for y in range(2, 9):
            for x in range(7, 10):
                px[x, y] = (0xE8, 0xE8, 0xF0, 255)
        px[8, 1] = (0xD0, 0x30, 0x30, 255)
        for x, y in ((6, 8), (10, 8), (6, 7), (10, 7)):
            px[x, y] = (0xD0, 0x30, 0x30, 255)
        px[8, 4] = (0x60, 0xB0, 0xF0, 255)
        for x in (7, 8, 9):
            px[x, 9] = (0xFF, 0xA0, 0x20, 255)
        px[8, 10] = (0xFF, 0xD0, 0x40, 255)

    def monster(px):
        for y in range(3, 11):
            for x in range(4, 12):
                if not (y < 5 and x in (4, 11)):
                    px[x, y] = (0x40, 0xA0, 0x40, 255)
        for x, y in ((6, 5), (9, 5)):
            px[x, y] = (0xFF, 0xFF, 0x60, 255)
        for x in range(6, 10):
            px[x, 8] = (0x20, 0x20, 0x20, 255)
        px[6, 9] = px[9, 9] = (0xFF, 0xFF, 0xFF, 255)

    def heart(px):
        rows = ["0110110", "1111111", "1111111", "0111110", "0011100", "0001000"]
        for dy, row in enumerate(rows):
            for dx, c in enumerate(row):
                if c == "1":
                    px[5 + dx, 3 + dy] = (0xE0, 0x30, 0x50, 255)
        px[6, 4] = (0xFF, 0xA0, 0xB0, 255)

    def cowboy(px):
        for y in range(1, 11):
            for x in range(16):
                if (x - 8) ** 2 + (y - 10) ** 2 <= 9:
                    px[x, y] = (0xFF, 0xD0, 0x40, 255)
        for x in range(3, 13):
            px[x, 10] = (0x30, 0x20, 0x10, 255)
        for x in range(5, 11):
            px[x, 6] = (0x50, 0x30, 0x18, 255)
        for y in range(3, 6):
            for x in range(6, 10):
                px[x, y] = (0x50, 0x30, 0x18, 255)

    poster(1, (0x10, 0x14, 0x40), rocket, (0xF0, 0xC0, 0x30))
    poster(2, (0x20, 0x10, 0x18), monster, (0xD0, 0x20, 0x20))
    poster(3, (0xF8, 0xC0, 0xD0), heart, (0x90, 0x20, 0x40))
    poster(4, (0xE0, 0x70, 0x30), cowboy, (0x40, 0x20, 0x10))

    # Popcorn: in the machine's case (block) and in a striped paper bag (item).
    rnd = random.Random("popcorn")
    img = new(color=(0xF2, 0xE6, 0xB0))
    px = img.load()
    for _ in range(70):
        x, y = rnd.randrange(16), rnd.randrange(16)
        px[x, y] = rnd.choice([(0xFF, 0xFA, 0xE8), (0xF8, 0xD8, 0x70), (0xE0, 0xC0, 0x60), (0xFF, 0xFF, 0xFF)]) + (255,)
    save(img, "block/popcorn.png")

    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    for y in range(6, 15):
        for x in range(4 + (y - 6) // 6, 12 - (y - 6) // 6):
            px[x, y] = ((0xE0, 0x30, 0x30) if (x // 2) % 2 == 0 else (0xF8, 0xF8, 0xF8)) + (255,)
    for x in range(4, 12):
        px[x, 6] = (0xC0, 0x20, 0x20, 255)
    for x, y in ((4, 5), (5, 4), (6, 5), (7, 3), (8, 4), (9, 5), (10, 4), (11, 5), (6, 3), (9, 3), (8, 2)):
        px[x, y] = rnd.choice([(0xFF, 0xFA, 0xE8), (0xF8, 0xD8, 0x70)]) + (255,)
    save(img, "item/popcorn.png")


if __name__ == "__main__":
    catalog_item()
    style_textures()
    bar_and_nursery()
    garage_and_library()
    paintings()
    tool_items()
    watering_can()
    theater()
    window_glass()
    for name, rgb in DYES.items():
        wallpaper(name, rgb)
    tiles()
    villager_profession()
    food_items()
    dartboard()
    piano_keys()
    for name, rgb in DYES.items():
        fabric(name, rgb)
    appliance_textures()
    tv_channels()
    computer_screen()
    arcade_screen()
    print("textures written to", os.path.normpath(ROOT))
