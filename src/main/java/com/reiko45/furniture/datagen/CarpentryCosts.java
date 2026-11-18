package com.reiko45.furniture.datagen;

import java.util.Map;

/**
 * How many planks each wooden piece costs at the furniture workbench (a chest counts as 8 planks).
 * Pieces that need other materials (lamps, sinks, counters with stone tops, bookshelves with books)
 * are left out and can only be made at a crafting table.
 */
final class CarpentryCosts {
    static final Map<String, Integer> COSTS = Map.ofEntries(
            // Bedroom
            Map.entry("nightstand", 13),
            Map.entry("dresser", 15),
            Map.entry("wardrobe", 22),
            Map.entry("desk", 5),
            // Kitchen
            Map.entry("upper_cabinet", 12),
            Map.entry("cutting_board", 1),
            // Dining room
            Map.entry("dining_table", 7),
            Map.entry("dining_chair", 3),
            Map.entry("bench", 3),
            Map.entry("sideboard", 15),
            // Living room
            Map.entry("coffee_table", 4),
            Map.entry("tv_stand", 15),
            Map.entry("wall_shelf", 2),
            // Office
            Map.entry("filing_cabinet", 23),
            // Outdoor
            Map.entry("patio_table", 6),
            Map.entry("patio_chair", 3),
            Map.entry("garden_bench", 5),
            Map.entry("shutters", 2),
            // Kids' room
            Map.entry("toy_chest", 15),
            Map.entry("kids_table", 3),
            Map.entry("kids_chair", 2),
            Map.entry("rocking_horse", 6),
            // Laundry room
            Map.entry("laundry_basket", 3),
            // Music room
            Map.entry("piano_bench", 4));

    private CarpentryCosts() {
    }
}
