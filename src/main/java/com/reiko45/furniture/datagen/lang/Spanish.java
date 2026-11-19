package com.reiko45.furniture.datagen.lang;

import static com.reiko45.furniture.datagen.lang.Grammar.Gender.F;
import static com.reiko45.furniture.datagen.lang.Grammar.Gender.M;

import com.reiko45.furniture.core.Style;
import java.util.EnumMap;
import java.util.Map;

/** Spanish (Spain). */
public class Spanish extends RomanceGrammar {
    private static final String[] WOODS_M = {"de roble", "de abeto", "de abedul", "de jungla", "de acacia", "de roble oscuro",
            "de mangle", "de cerezo", "de bambú", "carmesí", "distorsionado"};
    private static final String[] WOODS_F = {"de roble", "de abeto", "de abedul", "de jungla", "de acacia", "de roble oscuro",
            "de mangle", "de cerezo", "de bambú", "carmesí", "distorsionada"};
    private static final String[] COLORS_M = {"blanco", "naranja", "magenta", "azul claro", "amarillo", "verde lima", "rosa",
            "gris", "gris claro", "cian", "morado", "azul", "marrón", "verde", "rojo", "negro"};
    private static final String[] COLORS_F = {"blanca", "naranja", "magenta", "azul claro", "amarilla", "verde lima", "rosa",
            "gris", "gris claro", "cian", "morada", "azul", "marrón", "verde", "roja", "negra"};
    private static final Map<Style, String[]> STYLES = new EnumMap<>(Map.of(
            Style.RUSTIC, new String[] {"rústico", "rústica"},
            Style.MODERN, new String[] {"moderno", "moderna"},
            Style.ROYAL, new String[] {"regio", "regia"},
            Style.JAPANESE, new String[] {"japonés", "japonesa"},
            Style.MEDIEVAL, new String[] {"medieval", "medieval"},
            Style.VICTORIAN, new String[] {"victoriano", "victoriana"},
            Style.FUTURISTIC, new String[] {"futurista", "futurista"},
            Style.BEACH, new String[] {"playero", "playera"}));

    public Spanish() {
        // Wooden pieces.
        noun("bar_counter", "Barra", F);
        noun("baseboard", "Rodapié", M);
        noun("bed", "Cama", F);
        noun("bench", "Banco", M);
        noun("book_stand", "Atril", M);
        noun("bookshelf", "Estantería", F);
        noun("bunk_bed", "Litera", F);
        noun("cat_tree", "Rascador para gatos", M);
        noun("changing_table", "Cambiador", M);
        noun("coffee_table", "Mesa de centro", F);
        noun("counter", "Encimera", F);
        noun("crib", "Cuna", F);
        noun("crown_molding", "Moldura de techo", F);
        noun("cutting_board", "Tabla de cortar", F);
        noun("desk", "Escritorio", M);
        noun("dining_chair", "Silla de comedor", F);
        noun("dining_table", "Mesa de comedor", F);
        noun("dresser", "Cómoda", F);
        noun("filing_cabinet", "Archivador", M);
        noun("floor_lamp", "Lámpara de pie", F);
        noun("french_door", "Puerta francesa", F);
        noun("garden_bench", "Banco de jardín", M);
        noun("glass_cupboard", "Vitrina", F);
        noun("glass_upper_cabinet", "Armario alto acristalado", M);
        noun("globe", "Globo terráqueo", M);
        noun("kids_chair", "Silla infantil", F);
        noun("kids_table", "Mesa infantil", F);
        noun("kitchen_island", "Isla de cocina", F);
        noun("laundry_basket", "Cesto de la ropa", M);
        noun("library_ladder", "Escalera de biblioteca", F);
        noun("mirror", "Espejo", M);
        noun("nightstand", "Mesita de noche", F);
        noun("parquet", "Parqué", M);
        noun("parquet_slab", "Losa de parqué", F);
        noun("patio_chair", "Silla de jardín", F);
        noun("patio_table", "Mesa de jardín", F);
        noun("pegboard", "Panel perforado", M);
        noun("piano", "Piano", M);
        noun("piano_bench", "Banqueta de piano", F);
        noun("picture_frame", "Marco de fotos", M);
        noun("planter_box", "Jardinera", F);
        noun("pool_table", "Mesa de billar", F);
        noun("record_player", "Tocadiscos", M);
        noun("rocking_horse", "Caballito de balancín", M);
        noun("shutters", "Contraventana", F);
        noun("sideboard", "Aparador", M);
        noun("sink", "Fregadero", M);
        noun("sliding_door", "Puerta corredera", F);
        noun("table_lamp", "Lámpara de mesa", F);
        noun("tall_bookcase", "Librería alta", F);
        noun("toy_chest", "Baúl de juguetes", M);
        noun("tv_stand", "Mueble de televisión", M);
        noun("upper_cabinet", "Armario alto", M);
        noun("vanity", "Tocador", M);
        noun("wall_clock", "Reloj de pared", M);
        noun("wall_lamp", "Aplique", M);
        noun("wall_shelf", "Estante de pared", M);
        noun("wardrobe", "Armario", M);
        noun("window", "Ventana", F);
        noun("wine_rack", "Botellero", M);
        noun("workshop_bench", "Banco de trabajo", M);
        noun("writing_desk", "Secreter", M);
        noun("plant_stand", "Soporte para plantas", M);
        noun("window_flower_box", "Jardinera de ventana", F);
        noun("garden_shed_cabinet", "Armario de jardín", M);
        // Colored pieces.
        noun("growing_pot", "Maceta de cultivo", F);
        noun("hanging_planter", "Maceta colgante", F);
        noun("speaker", "Altavoz", M);
        noun("recliner", "Sillón reclinable", M);
        noun("easel", "Caballete", M);
        noun("vase", "Jarrón", M);
        noun("floor_cushion", "Cojín de suelo", M);
        noun("paint_pots", "Pintura", F);
        noun("present", "Regalo", M);
        noun("stocking", "Calcetín navideño", M);
        noun("concrete_table", "Mesa de hormigón", F);
        noun("concrete_bench", "Banco de hormigón", M);
        noun("concrete_counter", "Encimera de hormigón", F);
        noun("concrete_planter", "Jardinera de hormigón", F);
        noun("mailbox", "Buzón", M);
        noun("armchair", "Sillón", M);
        noun("baby_mobile", "Móvil de cuna", M);
        noun("bar_stool", "Taburete de bar", M);
        noun("blinds", "Persiana", F);
        noun("curtains", "Cortina", F);
        noun("feeding_bowl", "Comedero", M);
        noun("gaming_chair", "Silla gaming", F);
        noun("office_chair", "Silla de oficina", F);
        noun("oil_drum", "Bidón de aceite", M);
        noun("pendant_lamp", "Lámpara colgante", F);
        noun("pet_bed", "Cama para mascotas", F);
        noun("rug", "Alfombra", F);
        noun("sofa", "Sofá", M);
        noun("toolbox", "Caja de herramientas", F);
        noun("towel_rack", "Toallero", M);
        noun("wallpaper", "Papel pintado", M);

        single("light_switch", "Interruptor de luz");
        // Stone, copper and wrought-iron furniture.
        String[][] stones = {{"white_marble", "de mármol blanco"}, {"black_marble", "de mármol negro"}, {"quartz", "de cuarzo"},
                {"blackstone", "de piedra negra"}, {"stone_brick", "de ladrillos de piedra"}};
        String[][] stonePieces = {{"dining_table", "Mesa de comedor"}, {"bench", "Banco"}, {"coffee_table", "Mesa de centro"},
                {"counter", "Encimera"}, {"planter", "Jardinera"}};
        for (String[] stone : stones) {
            for (String[] piece : stonePieces) {
                single(stone[0] + "_" + piece[0], piece[1] + " " + stone[1]);
            }
        }
        String[][] copperPieces = {{"lamp", "Lámpara de cobre"}, {"table", "Mesa de cobre"}, {"chair", "Silla de cobre"}};
        String[][] ages = {{"", ""}, {"exposed_", " expuesto"}, {"weathered_", " erosionado"}, {"oxidized_", " oxidado"}};
        for (String[] piece : copperPieces) {
            for (String[] age : ages) {
                single(age[0] + "copper_" + piece[0], piece[1] + age[1]);
                single("waxed_" + age[0] + "copper_" + piece[0], piece[1] + age[1] + " encerado");
            }
        }
        single("wrought_iron_garden_chair", "Silla de jardín de hierro forjado");
        single("wrought_iron_garden_table", "Mesa de jardín de hierro forjado");
        single("wrought_iron_bench", "Banco de hierro forjado");
        single("wrought_iron_lamp_post", "Farola de hierro forjado");
        single("christmas_tree", "Árbol de Navidad");
        single("warm_string_lights", "Guirnalda de luces cálidas");
        single("colorful_string_lights", "Guirnalda de luces de colores");
        single("wreath", "Corona navideña");
        single("snow_globe", "Bola de nieve");
        single("gravestone", "Lápida");
        single("fake_cobweb", "Telaraña falsa");
        single("hanging_bats", "Murciélagos colgantes");
        single("candy_bowl", "Cuenco de caramelos");
        single("witch_cauldron", "Caldero de bruja");
        single("canvas", "Lienzo");
        single("pottery_wheel", "Torno de alfarero");
        single("terracotta_vase", "Jarrón de terracota");
        single("sewing_machine", "Máquina de coser");
        single("projector_screen", "Pantalla de proyección");
        single("projector", "Proyector");
        single("popcorn_machine", "Máquina de palomitas");
        single("movie_poster", "Póster de película");
        single("terracotta_growing_pot", "Maceta de cultivo de terracota");
        single("aquarium", "Acuario");
        single("arcade_cabinet", "Máquina recreativa");
        single("bathroom_cabinet", "Armario de baño");
        single("bathroom_sink", "Lavabo");
        single("bathtub", "Bañera");
        single("bird_cage", "Jaula de pájaros");
        single("blender", "Batidora");
        single("ceiling_light", "Plafón");
        single("chalkboard", "Pizarra");
        single("chandelier", "Lámpara de araña");
        single("coffee_machine", "Cafetera");
        single("computer", "Ordenador");
        single("dartboard", "Diana");
        single("desk_lamp", "Flexo");
        single("doorbell", "Timbre");
        single("drum_kit", "Batería");
        single("dryer", "Secadora");
        single("espresso_machine", "Máquina de café expreso");
        single("fireplace", "Chimenea");
        single("fridge", "Frigorífico");
        single("furniture_workbench", "Mesa de carpintería");
        single("grill", "Barbacoa");
        single("metal_shelving", "Estantería metálica");
        single("microwave", "Microondas");
        single("plate", "Plato");
        single("reading_lamp", "Lámpara de lectura");
        single("safe", "Caja fuerte");
        single("shower", "Ducha");
        single("stove", "Horno");
        single("toaster", "Tostadora");
        single("toilet", "Inodoro");
        single("trash_can", "Cubo de basura");
        single("tv", "Televisor");
        single("washing_machine", "Lavadora");
        single("white_tiles", "Baldosas blancas");
        single("black_tiles", "Baldosas negras");
        single("checkered_tiles", "Baldosas a cuadros");
        single("blue_tiles", "Baldosas azules");
        single("green_tiles", "Baldosas verdes");
        single("terracotta_tiles", "Baldosas de terracota");
        single("marble_tiles", "Baldosas de mármol");
        single("hexagon_tiles", "Baldosas hexagonales");

        String adv = "advancements.reikos_furniture.";
        text(adv + "root.title", "Reiko's Furniture");
        text(adv + "root.description", "Fabrica tu primer mueble");
        text(adv + "woodworker.title", "Carpintero");
        text(adv + "woodworker.description", "Fabrica una mesa de carpintería. Viene con un catálogo de muebles");
        text(adv + "take_a_seat.title", "Toma asiento");
        text(adv + "take_a_seat.description", "Siéntate en una silla, un sofá o un taburete");
        text(adv + "coffee_break.title", "Pausa para el café");
        text(adv + "coffee_break.description", "Tómate una bebida hecha con un electrodoméstico de la cocina o del bar");
        text(adv + "home_sweet_home.title", "Hogar, dulce hogar");
        text(adv + "home_sweet_home.description", "Ten un mueble de cada habitación");
        text(adv + "interior_designer.title", "Interiorista");
        text(adv + "interior_designer.description", "Gira un mueble con un martillo");
        text(adv + "heavy_lifting.title", "Trabajo pesado");
        text(adv + "heavy_lifting.description", "Mueve un mueble de almacenamiento con una carretilla, con todo lo que lleva dentro");
        text(adv + "a_matter_of_style.title", "Cuestión de estilo");
        text(adv + "a_matter_of_style.description", "Fabrica un mueble de un estilo");
        text(adv + "style_icon.title", "Icono de estilo");
        text(adv + "style_icon.description", "Ten un mueble de cada uno de los ocho estilos");
        text(adv + "tickling_the_ivories.title", "Al piano");
        text(adv + "tickling_the_ivories.description", "Toca una nota en un piano");
        text(adv + "bullseye.title", "¡En el blanco!");
        text(adv + "bullseye.description", "Consigue 50 puntos con un dardo");
        text(adv + "high_score.title", "Récord");
        text(adv + "high_score.description", "Consigue 9000 puntos o más en una máquina recreativa");
        text(adv + "fresh_laundry.title", "Ropa limpia");
        text(adv + "fresh_laundry.description", "Quita el tinte de algo en una lavadora");
        text(adv + "good_boy.title", "¿Quién es un buen chico?");
        text(adv + "good_boy.description", "Haz que tu mascota coma de un comedero");
        text(adv + "valued_customer.title", "Cliente preferente");
        text(adv + "valued_customer.description", "Compra muebles a un mueblista");

        text("catalog.reikos_furniture.extras", "Herramientas y comida");
        text("catalog.reikos_furniture.hint", "Elige una sección a la izquierda y haz clic en un mueble para ver cómo se fabrica. "
                + "Desplázate sobre la receta para ver sus otras maderas y colores.");
        text("catalog.reikos_furniture.made_in", "Se hace en: %s");
        text("catalog.reikos_furniture.no_recipe", "No se puede fabricar");
        text("catalog.reikos_furniture.pieces", "%s muebles");
        text("catalog.reikos_furniture.variants", "Variante %s de %s");
        text("catalog.reikos_furniture.craft", "Fabricar");
        text("catalog.reikos_furniture.craft_all", "Fabricar todo");
        text("catalog.reikos_furniture.missing", "No tienes los materiales para esto");
        text("tooltip.reikos_furniture.progress", "Progreso: %s%%");
        text("tooltip.reikos_furniture.done_in", "Listo en %s s");
        text("tooltip.reikos_furniture.owner", "Dueño: %s");
        text("tooltip.reikos_furniture.mail", "Correo esperando: %s");
        text("tooltip.reikos_furniture.lamps", "Lámparas conectadas: %s");
        text("tooltip.reikos_furniture.fish", "Peces: %s de %s");
        text("tooltip.reikos_furniture.growth", "Crecimiento: %s de %s");
        text("tooltip.reikos_furniture.painting", "«%s», de %s");
        text("config.jade.plugin_reikos_furniture.furniture", "Reiko's Furniture");
        text("config.waila.plugin_reikos_furniture.furniture", "Reiko's Furniture");
        text("catalog.reikos_furniture.workbench", "Mesa de carpintería: %s tablones");

        String config = "config.reikos_furniture.";
        text(config + "appliance_speed", "Velocidad de los electrodomésticos");
        text(config + "dolly_carries_full_storage", "La carretilla lleva muebles llenos");
        text(config + "gameplay", "Jugabilidad");
        text(config + "laundry_cycle_seconds", "Ciclo de lavado (segundos)");
        text(config + "modded_woods", "Maderas de otros mods");
        text(config + "modded_woods.enabled", "Muebles en maderas de otros mods");
        text(config + "modded_woods.styles", "Estilos en maderas de otros mods");
        text(config + "safe_owner_only", "Las cajas fuertes solo se abren para su dueño");
        text(config + "village_workshop_weight", "Frecuencia de talleres en las aldeas");
        text(config + "world", "Mundo");
        text("reikos_furniture.configuration.title", "Ajustes de Reiko's Furniture");

        text("container.reikos_furniture.furniture_workbench", "Mesa de carpintería");
        text("container.reikos_furniture.furniture_workbench.cost", "Necesita %s tablones");
        text("container.reikos_furniture.microwave", "Microondas");
        text("container.reikos_furniture.stove", "Horno");
        text("emi.category.reikos_furniture.appliance", "Electrodomésticos de cocina");
        text("emi.category.reikos_furniture.carpentry", "Mesa de carpintería");
        text("recipe_category.reikos_furniture.appliance", "Electrodomésticos de cocina");
        text("recipe_category.reikos_furniture.carpentry", "Mesa de carpintería");
        text("recipe.reikos_furniture.appliance_time", "Tarda %s s");
        text("entity.minecraft.villager.furniture_maker", "Mueblista");
        text("entity.minecraft.villager.reikos_furniture.furniture_maker", "Mueblista");

        String item = "item.reikos_furniture.";
        text(item + "coffee", "Café");
        text(item + "hot_cocoa", "Chocolate caliente");
        text(item + "fruit_smoothie", "Batido de frutas");
        text(item + "toast", "Tostada");
        text(item + "espresso", "Café expreso");
        text(item + "popcorn", "Palomitas");
        text(item + "latte", "Café con leche");
        text(item + "furniture_catalog", "Catálogo de muebles");
        text(item + "furniture_catalog.tooltip", "Todos los muebles, por habitación y estilo");
        text(item + "hammer", "Martillo de muebles");
        text(item + "hammer.tooltip", "Clic derecho en un mueble para girarlo (agachado: hacia el otro lado)");
        text(item + "light_switch.tooltip", "Agáchate y haz clic derecho en lámparas para conectarlas (%s conectadas)");
        text(item + "watering_can", "Regadera");
        text(item + "watering_can.water", "Agua: %s/%s");
        text(item + "watering_can.tooltip", "Haz clic derecho en agua, un fregadero o una bañera para llenarla y luego en plantas para regarlas (3×3)");
        text("message.reikos_furniture.watering_can_empty", "La regadera está vacía");
        text("message.reikos_furniture.projector_no_screen", "No hay ninguna pantalla de proyección delante del objetivo");
        text("block.reikos_furniture.present.full", "Hay algo dentro...");
        text("container.reikos_furniture.easel", "Pintura");
        text("container.reikos_furniture.easel.sign", "Firmar");
        text("container.reikos_furniture.easel.title", "Título");
        text("container.reikos_furniture.easel.fill", "Relleno");
        text("container.reikos_furniture.easel.brush", "Pincel");
        text("container.reikos_furniture.easel.signing", "Ponle un título. Una pintura firmada ya no se puede cambiar.");
        text("item.reikos_furniture.canvas.by", "de %s");
        text("item.reikos_furniture.canvas.unfinished", "Pintura sin terminar");
        text("item.reikos_furniture.canvas.tooltip", "Ponlo en un caballete para pintarlo");
        text("message.reikos_furniture.lamp_linked", "Lámpara conectada al interruptor (%s de %s)");
        text("message.reikos_furniture.lamp_unlinked", "Lámpara desconectada del interruptor (%s de %s)");
        text("message.reikos_furniture.switch_full", "Este interruptor ya controla %s de %s lámparas");
        text("container.reikos_furniture.mailbox", "Buzón de %s");
        text("container.reikos_furniture.mailbox.not_yours", "Solo %s puede leer el correo de este buzón. Puedes enviar correo desde aquí.");
        text("container.reikos_furniture.mailbox.send_to", "Enviar a:");
        text("container.reikos_furniture.mailbox.send", "Enviar");
        text("message.reikos_furniture.mail_sent", "Enviado a %s");
        text("message.reikos_furniture.mail_partly_sent", "El buzón de %s está lleno: lo que no cabía sigue aquí");
        text("message.reikos_furniture.mail_no_mailbox", "%s no tiene buzón");
        text("message.reikos_furniture.mail_unknown_player", "No hay ningún jugador llamado %s");
        text("message.reikos_furniture.mail_nothing", "Pon lo que quieras enviar en las casillas bajo el nombre");
        text("message.reikos_furniture.mail_received", "Tienes correo de %s");
        text(item + "stain", "Tinte: %s");
        text(item + "moving_dolly", "Carretilla de mudanzas");
        text(item + "moving_dolly.tooltip", "Clic derecho en un mueble de almacenamiento para moverlo con su contenido");
        text(item + "moving_dolly.carrying", "Lleva: %s");
        text("itemGroup.reikos_furniture", "Reiko's Furniture");
        text("itemGroup.reikos_furniture.styles", "Reiko's Furniture: estilos");

        String message = "message.reikos_furniture.";
        text(message + "arcade_score", "¡Nuevo récord: %s!");
        text(message + "bird_cage_empty", "Trae un loro en el hombro para meterlo");
        text(message + "clock_time", "Son las %s");
        text(message + "dart_score", "Has conseguido %s");
        text(message + "dolly_no_room", "Aquí no cabe");
        text(message + "dolly_not_empty", "Vacíalo primero (el servidor no permite mover muebles llenos)");
        text(message + "dolly_not_storage", "La carretilla solo lleva muebles de almacenamiento");
        text(message + "dolly_not_yours", "Esta caja fuerte no es tuya");
        text(message + "hammer_cannot_turn", "Este mueble no se puede girar con el martillo");
        text(message + "ladder_cannot_slide", "Ahí no hay nada en lo que apoyarla");
        text(message + "laundry_busy", "Espera a que termine el ciclo");
        text(message + "safe_locked", "Esta caja fuerte es de %s");

        painting("big_wave", "La gran ola");
        painting("cat", "Gato");
        painting("city_lights", "Luces de la ciudad");
        painting("fruit_bowl", "Frutero");
        painting("lighthouse", "Faro");
        painting("meadow", "Pradera");
        painting("mountains", "Montañas");
        painting("night_hills", "Colinas de noche");
        painting("sailboat", "Velero");
        painting("shapes", "Formas");
        painting("still_life", "Bodegón");
        painting("sunset", "Atardecer");

        String room = "room.reikos_furniture.";
        text(room + "bedroom", "Dormitorio");
        text(room + "kitchen", "Cocina");
        text(room + "dining", "Comedor");
        text(room + "livingroom", "Salón");
        text(room + "bathroom", "Baño");
        text(room + "office", "Despacho");
        text(room + "outdoor", "Exterior");
        text(room + "kids", "Habitación infantil");
        text(room + "laundry", "Lavadero");
        text(room + "music", "Sala de música");
        text(room + "gameroom", "Sala de juegos");
        text(room + "pets", "Rincón de mascotas");
        text(room + "decor", "Decoración");
        text(room + "garage", "Garaje");
        text(room + "library", "Biblioteca");
        text(room + "bar", "Bar en casa");
        text(room + "nursery", "Cuarto del bebé y manualidades");
        text(room + "greenhouse", "Invernadero y jardín");
        text(room + "theater", "Cine en casa");
        text(room + "studio", "Taller de arte");
        text(room + "holiday", "Fiestas");
        text(room + "stone", "Piedra y hormigón");
        text(room + "metal", "Cobre y hierro");
        text("style.reikos_furniture.rustic", "Estilo rústico");
        text("style.reikos_furniture.modern", "Estilo moderno");
        text("style.reikos_furniture.royal", "Estilo regio");
        text("style.reikos_furniture.japanese", "Estilo japonés");
        text("style.reikos_furniture.medieval", "Estilo medieval");
        text("style.reikos_furniture.victorian", "Estilo victoriano");
        text("style.reikos_furniture.futuristic", "Estilo futurista");
        text("style.reikos_furniture.beach", "Estilo casa de playa");

        String sub = "subtitles.reikos_furniture.";
        text(sub + "arcade_blip", "Máquina recreativa pita");
        text(sub + "blender", "Batidora zumba");
        text(sub + "coffee_brewing", "Se prepara café");
        text(sub + "doorbell", "Suena el timbre");
        text(sub + "dryer_running", "Secadora gira");
        text(sub + "globe_spin", "Globo gira");
        text(sub + "microwave_ding", "Microondas pita");
        text(sub + "sliding_door", "Puerta corredera se desliza");
        text(sub + "toaster_pop", "Tostadora salta");
        text(sub + "toilet_flush", "Cisterna se descarga");
        text(sub + "toolbox_latch", "Cierre de caja de herramientas");
        text(sub + "projector", "Proyector zumba");
        text(sub + "popcorn_popping", "Palomitas estallan");
        text(sub + "pottery_wheel", "Torno de alfarero gira");
        text(sub + "sewing_machine", "Máquina de coser traquetea");
        text(sub + "tv_static", "Televisor chisporrotea");
        text(sub + "washer_running", "Lavadora funciona");
        text(sub + "workbench_saw", "Sierra corta");
    }

    private void painting(String id, String title) {
        text("painting.reikos_furniture." + id + ".title", title);
        text("painting.reikos_furniture." + id + ".author", "Reiko's Furniture");
    }

    @Override
    public String locale() {
        return "es_es";
    }

    @Override
    public String oakWord() {
        return "roble";
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
