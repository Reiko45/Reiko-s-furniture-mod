package com.reiko45.furniture.datagen.lang;

import static com.reiko45.furniture.datagen.lang.Grammar.Gender.F;
import static com.reiko45.furniture.datagen.lang.Grammar.Gender.M;
import static com.reiko45.furniture.datagen.lang.Grammar.Gender.N;

import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Style;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

/**
 * German. Color and style come first and agree with the noun ("Roter Sessel", "Rote Lampe",
 * "Rotes Sofa"); the wood is joined to the noun: "Rustikales Eichenholz-Bett".
 */
public class German extends Grammar {
    /** Wood words as in Minecraft's door names ("Eichenholztür" without "tür"). */
    private static final String[] WOODS = {"Eichenholz", "Fichtenholz", "Birkenholz", "Tropenholz", "Akazienholz",
            "Schwarzeichenholz", "Mangrovenholz", "Kirschholz", "Bambus", "Karmesin", "Wirr"};
    /** Colors as in Minecraft's carpets (masculine), wool (feminine) and beds (neuter). */
    private static final String[] COLORS_M = {"Weißer", "Oranger", "Magenta", "Hellblauer", "Gelber", "Hellgrüner", "Rosa",
            "Grauer", "Hellgrauer", "Türkiser", "Violetter", "Blauer", "Brauner", "Grüner", "Roter", "Schwarzer"};
    private static final String[] COLORS_F = {"Weiße", "Orange", "Magenta", "Hellblaue", "Gelbe", "Hellgrüne", "Rosa",
            "Graue", "Hellgraue", "Türkise", "Violette", "Blaue", "Braune", "Grüne", "Rote", "Schwarze"};
    private static final String[] COLORS_N = {"Weißes", "Oranges", "Magenta", "Hellblaues", "Gelbes", "Hellgrünes", "Rosa",
            "Graues", "Hellgraues", "Türkises", "Violettes", "Blaues", "Braunes", "Grünes", "Rotes", "Schwarzes"};
    private static final Map<Style, String[]> STYLES = new EnumMap<>(Map.of(
            Style.RUSTIC, new String[] {"Rustikaler", "Rustikale", "Rustikales"},
            Style.MODERN, new String[] {"Moderner", "Moderne", "Modernes"},
            Style.ROYAL, new String[] {"Königlicher", "Königliche", "Königliches"},
            Style.JAPANESE, new String[] {"Japanischer", "Japanische", "Japanisches"}));

    public German() {
        noun("bar_counter", "Bartheke", F);
        noun("baseboard", "Fußleiste", F);
        noun("bed", "Bett", N);
        noun("bench", "Sitzbank", F);
        noun("book_stand", "Buchständer", M);
        noun("bookshelf", "Bücherregal", N);
        noun("bunk_bed", "Etagenbett", N);
        noun("cat_tree", "Kratzbaum", M);
        noun("changing_table", "Wickeltisch", M);
        noun("coffee_table", "Couchtisch", M);
        noun("counter", "Küchentheke", F);
        noun("crib", "Gitterbett", N);
        noun("crown_molding", "Deckenleiste", F);
        noun("cutting_board", "Schneidebrett", N);
        noun("desk", "Schreibtisch", M);
        noun("dining_chair", "Esszimmerstuhl", M);
        noun("dining_table", "Esstisch", M);
        noun("dresser", "Kommode", F);
        noun("filing_cabinet", "Aktenschrank", M);
        noun("floor_lamp", "Stehlampe", F);
        noun("french_door", "Sprossentür", F);
        noun("garden_bench", "Gartenbank", F);
        noun("glass_cupboard", "Vitrinenschrank", M);
        noun("glass_upper_cabinet", "Hängevitrine", F);
        noun("globe", "Globus", M);
        noun("kids_chair", "Kinderstuhl", M);
        noun("kids_table", "Kindertisch", M);
        noun("kitchen_island", "Kücheninsel", F);
        noun("laundry_basket", "Wäschekorb", M);
        noun("library_ladder", "Bibliotheksleiter", F);
        noun("mirror", "Spiegel", M);
        noun("nightstand", "Nachttisch", M);
        noun("parquet", "Parkett", N);
        noun("parquet_slab", "Parkettstufe", F);
        noun("patio_chair", "Gartenstuhl", M);
        noun("patio_table", "Gartentisch", M);
        noun("pegboard", "Lochwand", F);
        noun("piano", "Klavier", N);
        noun("piano_bench", "Klavierbank", F);
        noun("picture_frame", "Bilderrahmen", M);
        noun("planter_box", "Pflanzkasten", M);
        noun("pool_table", "Billardtisch", M);
        noun("record_player", "Plattenspieler", M);
        noun("rocking_horse", "Schaukelpferd", N);
        noun("shutters", "Fensterladen", M);
        noun("sideboard", "Sideboard", N);
        noun("sink", "Spüle", F);
        noun("sliding_door", "Schiebetür", F);
        noun("table_lamp", "Tischlampe", F);
        noun("tall_bookcase", "Bücherwand", F);
        noun("toy_chest", "Spielzeugkiste", F);
        noun("tv_stand", "TV-Schrank", M);
        noun("upper_cabinet", "Hängeschrank", M);
        noun("vanity", "Schminktisch", M);
        noun("wall_clock", "Wanduhr", F);
        noun("wall_lamp", "Wandlampe", F);
        noun("wall_shelf", "Wandregal", N);
        noun("wardrobe", "Kleiderschrank", M);
        noun("window", "Fenster", N);
        noun("wine_rack", "Weinregal", N);
        noun("workshop_bench", "Werkbank", F);
        noun("writing_desk", "Sekretär", M);
        noun("armchair", "Sessel", M);
        noun("baby_mobile", "Mobile", N);
        noun("bar_stool", "Barhocker", M);
        noun("blinds", "Jalousie", F);
        noun("curtains", "Vorhang", M);
        noun("feeding_bowl", "Futternapf", M);
        noun("gaming_chair", "Gaming-Stuhl", M);
        noun("office_chair", "Bürostuhl", M);
        noun("oil_drum", "Ölfass", N);
        noun("pendant_lamp", "Hängelampe", F);
        noun("pet_bed", "Haustierbett", N);
        noun("rug", "Teppich", M);
        noun("sofa", "Sofa", N);
        noun("toolbox", "Werkzeugkasten", M);
        noun("towel_rack", "Handtuchhalter", M);
        noun("wallpaper", "Tapete", F);

        single("aquarium", "Aquarium");
        single("arcade_cabinet", "Spielautomat");
        single("bathroom_cabinet", "Badezimmerschrank");
        single("bathroom_sink", "Waschbecken");
        single("bathtub", "Badewanne");
        single("bird_cage", "Vogelkäfig");
        single("blender", "Standmixer");
        single("ceiling_light", "Deckenleuchte");
        single("chalkboard", "Kreidetafel");
        single("chandelier", "Kronleuchter");
        single("coffee_machine", "Kaffeemaschine");
        single("computer", "Computer");
        single("dartboard", "Dartscheibe");
        single("desk_lamp", "Schreibtischlampe");
        single("doorbell", "Türklingel");
        single("drum_kit", "Schlagzeug");
        single("dryer", "Wäschetrockner");
        single("espresso_machine", "Espressomaschine");
        single("fireplace", "Kamin");
        single("fridge", "Kühlschrank");
        single("furniture_workbench", "Möbelwerkbank");
        single("grill", "Grill");
        single("metal_shelving", "Metallregal");
        single("microwave", "Mikrowelle");
        single("plate", "Teller");
        single("reading_lamp", "Leselampe");
        single("safe", "Tresor");
        single("shower", "Dusche");
        single("stove", "Herd");
        single("toaster", "Toaster");
        single("toilet", "Toilette");
        single("trash_can", "Mülleimer");
        single("tv", "Fernseher");
        single("washing_machine", "Waschmaschine");
        single("white_tiles", "Weiße Fliesen");
        single("black_tiles", "Schwarze Fliesen");
        single("checkered_tiles", "Schachbrettfliesen");
        single("blue_tiles", "Blaue Fliesen");
        single("green_tiles", "Grüne Fliesen");
        single("terracotta_tiles", "Terrakottafliesen");
        single("marble_tiles", "Marmorfliesen");
        single("hexagon_tiles", "Sechseckfliesen");

        String adv = "advancements.reikos_furniture.";
        text(adv + "root.title", "Reiko's Furniture");
        text(adv + "root.description", "Stelle dein erstes Möbelstück her");
        text(adv + "woodworker.title", "Schreiner");
        text(adv + "woodworker.description", "Stelle eine Möbelwerkbank her. Dazu gibt es einen Möbelkatalog");
        text(adv + "take_a_seat.title", "Nimm Platz");
        text(adv + "take_a_seat.description", "Setz dich auf einen Stuhl, ein Sofa oder einen Hocker");
        text(adv + "coffee_break.title", "Kaffeepause");
        text(adv + "coffee_break.description", "Trinke etwas, das ein Küchen- oder Bargerät zubereitet hat");
        text(adv + "home_sweet_home.title", "Trautes Heim");
        text(adv + "home_sweet_home.description", "Besitze ein Möbelstück aus jedem Raum");
        text(adv + "interior_designer.title", "Innenarchitekt");
        text(adv + "interior_designer.description", "Drehe ein Möbelstück mit einem Hammer");
        text(adv + "heavy_lifting.title", "Schwerstarbeit");
        text(adv + "heavy_lifting.description", "Bewege ein Aufbewahrungsmöbel samt Inhalt mit einer Sackkarre");
        text(adv + "a_matter_of_style.title", "Eine Frage des Stils");
        text(adv + "a_matter_of_style.description", "Stelle ein Möbelstück in einem Stil her");
        text(adv + "style_icon.title", "Stilikone");
        text(adv + "style_icon.description", "Besitze ein Stück in jedem Stil: rustikal, modern, königlich und japanisch");
        text(adv + "tickling_the_ivories.title", "In die Tasten");
        text(adv + "tickling_the_ivories.description", "Spiele eine Note auf einem Klavier");
        text(adv + "bullseye.title", "Volltreffer!");
        text(adv + "bullseye.description", "Erziele 50 Punkte mit einem Dartpfeil");
        text(adv + "high_score.title", "Highscore");
        text(adv + "high_score.description", "Erziele 9000 Punkte oder mehr an einem Spielautomaten");
        text(adv + "fresh_laundry.title", "Frische Wäsche");
        text(adv + "fresh_laundry.description", "Wasche in einer Waschmaschine die Farbe aus etwas heraus");
        text(adv + "good_boy.title", "Wer ist ein Braver?");
        text(adv + "good_boy.description", "Lass dein Haustier aus einem Futternapf fressen");
        text(adv + "valued_customer.title", "Stammkunde");
        text(adv + "valued_customer.description", "Kaufe Möbel bei einem Möbeltischler");

        text("catalog.reikos_furniture.extras", "Werkzeug & Essen");
        text("catalog.reikos_furniture.hint", "Wähle links einen Bereich und klicke auf ein Möbelstück, um zu sehen, wie es "
                + "hergestellt wird. Scrolle über dem Rezept für andere Hölzer und Farben.");
        text("catalog.reikos_furniture.made_in", "Hergestellt in: %s");
        text("catalog.reikos_furniture.no_recipe", "Nicht herstellbar");
        text("catalog.reikos_furniture.pieces", "%s Möbelstücke");
        text("catalog.reikos_furniture.variants", "Variante %s von %s");
        text("catalog.reikos_furniture.workbench", "Möbelwerkbank: %s Bretter");

        String config = "config.reikos_furniture.";
        text(config + "appliance_speed", "Gerätegeschwindigkeit");
        text(config + "dolly_carries_full_storage", "Sackkarre trägt volle Möbel");
        text(config + "gameplay", "Spielmechanik");
        text(config + "laundry_cycle_seconds", "Waschgang (Sekunden)");
        text(config + "modded_woods", "Hölzer anderer Mods");
        text(config + "modded_woods.enabled", "Möbel aus Hölzern anderer Mods");
        text(config + "modded_woods.styles", "Stile aus Hölzern anderer Mods");
        text(config + "safe_owner_only", "Tresore öffnen sich nur für ihren Besitzer");
        text(config + "village_workshop_weight", "Häufigkeit von Werkstätten in Dörfern");
        text(config + "world", "Welt");
        text("reikos_furniture.configuration.title", "Reiko's Furniture – Einstellungen");

        text("container.reikos_furniture.furniture_workbench", "Möbelwerkbank");
        text("container.reikos_furniture.furniture_workbench.cost", "Benötigt %s Bretter");
        text("container.reikos_furniture.microwave", "Mikrowelle");
        text("container.reikos_furniture.stove", "Herd");
        text("emi.category.reikos_furniture.appliance", "Küchengeräte");
        text("emi.category.reikos_furniture.carpentry", "Möbelwerkbank");
        text("recipe_category.reikos_furniture.appliance", "Küchengeräte");
        text("recipe_category.reikos_furniture.carpentry", "Möbelwerkbank");
        text("recipe.reikos_furniture.appliance_time", "Dauert %s s");
        text("entity.minecraft.villager.furniture_maker", "Möbeltischler");
        text("entity.minecraft.villager.reikos_furniture.furniture_maker", "Möbeltischler");

        String item = "item.reikos_furniture.";
        text(item + "coffee", "Kaffee");
        text(item + "hot_cocoa", "Heiße Schokolade");
        text(item + "fruit_smoothie", "Fruchtsmoothie");
        text(item + "toast", "Toast");
        text(item + "espresso", "Espresso");
        text(item + "latte", "Milchkaffee");
        text(item + "furniture_catalog", "Möbelkatalog");
        text(item + "furniture_catalog.tooltip", "Alle Möbelstücke, nach Raum und Stil");
        text(item + "hammer", "Möbelhammer");
        text(item + "hammer.tooltip", "Rechtsklick auf ein Möbelstück dreht es (beim Schleichen andersherum)");
        text(item + "stain", "Beize: %s");
        text(item + "moving_dolly", "Sackkarre");
        text(item + "moving_dolly.tooltip", "Rechtsklick auf ein Aufbewahrungsmöbel bewegt es samt Inhalt");
        text(item + "moving_dolly.carrying", "Trägt: %s");
        text("itemGroup.reikos_furniture", "Reiko's Furniture");
        text("itemGroup.reikos_furniture.styles", "Reiko's Furniture: Stile");

        String message = "message.reikos_furniture.";
        text(message + "arcade_score", "Neuer Highscore: %s!");
        text(message + "bird_cage_empty", "Komm mit einem Papagei auf der Schulter, um ihn hineinzusetzen");
        text(message + "clock_time", "Es ist %s");
        text(message + "dart_score", "Du hast %s Punkte erzielt");
        text(message + "dolly_no_room", "Hier ist kein Platz dafür");
        text(message + "dolly_not_empty", "Leere es zuerst (der Server erlaubt es nicht, volle Möbel zu bewegen)");
        text(message + "dolly_not_storage", "Die Sackkarre trägt nur Aufbewahrungsmöbel");
        text(message + "dolly_not_yours", "Dieser Tresor gehört dir nicht");
        text(message + "hammer_cannot_turn", "Dieses Möbelstück kann nicht mit dem Hammer gedreht werden");
        text(message + "ladder_cannot_slide", "Dort gibt es nichts zum Anlehnen");
        text(message + "laundry_busy", "Warte, bis der Waschgang fertig ist");
        text(message + "safe_locked", "Dieser Tresor gehört %s");

        painting("big_wave", "Die große Welle");
        painting("cat", "Katze");
        painting("city_lights", "Lichter der Stadt");
        painting("fruit_bowl", "Obstschale");
        painting("lighthouse", "Leuchtturm");
        painting("meadow", "Wiese");
        painting("mountains", "Berge");
        painting("night_hills", "Hügel bei Nacht");
        painting("sailboat", "Segelboot");
        painting("shapes", "Formen");
        painting("still_life", "Stillleben");
        painting("sunset", "Sonnenuntergang");

        String room = "room.reikos_furniture.";
        text(room + "bedroom", "Schlafzimmer");
        text(room + "kitchen", "Küche");
        text(room + "dining", "Esszimmer");
        text(room + "livingroom", "Wohnzimmer");
        text(room + "bathroom", "Badezimmer");
        text(room + "office", "Büro");
        text(room + "outdoor", "Außenbereich");
        text(room + "kids", "Kinderzimmer");
        text(room + "laundry", "Waschküche");
        text(room + "music", "Musikzimmer");
        text(room + "gameroom", "Spielzimmer");
        text(room + "pets", "Haustierecke");
        text(room + "decor", "Dekoration");
        text(room + "garage", "Garage");
        text(room + "library", "Bibliothek");
        text(room + "bar", "Hausbar");
        text(room + "nursery", "Babyzimmer & Hobbyraum");
        text("style.reikos_furniture.rustic", "Rustikaler Stil");
        text("style.reikos_furniture.modern", "Moderner Stil");
        text("style.reikos_furniture.royal", "Königlicher Stil");
        text("style.reikos_furniture.japanese", "Japanischer Stil");

        String sub = "subtitles.reikos_furniture.";
        text(sub + "arcade_blip", "Spielautomat piept");
        text(sub + "blender", "Mixer surrt");
        text(sub + "coffee_brewing", "Kaffee läuft durch");
        text(sub + "doorbell", "Türklingel läutet");
        text(sub + "dryer_running", "Trockner rumpelt");
        text(sub + "globe_spin", "Globus dreht sich");
        text(sub + "microwave_ding", "Mikrowelle piept");
        text(sub + "sliding_door", "Schiebetür gleitet");
        text(sub + "toaster_pop", "Toaster springt auf");
        text(sub + "toilet_flush", "Toilettenspülung rauscht");
        text(sub + "toolbox_latch", "Werkzeugkasten klickt");
        text(sub + "tv_static", "Fernseher rauscht");
        text(sub + "washer_running", "Waschmaschine läuft");
        text(sub + "workbench_saw", "Säge sägt");
    }

    private void painting(String id, String title) {
        text("painting.reikos_furniture." + id + ".title", title);
        text("painting.reikos_furniture." + id + ".author", "Reiko's Furniture");
    }

    @Override
    public String locale() {
        return "de_de";
    }

    @Override
    public String oakWord() {
        return "Eichenholz";
    }

    @Override
    public String name(Noun noun, @Nullable FurnitureWood wood, @Nullable DyeColor color, @Nullable Style style) {
        int form = noun.gender().ordinal(); // M, F, N
        StringBuilder name = new StringBuilder();
        if (color != null) {
            name.append(new String[][] {COLORS_M, COLORS_F, COLORS_N}[form][color.ordinal()]).append(' ');
        }
        if (style != null) {
            String adjective = STYLES.get(style)[form];
            name.append(name.isEmpty() ? adjective : lowerFirst(adjective)).append(' ');
        }
        if (wood != null) {
            name.append(WOODS[index(wood)]).append('-');
        }
        return name.append(noun.text()).toString();
    }
}
