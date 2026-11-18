package com.reiko45.furniture.datagen.lang;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureRegistry;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Style;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.datagen.FurnitureLanguageProvider;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.jetbrains.annotations.Nullable;

/**
 * Writes one language file from a {@link Grammar}. Furniture names are put together from the
 * language's words; everything else comes from its list of texts. Data generation fails if any English
 * key has no translation, so the languages can't fall behind.
 */
public class TranslatedLanguageProvider extends LanguageProvider {
    private final PackOutput output;
    private final Grammar grammar;

    public TranslatedLanguageProvider(PackOutput output, Grammar grammar) {
        super(output, ReikosFurniture.MODID, grammar.locale());
        this.output = output;
        this.grammar = grammar;
    }

    @Override
    protected void addTranslations() {
        Map<String, String> english = FurnitureLanguageProvider.english(output);
        Map<String, String> out = new LinkedHashMap<>();
        out.put(FurnitureLanguageProvider.OAK_WORD_KEY, grammar.oakWord());

        for (WoodSet<?> set : FurnitureRegistry.woodSets()) {
            Grammar.Noun noun = grammar.noun(basePiece(set.piece(), set.style()));
            for (FurnitureWood wood : FurnitureWood.values()) {
                out.put(set.get(wood).get().getDescriptionId(), grammar.name(noun, wood, null, set.style()));
            }
        }
        for (ColorSet<?> set : FurnitureRegistry.colorSets()) {
            Grammar.Noun noun = grammar.noun(basePiece(set.piece(), set.style()));
            for (DyeColor color : DyeColor.values()) {
                out.put(set.get(color).get().getDescriptionId(), grammar.name(noun, null, color, set.style()));
            }
        }
        for (DeferredBlock<?> holder : FurnitureRegistry.rooms().keySet()) {
            Block block = holder.get();
            if (!out.containsKey(block.getDescriptionId())) {
                String id = BuiltInRegistries.BLOCK.getKey(block).getPath();
                String name = grammar.single(id);
                if (name == null) {
                    throw new IllegalStateException(grammar.locale() + " has no name for " + id);
                }
                out.put(block.getDescriptionId(), name);
            }
        }

        List<String> missing = new ArrayList<>();
        for (String key : english.keySet()) {
            if (!out.containsKey(key)) {
                String text = grammar.text(key);
                if (text == null) {
                    missing.add(key);
                } else {
                    out.put(key, text);
                }
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException(grammar.locale() + " is missing " + missing.size() + " texts: " + missing);
        }
        List<String> extra = out.keySet().stream().filter(key -> !english.containsKey(key)).toList();
        if (!extra.isEmpty()) {
            throw new IllegalStateException(grammar.locale() + " has keys that English doesn't: " + extra);
        }
        out.forEach(this::add);
    }

    /** {@code rustic_bed} is a bed in the rustic style; the hidden top bunk is named like its bunk bed. */
    private static String basePiece(String piece, @Nullable Style style) {
        String base = style == null ? piece : piece.substring(style.id().length() + 1);
        return base.equals("bunk_bed_top") ? "bunk_bed" : base;
    }
}
