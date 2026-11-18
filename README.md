# Reiko's Furniture

A furniture mod for **Minecraft 1.21.1** on **NeoForge**. The furniture is split into room sections:
bedroom, kitchen, dining room and living room. Wooden pieces come in all 11 vanilla wood types,
and sofas come in all 16 colors.

Everything is in one creative tab, **Reiko's Furniture**, ordered room by room. Each item's tooltip
shows which room it belongs to.

## Furniture

| Room | Piece | Variants | What it does |
|---|---|---|---|
| Bedroom | Nightstand | 11 woods | Storage, 1 row |
| | Dresser | 11 woods | Storage, 3 rows |
| | Wardrobe (2 blocks tall) | 11 woods | Storage, 6 rows |
| | Desk | 11 woods | Decoration |
| | Table lamp | 11 woods | Right-click to switch on and off |
| Kitchen | Counter | 11 woods | Storage, 3 rows |
| | Upper cabinet | 11 woods | Wall cabinet, storage, 2 rows |
| | Sink | 11 woods | Fills buckets and glass bottles with water |
| | Stove | 1 | Cooks food like a smoker |
| | Fridge (2 blocks tall) | 1 | Storage, 6 rows |
| Dining room | Dining table | 11 woods | Joins with tables next to it |
| | Dining chair | 11 woods | Sit on it |
| | Bench | 11 woods | Sit on it |
| | Sideboard | 11 woods | Storage, 3 rows |
| Living room | Sofa | 16 colors | Sit on it; sofas side by side join into one long sofa |
| | Coffee table | 11 woods | Joins with tables next to it |
| | Bookshelf | 11 woods | Storage, 2 rows; powers enchanting tables |
| | TV stand | 11 woods | Storage, 1 row |
| | TV | 1 | Right-click to switch on and off |
| | Floor lamp (2 blocks tall) | 11 woods | Right-click to switch on and off |

That is 195 blocks in total.

## How it works in game

- **Sitting:** right-click a chair, bench or sofa with an empty hand. Sneak to stand up. Only one
  player fits on each seat.
- **Storage:** right-click to open. Breaking a storage piece drops what is inside. Comparators read
  how full it is.
- **Lights:** right-click lamps and the TV to switch them on and off.
- **Stove:** works like a smoker. It uses fuel, only cooks food, and cooks twice as fast as a furnace.
- **Sink:** right-click with an empty bucket or glass bottle to fill it with water. Right-click with a
  water bucket to empty it.
- **Tables:** dining tables and coffee tables of the same wood join together. Legs only appear on
  the outer corners.
- **Sofas:** sofas facing the same way join together, whatever their color. Only the two ends keep
  their armrests.
- **Bookshelf:** counts as a bookshelf for enchanting tables.
- Furniture faces you when placed, and most pieces can be placed underwater (waterlogged). Wooden
  and wool furniture can burn, except crimson and warped wood.

## Recipes

All recipes show up in the recipe book. In the patterns below, **P** is planks, **L** is a log and
**S** is a slab of the wood you want. A space means an empty slot.

| Piece | Pattern | Other ingredients | Makes |
|---|---|---|---|
| Nightstand | `PPP` / `PCP` | C = chest | 1 |
| Dresser | `PPP` / `PCP` / `PPP` | C = chest | 1 |
| Wardrobe | `PCP` / `PPP` / `PCP` | C = chest | 1 |
| Desk | `PPP` / `P P` | | 1 |
| Table lamp | `W` / `T` / `P` | W = white wool, T = torch | 1 |
| Counter | `TTT` / `PCP` / `PPP` | T = smooth stone slab, C = chest | 1 |
| Upper cabinet | `SSS` / `PCP` | C = chest | 1 |
| Sink | `IBI` / `PPP` | I = iron ingot, B = bucket | 1 |
| Stove | `III` / `IFI` / `III` | I = iron ingot, F = furnace | 1 |
| Fridge | `III` / `ICI` / `III` | I = iron ingot, C = chest | 1 |
| Dining table | `PPP` / `L L` / `L L` | | 1 |
| Dining chair | `P  ` / `PPP` / `L L` | | 2 |
| Bench | `PPP` / `L L` | | 2 |
| Sideboard | `PPP` / `PCP` / `L L` | C = chest | 1 |
| Sofa | `W W` / `WWW` / `P P` | W = wool of the sofa's color, P = any planks | 2 |
| Coffee table | `SSS` / `L L` | | 1 |
| Bookshelf | `PPP` / `BCB` / `PPP` | B = book, C = chest | 1 |
| TV stand | `SSS` / `PCP` / `PPP` | C = chest | 1 |
| TV | `III` / `IGI` / `IRI` | I = iron ingot, G = glass pane, R = redstone | 1 |
| Floor lamp | `W` / `T` / `L` | W = white wool, T = torch | 1 |

## Building and running

You need Java 21. The Gradle wrapper downloads everything else.

```bash
./gradlew build
```

The mod jar is written to `build/libs/reikos_furniture-1.0.0.jar`. Put it in the `mods` folder of a
Minecraft 1.21.1 NeoForge install.

Other useful tasks:

| Command | What it does |
|---|---|
| `./gradlew runClient` | Starts the game with the mod loaded |
| `./gradlew runServer` | Starts a dedicated server with the mod loaded |
| `./gradlew runData` | Regenerates models, block states, recipes, loot tables, tags and names into `src/generated/resources` |
| `./gradlew runGameTestServer` | Runs the automated in-game tests and exits |

Run `./gradlew runData` after changing pieces, models or recipes, and commit the files it generates.

## Project layout

```
src/main/java/com/reiko45/furniture/
  ReikosFurniture.java         Mod entry point; lists the room sections
  core/                        Shared parts every room uses
    FurnitureWood.java         The 11 wood types
    FurnitureRegistry.java     Registers furniture blocks and their items
    block/                     Base blocks: seats, storage, two-block-tall pieces, lights, connecting tables
    blockentity/, entity/      Storage inventory, invisible seat entity
  bedroom/  kitchen/  dining/  livingroom/
    <Room>Furniture.java       The room's blocks
    <Room>Data.java            The room's models and recipes (used by runData)
  datagen/                     Data generation shared by all rooms
  gametest/                    Automated in-game tests
src/main/resources/assets/reikos_furniture/models/block/template/<room>/
                               Hand-made model templates, one per piece
src/generated/resources/       Output of runData (do not edit by hand)
```

Models use only vanilla textures. Each piece has one template model that uses the texture
variables `#planks`, `#log` and `#stripped_log` (sofas use `#wool`). Data generation creates the
model for every wood type or color from it. You can open and edit the templates in
[Blockbench](https://www.blockbench.net/) as Java block models.

### Adding a piece to a room

1. Add the block to the room's `<Room>Furniture.java`, for example:
   ```java
   public static final WoodSet<FurnitureStorageBlock> CHEST_OF_DRAWERS = FurnitureRegistry.woodSet(
           "chest_of_drawers", ROOM, wood -> new FurnitureStorageBlock(wood.properties(), 3, SHAPE));
   ```
2. Add a template model at `models/block/template/<room>/chest_of_drawers.json`. Make the front
   face north.
3. In `<Room>Data.java`, add the model (`models.woodFacing(CHEST_OF_DRAWERS, "chest_of_drawers")`)
   and a recipe.
4. Run `./gradlew runData`.

Names, loot tables and mining tags are generated automatically.

### Adding a room

1. Add the room to `core/Room.java`.
2. Create a package for it with a `<Room>Furniture` class and a `<Room>Data` class, like the existing
   rooms.
3. Call `<Room>Furniture.init()` in `ReikosFurniture` and add `new <Room>Data()` to the list in
   `datagen/DataGenerators.java`.

## License

All Rights Reserved. To change the license, edit `mod_license` in `gradle.properties` and add a
`LICENSE` file.
