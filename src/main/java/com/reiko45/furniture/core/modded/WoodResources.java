package com.reiko45.furniture.core.modded;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.FurnitureWood;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModFileInfo;

/**
 * Makes the resources for modded woods by copying the oak ones shipped in the jar: every file named
 * {@code oak_*} (blockstates, models, loot tables, recipes, workbench recipes, recipe advancements),
 * with the oak ids, ingredients and textures swapped for the modded wood's. Tags that list oak pieces
 * and the English names get matching entries too.
 */
public final class WoodResources {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Pattern OWN_OAK_ID = Pattern.compile("reikos_furniture:((?:block/|item/|blocks/|carpentry/)?)oak_");
    private static final String OAK_PREFIX = "oak_";

    private WoodResources() {
    }

    /** What to put in place of the oak names for one wood. Textures are only needed for client resources. */
    public record Swap(String woodId, String displayName, String planks, String log, String slab,
                       String planksTexture, String logTexture, String strippedLogTexture) {
        public static Swap of(FurnitureWood wood, boolean withTextures) {
            String planksTexture = "";
            String logTexture = "";
            String strippedLogTexture = "";
            if (withTextures) {
                // Missing log textures fall back to the planks, missing stripped ones to the log.
                planksTexture = texture(wood.planks(), null);
                logTexture = texture(wood.log(), planksTexture);
                strippedLogTexture = texture(wood.strippedLog(), logTexture);
            }
            return new Swap(wood.id(), wood.displayName(), key(wood.planks()), key(wood.log()), key(wood.slab()),
                    planksTexture, logTexture, strippedLogTexture);
        }
    }

    /** Swaps oak for the modded wood in one oak file. */
    public static String translate(String json, Swap swap) {
        String out = json
                .replace("\"minecraft:block/oak_planks\"", quote(swap.planksTexture()))
                .replace("\"minecraft:block/oak_log\"", quote(swap.logTexture()))
                .replace("\"minecraft:block/stripped_oak_log\"", quote(swap.strippedLogTexture()))
                .replace("\"minecraft:oak_planks\"", quote(swap.planks()))
                .replace("\"minecraft:oak_log\"", quote(swap.log()))
                .replace("\"minecraft:oak_slab\"", quote(swap.slab()));
        return OWN_OAK_ID.matcher(out).replaceAll(Matcher.quoteReplacement("reikos_furniture:") + "$1"
                + Matcher.quoteReplacement(swap.woodId() + "_"));
    }

    /** The name of an oak file for another wood: {@code oak_nightstand.json} becomes {@code <wood>_nightstand.json}. */
    public static String rename(String fileName, Swap swap) {
        return swap.woodId() + "_" + fileName.substring(OAK_PREFIX.length());
    }

    /** "Oak Nightstand" becomes "Fir Nightstand". */
    public static String rename(String oakName, String displayName) {
        return oakName.startsWith("Oak ") ? displayName + oakName.substring(3) : displayName + " " + oakName;
    }

    /** Builds every file of the pack for one side (client resources or server data). */
    public static Map<ResourceLocation, byte[]> build(PackType type, List<FurnitureWood> woods) {
        Map<ResourceLocation, byte[]> files = new LinkedHashMap<>();
        if (woods.isEmpty()) {
            return files;
        }
        boolean client = type == PackType.CLIENT_RESOURCES;
        List<Swap> swaps = woods.stream().map(wood -> Swap.of(wood, client)).toList();
        Path root = ModList.get().getModFileById(ReikosFurniture.MODID).getFile().findResource(type.getDirectory());
        try (Stream<Path> paths = Files.walk(root)) {
            for (Path file : paths.filter(path -> path.toString().endsWith(".json")).toList()) {
                String relative = root.relativize(file).toString().replace('\\', '/');
                int slash = relative.indexOf('/');
                if (slash < 0) {
                    continue;
                }
                String namespace = relative.substring(0, slash);
                String path = relative.substring(slash + 1);
                String fileName = path.substring(path.lastIndexOf('/') + 1);
                if (fileName.startsWith(OAK_PREFIX) && namespace.equals(ReikosFurniture.MODID) && !path.startsWith("textures/")) {
                    String json = Files.readString(file, StandardCharsets.UTF_8);
                    String folder = path.substring(0, path.length() - fileName.length());
                    for (Swap swap : swaps) {
                        files.put(ResourceLocation.fromNamespaceAndPath(namespace, folder + rename(fileName, swap)),
                                translate(json, swap).getBytes(StandardCharsets.UTF_8));
                    }
                } else if (path.startsWith("tags/")) {
                    byte[] tag = tag(Files.readString(file, StandardCharsets.UTF_8), swaps);
                    if (tag != null) {
                        files.put(ResourceLocation.fromNamespaceAndPath(namespace, path), tag);
                    }
                } else if (client && namespace.equals(ReikosFurniture.MODID) && path.equals("lang/en_us.json")) {
                    files.put(ResourceLocation.fromNamespaceAndPath(namespace, path), lang(Files.readString(file, StandardCharsets.UTF_8), swaps));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read Reiko's Furniture resources for modded woods", e);
        }
        return files;
    }

    /** A tag file adding the modded version of every oak piece the tag lists, or null if it lists none. */
    static byte[] tag(String json, List<Swap> swaps) {
        JsonObject tag = JsonParser.parseString(json).getAsJsonObject();
        JsonArray added = new JsonArray();
        String oak = "reikos_furniture:" + OAK_PREFIX;
        for (JsonElement value : tag.getAsJsonArray("values")) {
            if (value.isJsonPrimitive() && value.getAsString().startsWith(oak)) {
                for (Swap swap : swaps) {
                    added.add("reikos_furniture:" + swap.woodId() + "_" + value.getAsString().substring(oak.length()));
                }
            }
        }
        if (added.isEmpty()) {
            return null;
        }
        JsonObject out = new JsonObject();
        out.addProperty("replace", false);
        out.add("values", added);
        return GSON.toJson(out).getBytes(StandardCharsets.UTF_8);
    }

    /** English names for the modded pieces, from the oak ones. */
    static byte[] lang(String json, List<Swap> swaps) {
        JsonObject names = JsonParser.parseString(json).getAsJsonObject();
        JsonObject out = new JsonObject();
        String oak = "block.reikos_furniture." + OAK_PREFIX;
        for (Map.Entry<String, JsonElement> entry : names.entrySet()) {
            if (entry.getKey().startsWith(oak)) {
                for (Swap swap : swaps) {
                    out.addProperty("block.reikos_furniture." + swap.woodId() + "_" + entry.getKey().substring(oak.length()),
                            rename(entry.getValue().getAsString(), swap.displayName()));
                }
            }
        }
        return GSON.toJson(out).getBytes(StandardCharsets.UTF_8);
    }

    private static String quote(String value) {
        return "\"" + value + "\"";
    }

    private static String key(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    /**
     * The texture for a block: {@code <mod>:block/<name>} if the mod ships that image, else the fallback
     * (the planks texture), else that path anyway.
     */
    static String texture(Block block, String fallback) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        for (IModFileInfo mod : ModList.get().getModFiles()) {
            if (Files.exists(mod.getFile().findResource("assets", key.getNamespace(), "textures", "block", key.getPath() + ".png"))) {
                return key.getNamespace() + ":block/" + key.getPath();
            }
        }
        ReikosFurniture.LOGGER.debug("No texture for {} in any mod, modded wood furniture uses {}", key, fallback);
        return fallback != null ? fallback : key.getNamespace() + ":block/" + key.getPath();
    }
}
