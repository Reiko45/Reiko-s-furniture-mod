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


if __name__ == "__main__":
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
