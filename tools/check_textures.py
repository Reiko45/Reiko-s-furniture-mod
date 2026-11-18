"""Checks that every texture referenced by the mod's models exists.

Usage: python3 tools/check_textures.py [path to the Minecraft resources jar]
By default it uses the jar ModDevGradle downloads to build/moddev/artifacts/ (run ./gradlew build first).
Vanilla textures are looked up in that jar; reikos_furniture textures in src/main/resources.
"""
import glob
import json
import os
import sys
import zipfile

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..")
MODEL_DIRS = ["src/main/resources/assets", "src/generated/resources/assets"]
OWN_TEXTURES = os.path.join(ROOT, "src/main/resources/assets/reikos_furniture/textures")


def main():
    jars = sys.argv[1:] or glob.glob(os.path.join(ROOT, "build/moddev/artifacts/*client-extra*.jar"))
    if not jars:
        sys.exit("Minecraft resources jar not found; run ./gradlew build first or pass its path.")
    vanilla = {n for n in zipfile.ZipFile(jars[0]).namelist() if n.startswith("assets/minecraft/textures/")}
    missing = []
    for model_dir in MODEL_DIRS:
        for path in glob.glob(os.path.join(ROOT, model_dir, "*/models/**/*.json"), recursive=True):
            textures = json.load(open(path)).get("textures", {})
            for value in textures.values():
                if value.startswith("#"):
                    continue
                namespace, _, tex = value.partition(":") if ":" in value else ("minecraft", "", value)
                if namespace == "minecraft":
                    ok = f"assets/minecraft/textures/{tex}.png" in vanilla
                elif namespace == "reikos_furniture":
                    ok = os.path.exists(os.path.join(OWN_TEXTURES, tex + ".png"))
                else:
                    ok = True  # other mods' textures (modded woods) are checked at runtime
                if not ok:
                    missing.append((value, os.path.relpath(path, ROOT)))
    for value, path in sorted(set(missing)):
        print("MISSING", value, "in", path)
    print(f"checked; missing: {len(set(missing))}")
    sys.exit(1 if missing else 0)


if __name__ == "__main__":
    main()
