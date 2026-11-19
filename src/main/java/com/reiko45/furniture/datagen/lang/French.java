package com.reiko45.furniture.datagen.lang;

import static com.reiko45.furniture.datagen.lang.Grammar.Gender.F;
import static com.reiko45.furniture.datagen.lang.Grammar.Gender.M;

import com.reiko45.furniture.core.Style;
import java.util.EnumMap;
import java.util.Map;

/** French (France). */
public class French extends RomanceGrammar {
    private static final String[] WOODS_M = {"en chêne", "en sapin", "en bouleau", "en acajou", "en acacia", "en chêne noir",
            "en palétuvier", "en cerisier", "en bambou", "carmin", "biscornu"};
    private static final String[] WOODS_F = {"en chêne", "en sapin", "en bouleau", "en acajou", "en acacia", "en chêne noir",
            "en palétuvier", "en cerisier", "en bambou", "carmin", "biscornue"};
    private static final String[] COLORS_M = {"blanc", "orange", "magenta", "bleu clair", "jaune", "vert clair", "rose",
            "gris", "gris clair", "cyan", "violet", "bleu", "marron", "vert", "rouge", "noir"};
    private static final String[] COLORS_F = {"blanche", "orange", "magenta", "bleu clair", "jaune", "vert clair", "rose",
            "grise", "gris clair", "cyan", "violette", "bleue", "marron", "verte", "rouge", "noire"};
    private static final Map<Style, String[]> STYLES = new EnumMap<>(Map.of(
            Style.RUSTIC, new String[] {"rustique", "rustique"},
            Style.MODERN, new String[] {"moderne", "moderne"},
            Style.ROYAL, new String[] {"royal", "royale"},
            Style.JAPANESE, new String[] {"japonais", "japonaise"}));

    public French() {
        noun("bar_counter", "Comptoir de bar", M);
        noun("baseboard", "Plinthe", F);
        noun("bed", "Lit", M);
        noun("bench", "Banc", M);
        noun("book_stand", "Lutrin", M);
        noun("bookshelf", "Étagère à livres", F);
        noun("bunk_bed", "Lit superposé", M);
        noun("cat_tree", "Arbre à chat", M);
        noun("changing_table", "Table à langer", F);
        noun("coffee_table", "Table basse", F);
        noun("counter", "Plan de travail", M);
        noun("crib", "Berceau", M);
        noun("crown_molding", "Corniche", F);
        noun("cutting_board", "Planche à découper", F);
        noun("desk", "Bureau", M);
        noun("dining_chair", "Chaise de salle à manger", F);
        noun("dining_table", "Table de salle à manger", F);
        noun("dresser", "Commode", F);
        noun("filing_cabinet", "Classeur", M);
        noun("floor_lamp", "Lampadaire", M);
        noun("french_door", "Porte-fenêtre", F);
        noun("garden_bench", "Banc de jardin", M);
        noun("glass_cupboard", "Vaisselier vitré", M);
        noun("glass_upper_cabinet", "Meuble haut vitré", M);
        noun("globe", "Globe terrestre", M);
        noun("kids_chair", "Chaise pour enfant", F);
        noun("kids_table", "Table pour enfant", F);
        noun("kitchen_island", "Îlot de cuisine", M);
        noun("laundry_basket", "Panier à linge", M);
        noun("library_ladder", "Échelle de bibliothèque", F);
        noun("mirror", "Miroir", M);
        noun("nightstand", "Table de chevet", F);
        noun("parquet", "Parquet", M);
        noun("parquet_slab", "Dalle de parquet", F);
        noun("patio_chair", "Chaise de jardin", F);
        noun("patio_table", "Table de jardin", F);
        noun("pegboard", "Panneau perforé", M);
        noun("piano", "Piano", M);
        noun("piano_bench", "Banquette de piano", F);
        noun("picture_frame", "Cadre photo", M);
        noun("planter_box", "Jardinière", F);
        noun("pool_table", "Table de billard", F);
        noun("record_player", "Tourne-disque", M);
        noun("rocking_horse", "Cheval à bascule", M);
        noun("shutters", "Volet", M);
        noun("sideboard", "Buffet", M);
        noun("sink", "Évier", M);
        noun("sliding_door", "Porte coulissante", F);
        noun("table_lamp", "Lampe de chevet", F);
        noun("tall_bookcase", "Grande bibliothèque", F);
        noun("toy_chest", "Coffre à jouets", M);
        noun("tv_stand", "Meuble TV", M);
        noun("upper_cabinet", "Meuble haut", M);
        noun("vanity", "Coiffeuse", F);
        noun("wall_clock", "Horloge murale", F);
        noun("wall_lamp", "Applique murale", F);
        noun("wall_shelf", "Étagère murale", F);
        noun("wardrobe", "Armoire", F);
        noun("window", "Fenêtre", F);
        noun("wine_rack", "Casier à vin", M);
        noun("workshop_bench", "Établi", M);
        noun("writing_desk", "Secrétaire", M);
        noun("plant_stand", "Support à plantes", M);
        noun("window_flower_box", "Jardinière de fenêtre", F);
        noun("garden_shed_cabinet", "Armoire de jardin", F);
        noun("growing_pot", "Pot de culture", M);
        noun("hanging_planter", "Pot suspendu", M);
        noun("speaker", "Enceinte", F);
        noun("recliner", "Fauteuil inclinable", M);
        noun("easel", "Chevalet", M);
        noun("vase", "Vase", M);
        noun("floor_cushion", "Coussin de sol", M);
        noun("paint_pots", "Peinture", F);
        noun("mailbox", "Boîte aux lettres", F);
        noun("armchair", "Fauteuil", M);
        noun("baby_mobile", "Mobile pour bébé", M);
        noun("bar_stool", "Tabouret de bar", M);
        noun("blinds", "Store", M);
        noun("curtains", "Rideau", M);
        noun("feeding_bowl", "Gamelle", F);
        noun("gaming_chair", "Chaise gaming", F);
        noun("office_chair", "Chaise de bureau", F);
        noun("oil_drum", "Baril d'huile", M);
        noun("pendant_lamp", "Suspension", F);
        noun("pet_bed", "Panier pour animal", M);
        noun("rug", "Tapis", M);
        noun("sofa", "Canapé", M);
        noun("toolbox", "Boîte à outils", F);
        noun("towel_rack", "Porte-serviettes", M);
        noun("wallpaper", "Papier peint", M);

        single("light_switch", "Interrupteur");
        single("canvas", "Toile");
        single("pottery_wheel", "Tour de potier");
        single("terracotta_vase", "Vase en terre cuite");
        single("sewing_machine", "Machine à coudre");
        single("projector_screen", "Écran de projection");
        single("projector", "Projecteur");
        single("popcorn_machine", "Machine à pop-corn");
        single("movie_poster", "Affiche de film");
        single("terracotta_growing_pot", "Pot de culture en terre cuite");
        single("aquarium", "Aquarium");
        single("arcade_cabinet", "Borne d'arcade");
        single("bathroom_cabinet", "Armoire de toilette");
        single("bathroom_sink", "Lavabo");
        single("bathtub", "Baignoire");
        single("bird_cage", "Cage à oiseaux");
        single("blender", "Mixeur");
        single("ceiling_light", "Plafonnier");
        single("chalkboard", "Tableau noir");
        single("chandelier", "Lustre");
        single("coffee_machine", "Cafetière");
        single("computer", "Ordinateur");
        single("dartboard", "Cible de fléchettes");
        single("desk_lamp", "Lampe de bureau");
        single("doorbell", "Sonnette");
        single("drum_kit", "Batterie");
        single("dryer", "Sèche-linge");
        single("espresso_machine", "Machine à expresso");
        single("fireplace", "Cheminée");
        single("fridge", "Réfrigérateur");
        single("furniture_workbench", "Établi de menuiserie");
        single("grill", "Barbecue");
        single("metal_shelving", "Étagère métallique");
        single("microwave", "Micro-ondes");
        single("plate", "Assiette");
        single("reading_lamp", "Lampe de lecture");
        single("safe", "Coffre-fort");
        single("shower", "Douche");
        single("stove", "Cuisinière");
        single("toaster", "Grille-pain");
        single("toilet", "Toilettes");
        single("trash_can", "Poubelle");
        single("tv", "Télévision");
        single("washing_machine", "Lave-linge");
        single("white_tiles", "Carrelage blanc");
        single("black_tiles", "Carrelage noir");
        single("checkered_tiles", "Carrelage en damier");
        single("blue_tiles", "Carrelage bleu");
        single("green_tiles", "Carrelage vert");
        single("terracotta_tiles", "Carrelage en terre cuite");
        single("marble_tiles", "Carrelage en marbre");
        single("hexagon_tiles", "Carrelage hexagonal");

        String adv = "advancements.reikos_furniture.";
        text(adv + "root.title", "Reiko's Furniture");
        text(adv + "root.description", "Fabriquez votre premier meuble");
        text(adv + "woodworker.title", "Menuisier");
        text(adv + "woodworker.description", "Fabriquez un établi de menuiserie. Il est livré avec un catalogue de meubles");
        text(adv + "take_a_seat.title", "Asseyez-vous");
        text(adv + "take_a_seat.description", "Asseyez-vous sur une chaise, un canapé ou un tabouret");
        text(adv + "coffee_break.title", "Pause café");
        text(adv + "coffee_break.description", "Buvez une boisson préparée avec un appareil de la cuisine ou du bar");
        text(adv + "home_sweet_home.title", "Home sweet home");
        text(adv + "home_sweet_home.description", "Possédez un meuble de chaque pièce");
        text(adv + "interior_designer.title", "Décorateur d'intérieur");
        text(adv + "interior_designer.description", "Tournez un meuble avec un marteau");
        text(adv + "heavy_lifting.title", "Déménageur");
        text(adv + "heavy_lifting.description", "Déplacez un meuble de rangement avec un diable, avec tout son contenu");
        text(adv + "a_matter_of_style.title", "Une question de style");
        text(adv + "a_matter_of_style.description", "Fabriquez un meuble d'un style");
        text(adv + "style_icon.title", "Icône de style");
        text(adv + "style_icon.description", "Possédez un meuble de chaque style : rustique, moderne, royal et japonais");
        text(adv + "tickling_the_ivories.title", "Pianiste");
        text(adv + "tickling_the_ivories.description", "Jouez une note sur un piano");
        text(adv + "bullseye.title", "Dans le mille !");
        text(adv + "bullseye.description", "Marquez 50 points avec une fléchette");
        text(adv + "high_score.title", "Meilleur score");
        text(adv + "high_score.description", "Marquez 9000 points ou plus sur une borne d'arcade");
        text(adv + "fresh_laundry.title", "Linge propre");
        text(adv + "fresh_laundry.description", "Retirez la teinture de quelque chose dans un lave-linge");
        text(adv + "good_boy.title", "Qui est un bon chien ?");
        text(adv + "good_boy.description", "Faites manger votre animal dans une gamelle");
        text(adv + "valued_customer.title", "Client fidèle");
        text(adv + "valued_customer.description", "Achetez des meubles à un ébéniste");

        text("catalog.reikos_furniture.extras", "Outils et nourriture");
        text("catalog.reikos_furniture.hint", "Choisissez une section à gauche, puis cliquez sur un meuble pour voir comment le "
                + "fabriquer. Faites défiler sur la recette pour voir ses autres bois et couleurs.");
        text("catalog.reikos_furniture.made_in", "Fabriqué avec : %s");
        text("catalog.reikos_furniture.no_recipe", "Ne se fabrique pas");
        text("catalog.reikos_furniture.pieces", "%s meubles");
        text("catalog.reikos_furniture.variants", "Variante %s sur %s");
        text("catalog.reikos_furniture.craft", "Fabriquer");
        text("catalog.reikos_furniture.craft_all", "Tout fabriquer");
        text("catalog.reikos_furniture.missing", "Vous n'avez pas les matériaux pour cela");
        text("catalog.reikos_furniture.workbench", "Établi de menuiserie : %s planches");

        String config = "config.reikos_furniture.";
        text(config + "appliance_speed", "Vitesse des appareils");
        text(config + "dolly_carries_full_storage", "Le diable transporte les meubles pleins");
        text(config + "gameplay", "Jeu");
        text(config + "laundry_cycle_seconds", "Cycle de lavage (secondes)");
        text(config + "modded_woods", "Bois des autres mods");
        text(config + "modded_woods.enabled", "Meubles dans les bois des autres mods");
        text(config + "modded_woods.styles", "Styles dans les bois des autres mods");
        text(config + "safe_owner_only", "Les coffres-forts ne s'ouvrent que pour leur propriétaire");
        text(config + "village_workshop_weight", "Fréquence des ateliers dans les villages");
        text(config + "world", "Monde");
        text("reikos_furniture.configuration.title", "Paramètres de Reiko's Furniture");

        text("container.reikos_furniture.furniture_workbench", "Établi de menuiserie");
        text("container.reikos_furniture.furniture_workbench.cost", "Nécessite %s planches");
        text("container.reikos_furniture.microwave", "Micro-ondes");
        text("container.reikos_furniture.stove", "Cuisinière");
        text("emi.category.reikos_furniture.appliance", "Appareils de cuisine");
        text("emi.category.reikos_furniture.carpentry", "Établi de menuiserie");
        text("recipe_category.reikos_furniture.appliance", "Appareils de cuisine");
        text("recipe_category.reikos_furniture.carpentry", "Établi de menuiserie");
        text("recipe.reikos_furniture.appliance_time", "Prend %s s");
        text("entity.minecraft.villager.furniture_maker", "Ébéniste");
        text("entity.minecraft.villager.reikos_furniture.furniture_maker", "Ébéniste");

        String item = "item.reikos_furniture.";
        text(item + "coffee", "Café");
        text(item + "hot_cocoa", "Chocolat chaud");
        text(item + "fruit_smoothie", "Smoothie aux fruits");
        text(item + "toast", "Pain grillé");
        text(item + "espresso", "Expresso");
        text(item + "popcorn", "Pop-corn");
        text(item + "latte", "Café au lait");
        text(item + "furniture_catalog", "Catalogue de meubles");
        text(item + "furniture_catalog.tooltip", "Tous les meubles, par pièce et par style");
        text(item + "hammer", "Marteau de menuisier");
        text(item + "hammer.tooltip", "Clic droit sur un meuble pour le tourner (accroupi : dans l'autre sens)");
        text(item + "light_switch.tooltip", "Accroupi, clic droit sur des lampes pour les relier (%s reliées)");
        text(item + "watering_can", "Arrosoir");
        text(item + "watering_can.water", "Eau : %s/%s");
        text(item + "watering_can.tooltip", "Clic droit sur de l'eau, un évier ou une baignoire pour le remplir, puis sur des plantes pour les arroser (3×3)");
        text("message.reikos_furniture.watering_can_empty", "L'arrosoir est vide");
        text("message.reikos_furniture.projector_no_screen", "Il n'y a pas d'écran de projection devant l'objectif");
        text("container.reikos_furniture.easel", "Peinture");
        text("container.reikos_furniture.easel.sign", "Signer");
        text("container.reikos_furniture.easel.title", "Titre");
        text("container.reikos_furniture.easel.fill", "Remplir");
        text("container.reikos_furniture.easel.brush", "Pinceau");
        text("container.reikos_furniture.easel.signing", "Donnez-lui un titre. Une peinture signée ne peut plus être modifiée.");
        text("item.reikos_furniture.canvas.by", "par %s");
        text("item.reikos_furniture.canvas.unfinished", "Peinture inachevée");
        text("item.reikos_furniture.canvas.tooltip", "Posez-la sur un chevalet pour la peindre");
        text("message.reikos_furniture.lamp_linked", "Lampe reliée à l'interrupteur (%s sur %s)");
        text("message.reikos_furniture.lamp_unlinked", "Lampe détachée de l'interrupteur (%s sur %s)");
        text("message.reikos_furniture.switch_full", "Cet interrupteur commande déjà %s lampes sur %s");
        text("container.reikos_furniture.mailbox", "Boîte aux lettres de %s");
        text("container.reikos_furniture.mailbox.not_yours", "Seul %s peut lire le courrier de cette boîte aux lettres. Vous pouvez envoyer du courrier d'ici.");
        text("container.reikos_furniture.mailbox.send_to", "Envoyer à :");
        text("container.reikos_furniture.mailbox.send", "Envoyer");
        text("message.reikos_furniture.mail_sent", "Envoyé à %s");
        text("message.reikos_furniture.mail_partly_sent", "La boîte aux lettres de %s est pleine : ce qui n'a pas tenu est encore ici");
        text("message.reikos_furniture.mail_no_mailbox", "%s n'a pas de boîte aux lettres");
        text("message.reikos_furniture.mail_unknown_player", "Aucun joueur ne s'appelle %s");
        text("message.reikos_furniture.mail_nothing", "Placez ce que vous voulez envoyer dans les cases sous le nom");
        text("message.reikos_furniture.mail_received", "Vous avez du courrier de %s");
        text(item + "stain", "Teinte : %s");
        text(item + "moving_dolly", "Diable de déménagement");
        text(item + "moving_dolly.tooltip", "Clic droit sur un meuble de rangement pour le déplacer avec son contenu");
        text(item + "moving_dolly.carrying", "Transporte : %s");
        text("itemGroup.reikos_furniture", "Reiko's Furniture");
        text("itemGroup.reikos_furniture.styles", "Reiko's Furniture : styles");

        String message = "message.reikos_furniture.";
        text(message + "arcade_score", "Nouveau record : %s !");
        text(message + "bird_cage_empty", "Venez avec un perroquet sur l'épaule pour le mettre dedans");
        text(message + "clock_time", "Il est %s");
        text(message + "dart_score", "Vous avez marqué %s");
        text(message + "dolly_no_room", "Il n'y a pas de place ici");
        text(message + "dolly_not_empty", "Videz-le d'abord (le serveur n'autorise pas le déplacement des meubles pleins)");
        text(message + "dolly_not_storage", "Le diable ne transporte que des meubles de rangement");
        text(message + "dolly_not_yours", "Ce coffre-fort n'est pas à vous");
        text(message + "hammer_cannot_turn", "Ce meuble ne peut pas être tourné au marteau");
        text(message + "ladder_cannot_slide", "Il n'y a rien sur quoi s'appuyer ici");
        text(message + "laundry_busy", "Attendez la fin du cycle");
        text(message + "safe_locked", "Ce coffre-fort appartient à %s");

        painting("big_wave", "La grande vague");
        painting("cat", "Chat");
        painting("city_lights", "Lumières de la ville");
        painting("fruit_bowl", "Coupe de fruits");
        painting("lighthouse", "Phare");
        painting("meadow", "Prairie");
        painting("mountains", "Montagnes");
        painting("night_hills", "Collines la nuit");
        painting("sailboat", "Voilier");
        painting("shapes", "Formes");
        painting("still_life", "Nature morte");
        painting("sunset", "Coucher de soleil");

        String room = "room.reikos_furniture.";
        text(room + "bedroom", "Chambre");
        text(room + "kitchen", "Cuisine");
        text(room + "dining", "Salle à manger");
        text(room + "livingroom", "Salon");
        text(room + "bathroom", "Salle de bain");
        text(room + "office", "Bureau");
        text(room + "outdoor", "Extérieur");
        text(room + "kids", "Chambre d'enfant");
        text(room + "laundry", "Buanderie");
        text(room + "music", "Salle de musique");
        text(room + "gameroom", "Salle de jeux");
        text(room + "pets", "Coin des animaux");
        text(room + "decor", "Décoration");
        text(room + "garage", "Garage");
        text(room + "library", "Bibliothèque");
        text(room + "bar", "Bar maison");
        text(room + "nursery", "Chambre de bébé et loisirs");
        text(room + "greenhouse", "Serre et jardin");
        text(room + "theater", "Home cinéma");
        text(room + "studio", "Atelier d'art");
        text("style.reikos_furniture.rustic", "Style rustique");
        text("style.reikos_furniture.modern", "Style moderne");
        text("style.reikos_furniture.royal", "Style royal");
        text("style.reikos_furniture.japanese", "Style japonais");

        String sub = "subtitles.reikos_furniture.";
        text(sub + "arcade_blip", "Borne d'arcade bipe");
        text(sub + "blender", "Mixeur vrombit");
        text(sub + "coffee_brewing", "Café coule");
        text(sub + "doorbell", "Sonnette retentit");
        text(sub + "dryer_running", "Sèche-linge tourne");
        text(sub + "globe_spin", "Globe tourne");
        text(sub + "microwave_ding", "Micro-ondes bipe");
        text(sub + "sliding_door", "Porte coulissante glisse");
        text(sub + "toaster_pop", "Grille-pain saute");
        text(sub + "toilet_flush", "Chasse d'eau");
        text(sub + "toolbox_latch", "Loquet de boîte à outils");
        text(sub + "projector", "Un projecteur ronronne");
        text(sub + "popcorn_popping", "Le pop-corn éclate");
        text(sub + "pottery_wheel", "Un tour de potier tourne");
        text(sub + "sewing_machine", "Une machine à coudre cliquette");
        text(sub + "tv_static", "Télévision grésille");
        text(sub + "washer_running", "Lave-linge tourne");
        text(sub + "workbench_saw", "Scie coupe");
    }

    private void painting(String id, String title) {
        text("painting.reikos_furniture." + id + ".title", title);
        text("painting.reikos_furniture." + id + ".author", "Reiko's Furniture");
    }

    @Override
    public String locale() {
        return "fr_fr";
    }

    @Override
    public String oakWord() {
        return "chêne";
    }

    @Override
    protected String[] woodsMasculine() {
        return WOODS_M;
    }

    @Override
    protected String[] woodsFeminine() {
        return WOODS_F;
    }

    @Override
    protected String[] colorsMasculine() {
        return COLORS_M;
    }

    @Override
    protected String[] colorsFeminine() {
        return COLORS_F;
    }

    @Override
    protected Map<Style, String[]> styles() {
        return STYLES;
    }
}
