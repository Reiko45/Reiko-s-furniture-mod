package com.reiko45.furniture.datagen.lang;

import static com.reiko45.furniture.datagen.lang.Grammar.Gender.F;
import static com.reiko45.furniture.datagen.lang.Grammar.Gender.M;

import com.reiko45.furniture.core.Style;
import java.util.EnumMap;
import java.util.Map;

/** Portuguese (Brazil). Names are in title case, like Minecraft's own Brazilian Portuguese names. */
public class Portuguese extends RomanceGrammar {
    private static final String[] WOODS_M = {"de Carvalho", "de Abeto", "de Bétula", "da Selva", "de Acácia",
            "de Carvalho Escuro", "de Mangue", "de Cerejeira", "de Bambu", "Carmesim", "Distorcido"};
    private static final String[] WOODS_F = {"de Carvalho", "de Abeto", "de Bétula", "da Selva", "de Acácia",
            "de Carvalho Escuro", "de Mangue", "de Cerejeira", "de Bambu", "Carmesim", "Distorcida"};
    private static final String[] COLORS_M = {"Branco", "Laranja", "Magenta", "Azul-Claro", "Amarelo", "Verde-Limão", "Rosa",
            "Cinza", "Cinza-Claro", "Ciano", "Roxo", "Azul", "Marrom", "Verde", "Vermelho", "Preto"};
    private static final String[] COLORS_F = {"Branca", "Laranja", "Magenta", "Azul-Clara", "Amarela", "Verde-Limão", "Rosa",
            "Cinza", "Cinza-Clara", "Ciano", "Roxa", "Azul", "Marrom", "Verde", "Vermelha", "Preta"};
    private static final Map<Style, String[]> STYLES = new EnumMap<>(Map.of(
            Style.RUSTIC, new String[] {"Rústico", "Rústica"},
            Style.MODERN, new String[] {"Moderno", "Moderna"},
            Style.ROYAL, new String[] {"Real", "Real"},
            Style.JAPANESE, new String[] {"Japonês", "Japonesa"}));

    public Portuguese() {
        noun("bar_counter", "Balcão de Bar", M);
        noun("baseboard", "Rodapé", M);
        noun("bed", "Cama", F);
        noun("bench", "Banco", M);
        noun("book_stand", "Suporte de Livro", M);
        noun("bookshelf", "Estante de Livros", F);
        noun("bunk_bed", "Beliche", M);
        noun("cat_tree", "Arranhador para Gatos", M);
        noun("changing_table", "Trocador", M);
        noun("coffee_table", "Mesa de Centro", F);
        noun("counter", "Bancada", F);
        noun("crib", "Berço", M);
        noun("crown_molding", "Moldura de Teto", F);
        noun("cutting_board", "Tábua de Corte", F);
        noun("desk", "Escrivaninha", F);
        noun("dining_chair", "Cadeira de Jantar", F);
        noun("dining_table", "Mesa de Jantar", F);
        noun("dresser", "Cômoda", F);
        noun("filing_cabinet", "Arquivo", M);
        noun("floor_lamp", "Luminária de Chão", F);
        noun("french_door", "Porta Francesa", F);
        noun("garden_bench", "Banco de Jardim", M);
        noun("glass_cupboard", "Cristaleira", F);
        noun("glass_upper_cabinet", "Armário Aéreo de Vidro", M);
        noun("globe", "Globo Terrestre", M);
        noun("kids_chair", "Cadeira Infantil", F);
        noun("kids_table", "Mesa Infantil", F);
        noun("kitchen_island", "Ilha de Cozinha", F);
        noun("laundry_basket", "Cesto de Roupa", M);
        noun("library_ladder", "Escada de Biblioteca", F);
        noun("mirror", "Espelho", M);
        noun("nightstand", "Criado-Mudo", M);
        noun("parquet", "Parquete", M);
        noun("parquet_slab", "Laje de Parquete", F);
        noun("patio_chair", "Cadeira de Jardim", F);
        noun("patio_table", "Mesa de Jardim", F);
        noun("pegboard", "Painel Perfurado", M);
        noun("piano", "Piano", M);
        noun("piano_bench", "Banqueta de Piano", F);
        noun("picture_frame", "Porta-Retrato", M);
        noun("planter_box", "Floreira", F);
        noun("pool_table", "Mesa de Sinuca", F);
        noun("record_player", "Toca-Discos", M);
        noun("rocking_horse", "Cavalinho de Balanço", M);
        noun("shutters", "Veneziana", F);
        noun("sideboard", "Aparador", M);
        noun("sink", "Pia", F);
        noun("sliding_door", "Porta de Correr", F);
        noun("table_lamp", "Abajur", M);
        noun("tall_bookcase", "Estante Alta", F);
        noun("toy_chest", "Baú de Brinquedos", M);
        noun("tv_stand", "Rack de TV", M);
        noun("upper_cabinet", "Armário Aéreo", M);
        noun("vanity", "Penteadeira", F);
        noun("wall_clock", "Relógio de Parede", M);
        noun("wall_lamp", "Arandela", F);
        noun("wall_shelf", "Prateleira de Parede", F);
        noun("wardrobe", "Guarda-Roupa", M);
        noun("window", "Janela", F);
        noun("wine_rack", "Adega", F);
        noun("workshop_bench", "Bancada de Trabalho", F);
        noun("writing_desk", "Secretária", F);
        noun("armchair", "Poltrona", F);
        noun("baby_mobile", "Móbile", M);
        noun("bar_stool", "Banqueta de Bar", F);
        noun("blinds", "Persiana", F);
        noun("curtains", "Cortina", F);
        noun("feeding_bowl", "Comedouro", M);
        noun("gaming_chair", "Cadeira Gamer", F);
        noun("office_chair", "Cadeira de Escritório", F);
        noun("oil_drum", "Tambor de Óleo", M);
        noun("pendant_lamp", "Luminária Pendente", F);
        noun("pet_bed", "Caminha de Pet", F);
        noun("rug", "Tapete", M);
        noun("sofa", "Sofá", M);
        noun("toolbox", "Caixa de Ferramentas", F);
        noun("towel_rack", "Toalheiro", M);
        noun("wallpaper", "Papel de Parede", M);

        single("aquarium", "Aquário");
        single("arcade_cabinet", "Fliperama");
        single("bathroom_cabinet", "Armário de Banheiro");
        single("bathroom_sink", "Pia de Banheiro");
        single("bathtub", "Banheira");
        single("bird_cage", "Gaiola");
        single("blender", "Liquidificador");
        single("ceiling_light", "Luminária de Teto");
        single("chalkboard", "Quadro-Negro");
        single("chandelier", "Lustre");
        single("coffee_machine", "Cafeteira");
        single("computer", "Computador");
        single("dartboard", "Alvo de Dardos");
        single("desk_lamp", "Luminária de Mesa");
        single("doorbell", "Campainha");
        single("drum_kit", "Bateria");
        single("dryer", "Secadora");
        single("espresso_machine", "Máquina de Espresso");
        single("fireplace", "Lareira");
        single("fridge", "Geladeira");
        single("furniture_workbench", "Bancada de Marcenaria");
        single("grill", "Churrasqueira");
        single("metal_shelving", "Estante de Metal");
        single("microwave", "Micro-ondas");
        single("plate", "Prato");
        single("reading_lamp", "Luminária de Leitura");
        single("safe", "Cofre");
        single("shower", "Chuveiro");
        single("stove", "Fogão");
        single("toaster", "Torradeira");
        single("toilet", "Vaso Sanitário");
        single("trash_can", "Lixeira");
        single("tv", "Televisão");
        single("washing_machine", "Máquina de Lavar");
        single("white_tiles", "Azulejos Brancos");
        single("black_tiles", "Azulejos Pretos");
        single("checkered_tiles", "Azulejos Xadrez");
        single("blue_tiles", "Azulejos Azuis");
        single("green_tiles", "Azulejos Verdes");
        single("terracotta_tiles", "Azulejos de Terracota");
        single("marble_tiles", "Azulejos de Mármore");
        single("hexagon_tiles", "Azulejos Hexagonais");

        String adv = "advancements.reikos_furniture.";
        text(adv + "root.title", "Reiko's Furniture");
        text(adv + "root.description", "Faça seu primeiro móvel");
        text(adv + "woodworker.title", "Marceneiro");
        text(adv + "woodworker.description", "Faça uma Bancada de Marcenaria. Ela vem com um catálogo de móveis");
        text(adv + "take_a_seat.title", "Sente-se");
        text(adv + "take_a_seat.description", "Sente-se em uma cadeira, um sofá ou uma banqueta");
        text(adv + "coffee_break.title", "Pausa para o Café");
        text(adv + "coffee_break.description", "Tome uma bebida feita com um eletrodoméstico da cozinha ou do bar");
        text(adv + "home_sweet_home.title", "Lar, Doce Lar");
        text(adv + "home_sweet_home.description", "Tenha um móvel de cada cômodo");
        text(adv + "interior_designer.title", "Designer de Interiores");
        text(adv + "interior_designer.description", "Gire um móvel com um martelo");
        text(adv + "heavy_lifting.title", "Peso Pesado");
        text(adv + "heavy_lifting.description", "Mova um móvel de armazenamento com um carrinho de mudança, com tudo dentro");
        text(adv + "a_matter_of_style.title", "Questão de Estilo");
        text(adv + "a_matter_of_style.description", "Faça um móvel de um estilo");
        text(adv + "style_icon.title", "Ícone de Estilo");
        text(adv + "style_icon.description", "Tenha um móvel de cada estilo: Rústico, Moderno, Real e Japonês");
        text(adv + "tickling_the_ivories.title", "Ao Piano");
        text(adv + "tickling_the_ivories.description", "Toque uma nota em um piano");
        text(adv + "bullseye.title", "Na Mosca!");
        text(adv + "bullseye.description", "Faça 50 pontos com um dardo");
        text(adv + "high_score.title", "Recorde");
        text(adv + "high_score.description", "Faça 9000 pontos ou mais em um fliperama");
        text(adv + "fresh_laundry.title", "Roupa Limpa");
        text(adv + "fresh_laundry.description", "Tire a tinta de algo em uma máquina de lavar");
        text(adv + "good_boy.title", "Quem é um Bom Garoto?");
        text(adv + "good_boy.description", "Faça seu pet comer de um comedouro");
        text(adv + "valued_customer.title", "Cliente Especial");
        text(adv + "valued_customer.description", "Compre móveis de um Moveleiro");

        text("catalog.reikos_furniture.extras", "Ferramentas e Comida");
        text("catalog.reikos_furniture.hint", "Escolha uma seção à esquerda e clique em um móvel para ver como é feito. "
                + "Role sobre a receita para ver suas outras madeiras e cores.");
        text("catalog.reikos_furniture.made_in", "Feito em: %s");
        text("catalog.reikos_furniture.no_recipe", "Não pode ser fabricado");
        text("catalog.reikos_furniture.pieces", "%s móveis");
        text("catalog.reikos_furniture.variants", "Variante %s de %s");
        text("catalog.reikos_furniture.workbench", "Bancada de Marcenaria: %s tábuas");

        String config = "config.reikos_furniture.";
        text(config + "appliance_speed", "Velocidade dos Eletrodomésticos");
        text(config + "dolly_carries_full_storage", "Carrinho Leva Móveis Cheios");
        text(config + "gameplay", "Jogabilidade");
        text(config + "laundry_cycle_seconds", "Ciclo de Lavagem (Segundos)");
        text(config + "modded_woods", "Madeiras de Outros Mods");
        text(config + "modded_woods.enabled", "Móveis em Madeiras de Outros Mods");
        text(config + "modded_woods.styles", "Estilos em Madeiras de Outros Mods");
        text(config + "safe_owner_only", "Cofres Só Abrem para o Dono");
        text(config + "village_workshop_weight", "Frequência de Oficinas nas Vilas");
        text(config + "world", "Mundo");
        text("reikos_furniture.configuration.title", "Configurações do Reiko's Furniture");

        text("container.reikos_furniture.furniture_workbench", "Bancada de Marcenaria");
        text("container.reikos_furniture.furniture_workbench.cost", "Precisa de %s tábuas");
        text("container.reikos_furniture.microwave", "Micro-ondas");
        text("container.reikos_furniture.stove", "Fogão");
        text("emi.category.reikos_furniture.appliance", "Eletrodomésticos de Cozinha");
        text("emi.category.reikos_furniture.carpentry", "Bancada de Marcenaria");
        text("recipe_category.reikos_furniture.appliance", "Eletrodomésticos de Cozinha");
        text("recipe_category.reikos_furniture.carpentry", "Bancada de Marcenaria");
        text("recipe.reikos_furniture.appliance_time", "Leva %s s");
        text("entity.minecraft.villager.furniture_maker", "Moveleiro");
        text("entity.minecraft.villager.reikos_furniture.furniture_maker", "Moveleiro");

        String item = "item.reikos_furniture.";
        text(item + "coffee", "Café");
        text(item + "hot_cocoa", "Chocolate Quente");
        text(item + "fruit_smoothie", "Vitamina de Frutas");
        text(item + "toast", "Torrada");
        text(item + "espresso", "Café Espresso");
        text(item + "latte", "Café com Leite");
        text(item + "furniture_catalog", "Catálogo de Móveis");
        text(item + "furniture_catalog.tooltip", "Todos os móveis, por cômodo e estilo");
        text(item + "hammer", "Martelo de Marceneiro");
        text(item + "hammer.tooltip", "Clique com o botão direito em um móvel para girá-lo (agachado: para o outro lado)");
        text(item + "moving_dolly", "Carrinho de Mudança");
        text(item + "moving_dolly.tooltip", "Clique com o botão direito em um móvel de armazenamento para movê-lo com o conteúdo");
        text(item + "moving_dolly.carrying", "Levando: %s");
        text("itemGroup.reikos_furniture", "Reiko's Furniture");
        text("itemGroup.reikos_furniture.styles", "Reiko's Furniture: Estilos");

        String message = "message.reikos_furniture.";
        text(message + "arcade_score", "Novo recorde: %s!");
        text(message + "bird_cage_empty", "Traga um papagaio no ombro para colocá-lo dentro");
        text(message + "clock_time", "São %s");
        text(message + "dart_score", "Você fez %s");
        text(message + "dolly_no_room", "Não há espaço para ele aqui");
        text(message + "dolly_not_empty", "Esvazie primeiro (o servidor não permite mover móveis cheios)");
        text(message + "dolly_not_storage", "O carrinho só leva móveis de armazenamento");
        text(message + "dolly_not_yours", "Este cofre não é seu");
        text(message + "hammer_cannot_turn", "Este móvel não pode ser girado com o martelo");
        text(message + "ladder_cannot_slide", "Não há onde apoiar ali");
        text(message + "laundry_busy", "Espere o ciclo terminar");
        text(message + "safe_locked", "Este cofre pertence a %s");

        painting("big_wave", "A Grande Onda");
        painting("cat", "Gato");
        painting("city_lights", "Luzes da Cidade");
        painting("fruit_bowl", "Fruteira");
        painting("lighthouse", "Farol");
        painting("meadow", "Prado");
        painting("mountains", "Montanhas");
        painting("night_hills", "Colinas à Noite");
        painting("sailboat", "Veleiro");
        painting("shapes", "Formas");
        painting("still_life", "Natureza-Morta");
        painting("sunset", "Pôr do Sol");

        String room = "room.reikos_furniture.";
        text(room + "bedroom", "Quarto");
        text(room + "kitchen", "Cozinha");
        text(room + "dining", "Sala de Jantar");
        text(room + "livingroom", "Sala de Estar");
        text(room + "bathroom", "Banheiro");
        text(room + "office", "Escritório");
        text(room + "outdoor", "Área Externa");
        text(room + "kids", "Quarto Infantil");
        text(room + "laundry", "Lavanderia");
        text(room + "music", "Sala de Música");
        text(room + "gameroom", "Sala de Jogos");
        text(room + "pets", "Cantinho do Pet");
        text(room + "decor", "Decoração");
        text(room + "garage", "Garagem");
        text(room + "library", "Biblioteca");
        text(room + "bar", "Bar em Casa");
        text(room + "nursery", "Quarto do Bebê e Hobbies");
        text("style.reikos_furniture.rustic", "Estilo Rústico");
        text("style.reikos_furniture.modern", "Estilo Moderno");
        text("style.reikos_furniture.royal", "Estilo Real");
        text("style.reikos_furniture.japanese", "Estilo Japonês");

        String sub = "subtitles.reikos_furniture.";
        text(sub + "arcade_blip", "Fliperama apita");
        text(sub + "blender", "Liquidificador zune");
        text(sub + "coffee_brewing", "Café é passado");
        text(sub + "doorbell", "Campainha toca");
        text(sub + "dryer_running", "Secadora gira");
        text(sub + "globe_spin", "Globo gira");
        text(sub + "microwave_ding", "Micro-ondas apita");
        text(sub + "sliding_door", "Porta de correr desliza");
        text(sub + "toaster_pop", "Torradeira estala");
        text(sub + "toilet_flush", "Descarga é acionada");
        text(sub + "toolbox_latch", "Trava da caixa de ferramentas");
        text(sub + "tv_static", "TV chia");
        text(sub + "washer_running", "Máquina de lavar funciona");
        text(sub + "workbench_saw", "Serra corta");
    }

    private void painting(String id, String title) {
        text("painting.reikos_furniture." + id + ".title", title);
        text("painting.reikos_furniture." + id + ".author", "Reiko's Furniture");
    }

    @Override
    public String locale() {
        return "pt_br";
    }

    @Override
    public String oakWord() {
        return "Carvalho";
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
