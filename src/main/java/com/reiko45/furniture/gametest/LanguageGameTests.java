package com.reiko45.furniture.gametest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.registry.ModBlocks;
import com.reiko45.furniture.core.registry.ModItems;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class LanguageGameTests {
    static final List<String> LANGUAGES = List.of("es_es", "fr_fr", "de_de", "pt_br");

    /** A language file from the mod's own resources. */
    static JsonObject language(String code) {
        Path file = ModList.get().getModFileById(ReikosFurniture.MODID).getFile()
                .findResource("assets", ReikosFurniture.MODID, "lang", code + ".json");
        try {
            return JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (IOException e) {
            throw new GameTestAssertException("can't read " + code + ": " + e.getMessage());
        }
    }

    private static int placeholders(String text) {
        return text.split("%s", -1).length - 1;
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void everyLanguageHasEveryText(GameTestHelper helper) {
        JsonObject english = language("en_us");
        for (String code : LANGUAGES) {
            JsonObject other = language(code);
            for (String key : english.keySet()) {
                helper.assertTrue(other.has(key), code + " is missing " + key);
                helper.assertValueEqual(placeholders(other.get(key).getAsString()), placeholders(english.get(key).getAsString()),
                        code + " " + key + " placeholders");
            }
            helper.assertValueEqual(other.size(), english.size(), code + " has no extra keys");
        }
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void everyBlockAndItemHasAName(GameTestHelper helper) {
        JsonObject english = language("en_us");
        ModItems.ITEMS.getEntries().forEach(item -> helper.assertTrue(english.has(item.value().getDescriptionId()),
                item.getId() + " has an English name"));
        // The mod's own blocks; pieces in other mods' woods get their names from the oak ones at runtime.
        ModBlocks.BLOCKS.getEntries().forEach(block -> helper.assertTrue(english.has(block.value().getDescriptionId()),
                block.getId() + " has an English name"));
        helper.succeed();
    }
}
