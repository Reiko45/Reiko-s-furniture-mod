"""Synthesizes the mod's sound effects with numpy and encodes them to .ogg with ffmpeg (libvorbis).

Usage: python3 tools/sounds/generate_sounds.py
Writes into src/main/resources/assets/reikos_furniture/sounds/. Needs numpy and ffmpeg.
Every sound is made from code with a fixed random seed, so running it again gives the same audio.
"""
import os
import subprocess
import tempfile
import wave

import numpy as np

RATE = 44100
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "..", "src", "main", "resources",
                   "assets", "reikos_furniture", "sounds")


def t(seconds):
    return np.arange(int(RATE * seconds)) / RATE


def env(n, attack=0.005, release=0.05):
    """Linear attack and release so sounds start and stop without clicks."""
    e = np.ones(n)
    a, r = int(RATE * attack), int(RATE * release)
    if a:
        e[:a] = np.linspace(0, 1, a)
    if r:
        e[-r:] *= np.linspace(1, 0, r)
    return e


def lowpass(x, cutoff):
    """Simple one-pole low-pass filter; cutoff can be a number or an array (sweep)."""
    cutoff = np.broadcast_to(np.asarray(cutoff, dtype=float), x.shape)
    alpha = 1 - np.exp(-2 * np.pi * cutoff / RATE)
    y = np.zeros_like(x)
    acc = 0.0
    for i in range(len(x)):
        acc += alpha[i] * (x[i] - acc)
        y[i] = acc
    return y


def bell(freq, seconds, decay):
    tt = t(seconds)
    partials = [(1, 1.0), (2.76, 0.45), (5.4, 0.25), (8.93, 0.1)]
    s = sum(a * np.sin(2 * np.pi * freq * p * tt) * np.exp(-tt * decay * p ** 0.5) for p, a in partials)
    return s * env(len(tt), 0.002, 0.05)


def write(name, samples, volume=0.8):
    samples = samples / max(1e-9, np.max(np.abs(samples))) * volume
    pcm = (samples * 32767).astype(np.int16)
    os.makedirs(OUT, exist_ok=True)
    with tempfile.TemporaryDirectory() as tmp:
        wav_path = os.path.join(tmp, name + ".wav")
        with wave.open(wav_path, "wb") as w:
            w.setnchannels(1)
            w.setsampwidth(2)
            w.setframerate(RATE)
            w.writeframes(pcm.tobytes())
        # bitexact: no random Ogg stream serial, so running the script again gives identical files.
        subprocess.run(["ffmpeg", "-loglevel", "error", "-y", "-i", wav_path, "-c:a", "libvorbis", "-q:a", "4",
                        "-fflags", "+bitexact", "-flags:a", "+bitexact",
                        os.path.join(OUT, name + ".ogg")], check=True)


def microwave_ding():
    beep = np.sin(2 * np.pi * 1850 * t(0.16)) * env(int(RATE * 0.16), 0.003, 0.02)
    gap = np.zeros(int(RATE * 0.12))
    return np.concatenate([beep, gap, beep, gap, beep])


def toilet_flush(rnd):
    n = int(RATE * 2.4)
    noise = rnd.standard_normal(n)
    sweep = np.linspace(2600, 600, n)
    water = lowpass(noise, sweep)
    gurgle = 0.6 + 0.4 * np.sin(2 * np.pi * (7 + 3 * np.sin(2 * np.pi * 0.7 * t(2.4))) * t(2.4))
    shape = np.minimum(1, t(2.4) / 0.25) * np.exp(-np.maximum(0, t(2.4) - 0.9) * 1.6)
    return water * gurgle * shape


def tv_static(rnd):
    n = int(RATE * 0.7)
    noise = rnd.standard_normal(n)
    hiss = noise - lowpass(noise, 1500)
    click = np.zeros(n)
    click[:200] = np.linspace(1, 0, 200)
    return (hiss * np.exp(-t(0.7) * 3) + click * 2) * env(n, 0.001, 0.08)


def doorbell():
    return np.concatenate([bell(659.3, 0.9, 3.0), bell(523.3, 1.4, 2.4)])


def washer(rnd):
    n = int(RATE * 3.0)
    hum = 0.5 * np.sin(2 * np.pi * 60 * t(3.0)) + 0.2 * np.sin(2 * np.pi * 120 * t(3.0))
    slosh = lowpass(rnd.standard_normal(n), 700) * (0.5 + 0.5 * np.sin(2 * np.pi * 0.66 * t(3.0)) ** 2)
    return (hum * 0.6 + slosh * 3) * env(n, 0.2, 0.3)


def dryer(rnd):
    n = int(RATE * 3.0)
    hum = 0.5 * np.sin(2 * np.pi * 75 * t(3.0))
    thumps = np.zeros(n)
    for k in range(5):
        start = int(RATE * (0.2 + k * 0.6 + rnd.uniform(-0.05, 0.05)))
        length = int(RATE * 0.12)
        thumps[start:start + length] += np.sin(2 * np.pi * 90 * t(0.12)) * np.exp(-t(0.12) * 30)
    return (hum * 0.4 + thumps * 1.5 + lowpass(rnd.standard_normal(n), 300) * 0.5) * env(n, 0.2, 0.3)


def coffee(rnd):
    n = int(RATE * 2.5)
    out = lowpass(rnd.standard_normal(n), 2500) * 0.15
    for _ in range(45):
        start = rnd.integers(0, n - 4000)
        f = rnd.uniform(300, 900)
        blip = np.sin(2 * np.pi * f * (1 + t(0.06) * 4) * t(0.06)) * np.exp(-t(0.06) * 60)
        out[start:start + len(blip)] += blip * rnd.uniform(0.3, 0.8)
    return out * env(n, 0.1, 0.3)


def toaster_pop():
    click = np.zeros(int(RATE * 0.5))
    click[:120] = np.linspace(1, -1, 120)
    tt = t(0.5)
    boing = np.sin(2 * np.pi * (420 - 180 * tt) * tt) * np.exp(-tt * 9)
    return click * 0.8 + boing


def blender(rnd):
    n = int(RATE * 2.5)
    tt = t(2.5)
    freq = 150 + 60 * np.minimum(1, tt / 0.4)
    phase = 2 * np.pi * np.cumsum(freq) / RATE
    saw = 2 * ((phase / (2 * np.pi)) % 1) - 1
    motor = lowpass(saw, 2500)
    grind = lowpass(rnd.standard_normal(n), 1200) * 0.3
    return (motor + grind) * env(n, 0.05, 0.25)


def saw(rnd):
    n = int(RATE * 1.2)
    out = np.zeros(n)
    for k in range(4):
        start = int(RATE * k * 0.3)
        length = int(RATE * 0.22)
        stroke = rnd.standard_normal(length)
        stroke = stroke - lowpass(stroke, 1800)
        out[start:start + length] += stroke * np.sin(np.linspace(0, np.pi, length))
    return out


def arcade_blip():
    notes = [523.3, 659.3, 784.0, 1046.5]
    parts = []
    for f in notes:
        tt = t(0.07)
        parts.append(np.sign(np.sin(2 * np.pi * f * tt)) * 0.5 * env(len(tt), 0.002, 0.01))
    return np.concatenate(parts)


def sliding_door(rnd):
    n = int(RATE * 0.7)
    tt = t(0.7)
    roll = lowpass(rnd.standard_normal(n), 400 + 300 * np.sin(np.pi * tt / 0.7)) * np.sin(np.pi * tt / 0.7)
    rumble = 0.3 * np.sin(2 * np.pi * 55 * tt) * np.sin(np.pi * tt / 0.7)
    stop = np.zeros(n)
    k = int(RATE * 0.58)
    stop[k:] = np.sin(2 * np.pi * 140 * t(0.7)[: n - k]) * np.exp(-t(0.7)[: n - k] * 40)
    return (roll * 2.5 + rumble + stop * 0.8) * env(n, 0.03, 0.05)


def globe_spin(rnd):
    n = int(RATE * 1.2)
    tt = t(1.2)
    speed = np.exp(-tt * 2.0)
    whir = lowpass(rnd.standard_normal(n), 300 + 900 * speed) * speed
    ticks = np.zeros(n)
    phase = np.cumsum(speed * 18 / RATE)
    ticks[np.where(np.diff(np.floor(phase), prepend=0) > 0)] = 1.0
    ticks = lowpass(ticks, 2500) * 6
    return (whir * 2 + ticks) * env(n, 0.01, 0.2)


def projector(rnd):
    """A film projector: a motor hum with the sprocket clicking 24 times a second."""
    n = int(RATE * 1.6)
    tt = t(1.6)
    hum = np.sin(2 * np.pi * 110 * tt) * 0.3 + np.sin(2 * np.pi * 220 * tt) * 0.15
    clicks = np.zeros(n)
    period = RATE // 24
    for start in range(0, n - 200, period):
        click = rnd.standard_normal(200) * np.exp(-np.arange(200) / 30)
        clicks[start:start + 200] += click
    clicks = clicks - lowpass(clicks, 900)
    return (hum + clicks * 0.6 + lowpass(rnd.standard_normal(n), 400) * 0.1) * env(n, 0.08, 0.3)


def popcorn_popping(rnd):
    """Kernels popping at random, faster and faster, over a soft sizzle."""
    n = int(RATE * 2.5)
    out = lowpass(rnd.standard_normal(n), 3000) * 0.05
    for _ in range(45):
        start = int(min(n - 2000, abs(rnd.normal(0.65, 0.25)) * n))
        length = 1200
        pop = rnd.standard_normal(length) * np.exp(-np.arange(length) / 140)
        out[start:start + length] += (pop - lowpass(pop, 600)) * rnd.uniform(0.5, 1.0)
    return out * env(n, 0.02, 0.2)


def pottery_wheel(rnd):
    """A low wheel hum and the wet hiss of clay under the hands, rising and falling."""
    n = int(RATE * 2.5)
    tt = t(2.5)
    hum = np.sin(2 * np.pi * 70 * tt) * 0.4 + np.sin(2 * np.pi * 140 * tt) * 0.15
    wet = lowpass(rnd.standard_normal(n), 900) * (0.6 + 0.4 * np.sin(2 * np.pi * 1.3 * tt))
    return (hum + wet * 0.8) * env(n, 0.1, 0.3)


def sewing_machine(rnd):
    """The needle going up and down, 14 stitches a second, with a motor whine."""
    n = int(RATE * 2.0)
    tt = t(2.0)
    out = np.sin(2 * np.pi * 380 * tt) * 0.05
    period = RATE // 14
    for start in range(0, n - 600, period):
        tick = rnd.standard_normal(600) * np.exp(-np.arange(600) / 80)
        out[start:start + 600] += tick - lowpass(tick, 1500)
    return out * env(n, 0.03, 0.15)


def toolbox_latch():
    tt = t(0.25)
    click = np.zeros(len(tt))
    for start in (0.0, 0.07):
        k = int(RATE * start)
        m = len(tt) - k
        click[k:] += (np.sin(2 * np.pi * 2300 * tt[:m]) + 0.5 * np.sin(2 * np.pi * 3700 * tt[:m])) * np.exp(-tt[:m] * 70)
    return click * env(len(tt), 0.001, 0.02)


if __name__ == "__main__":
    rnd = np.random.default_rng(45)
    write("microwave_ding", microwave_ding(), 0.6)
    write("toilet_flush", toilet_flush(rnd))
    write("tv_static", tv_static(rnd), 0.5)
    write("doorbell", doorbell())
    write("washer_running", washer(rnd), 0.6)
    write("dryer_running", dryer(rnd), 0.6)
    write("coffee_brewing", coffee(rnd), 0.6)
    write("toaster_pop", toaster_pop())
    write("blender", blender(rnd), 0.6)
    write("workbench_saw", saw(rnd), 0.6)
    write("arcade_blip", arcade_blip(), 0.5)
    write("sliding_door", sliding_door(rnd), 0.6)
    write("globe_spin", globe_spin(rnd), 0.5)
    write("toolbox_latch", toolbox_latch(), 0.6)
    write("projector", projector(rnd), 0.5)
    write("popcorn_popping", popcorn_popping(rnd), 0.6)
    write("pottery_wheel", pottery_wheel(rnd), 0.5)
    write("sewing_machine", sewing_machine(rnd), 0.5)
    print("sounds written to", os.path.normpath(OUT))
