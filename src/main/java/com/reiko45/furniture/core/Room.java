package com.reiko45.furniture.core;

/** The room sections of the mod. Each room has its own package and model template folder. */
public enum Room {
    BEDROOM("bedroom", "Bedroom"),
    KITCHEN("kitchen", "Kitchen"),
    DINING_ROOM("dining", "Dining Room"),
    LIVING_ROOM("livingroom", "Living Room"),
    BATHROOM("bathroom", "Bathroom"),
    OFFICE("office", "Office"),
    OUTDOOR("outdoor", "Outdoor"),
    KIDS_ROOM("kids", "Kids' Room"),
    LAUNDRY("laundry", "Laundry Room"),
    MUSIC("music", "Music Room"),
    GAME_ROOM("gameroom", "Game Room"),
    PET_CORNER("pets", "Pet Corner"),
    DECOR("decor", "Decor"),
    GARAGE("garage", "Garage"),
    LIBRARY("library", "Library"),
    BAR("bar", "Home Bar"),
    NURSERY("nursery", "Nursery & Hobby Room"),
    GREENHOUSE("greenhouse", "Greenhouse & Garden"),
    THEATER("theater", "Home Theater"),
    STUDIO("studio", "Art Studio"),
    HOLIDAY("holiday", "Holidays");

    private final String id;
    private final String displayName;

    Room(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    /** Folder name used for model templates, e.g. {@code models/block/template/bedroom}. */
    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public String translationKey() {
        return "room.reikos_furniture." + id;
    }
}
