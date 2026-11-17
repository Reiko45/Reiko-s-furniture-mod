package com.reiko45.furniture.datagen;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.ColorSet;
import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.core.WoodSet;
import com.reiko45.furniture.core.block.ConnectingTableBlock;
import com.reiko45.furniture.core.block.FurnitureBlock;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.block.TallStorageFurnitureBlock;
import java.util.function.Function;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelBuilder;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * Block states, block models and item models.
 * <p>
 * Every piece has a hand-made template model in {@code models/block/template/<room>/}. The template
 * uses the texture variables {@code #planks}, {@code #log} and {@code #stripped_log} (or {@code #wool}),
 * and this provider creates one child model per wood type or color.
 */
public class FurnitureBlockStateProvider extends BlockStateProvider {
    public FurnitureBlockStateProvider(PackOutput output, ExistingFileHelper files) {
        super(output, ReikosFurniture.MODID, files);
    }

    @Override
    protected void registerStatesAndModels() {
        DataGenerators.ROOMS.forEach(room -> room.models(this));
    }

    // ---- Templates -------------------------------------------------------------------------

    public ResourceLocation template(Room room, String name) {
        return modLoc("block/template/" + room.id() + "/" + name);
    }

    public ModelFile existingTemplate(Room room, String name) {
        return models().getExistingFile(template(room, name));
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    private static <T extends ModelBuilder<T>> T woodTextures(T model, FurnitureWood wood) {
        return model.texture("planks", wood.planksTexture())
                .texture("log", wood.logTexture())
                .texture("stripped_log", wood.strippedLogTexture());
    }

    private BlockModelBuilder woodModel(String name, ResourceLocation template, FurnitureWood wood) {
        return woodTextures(models().withExistingParent(name, template), wood);
    }

    private static ResourceLocation woolTexture(DyeColor color) {
        return ResourceLocation.withDefaultNamespace("block/" + color.getName() + "_wool");
    }

    /** Blockstate y-rotation for a model whose front faces north. */
    public static int yRot(Direction facing) {
        return ((int) facing.toYRot() + 180) % 360;
    }

    // ---- Block state shapes ------------------------------------------------------------------

    /** A block that only rotates with {@link FurnitureBlock#FACING}. */
    public void facingBlock(Block block, Function<BlockState, ModelFile> model) {
        getVariantBuilder(block).forAllStatesExcept(state -> ConfiguredModel.builder()
                .modelFile(model.apply(state))
                .rotationY(yRot(state.getValue(FurnitureBlock.FACING)))
                .build(), FurnitureBlock.WATERLOGGED);
    }

    /** A rotating block with an off model and an on model ({@link SwitchableFurnitureBlock#LIT}). */
    public void facingLitBlock(Block block, ModelFile off, ModelFile on) {
        facingBlock(block, state -> state.getValue(SwitchableFurnitureBlock.LIT) ? on : off);
    }

    /** A two-block-tall block with a model for each half. */
    public void tallBlock(Block block, ModelFile lower, ModelFile upper) {
        facingBlock(block, state -> state.getValue(TallStorageFurnitureBlock.HALF) == DoubleBlockHalf.UPPER ? upper : lower);
    }

    /** A {@link ConnectingTableBlock}: the top is always shown, legs only on open corners. */
    public void tableBlock(Block block, ModelFile top, ModelFile northWestLeg) {
        var builder = getMultipartBuilder(block);
        builder.part().modelFile(top).addModel().end();
        builder.part().modelFile(northWestLeg).rotationY(0).addModel()
                .condition(ConnectingTableBlock.NORTH, false).condition(ConnectingTableBlock.WEST, false).end();
        builder.part().modelFile(northWestLeg).rotationY(90).addModel()
                .condition(ConnectingTableBlock.NORTH, false).condition(ConnectingTableBlock.EAST, false).end();
        builder.part().modelFile(northWestLeg).rotationY(180).addModel()
                .condition(ConnectingTableBlock.SOUTH, false).condition(ConnectingTableBlock.EAST, false).end();
        builder.part().modelFile(northWestLeg).rotationY(270).addModel()
                .condition(ConnectingTableBlock.SOUTH, false).condition(ConnectingTableBlock.WEST, false).end();
    }

    // ---- Wood sets -------------------------------------------------------------------------

    /** Wooden piece with one template: {@code <template>}. */
    public void woodFacing(WoodSet<?> set, String template) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            Block block = set.get(wood).get();
            String name = name(block);
            ModelFile model = woodModel(name, template(set.room(), template), wood);
            facingBlock(block, state -> model);
            itemModels().withExistingParent(name, modLoc("block/" + name));
        }
    }

    /** Wooden piece that switches on and off: {@code <template>} and {@code <template>_on}. */
    public void woodFacingLit(WoodSet<?> set, String template) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            Block block = set.get(wood).get();
            String name = name(block);
            ModelFile off = woodModel(name, template(set.room(), template), wood);
            ModelFile on = woodModel(name + "_on", template(set.room(), template + "_on"), wood);
            facingLitBlock(block, off, on);
            itemModels().withExistingParent(name, modLoc("block/" + name));
        }
    }

    /** Two-block-tall wooden piece: {@code <template>_lower}, {@code <template>_upper} and {@code <template>_item}. */
    public void woodTall(WoodSet<?> set, String template) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            Block block = set.get(wood).get();
            String name = name(block);
            ModelFile lower = woodModel(name + "_lower", template(set.room(), template + "_lower"), wood);
            ModelFile upper = woodModel(name + "_upper", template(set.room(), template + "_upper"), wood);
            tallBlock(block, lower, upper);
            woodTextures(itemModels().withExistingParent(name, template(set.room(), template + "_item")), wood);
        }
    }

    /** Connecting wooden table: {@code <template>_top}, {@code <template>_leg} and {@code <template>_item}. */
    public void woodTable(WoodSet<?> set, String template) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            Block block = set.get(wood).get();
            String name = name(block);
            ModelFile top = woodModel(name + "_top", template(set.room(), template + "_top"), wood);
            ModelFile leg = woodModel(name + "_leg", template(set.room(), template + "_leg"), wood);
            tableBlock(block, top, leg);
            woodTextures(itemModels().withExistingParent(name, template(set.room(), template + "_item")), wood);
        }
    }

    // ---- Color sets ------------------------------------------------------------------------

    /** Creates {@code <color>_<suffix>} from a template, filling in {@code #wool}. */
    public BlockModelBuilder woolModel(String name, ResourceLocation template, DyeColor color) {
        return models().withExistingParent(name, template).texture("wool", woolTexture(color));
    }

    /** Item model for a colored piece, using an item template and filling in {@code #wool}. */
    public void woolItem(ColorSet<?> set, DyeColor color, String template) {
        Block block = set.get(color).get();
        itemModels().withExistingParent(name(block), template(set.room(), template))
                .texture("wool", woolTexture(color));
    }

    // ---- Single blocks ---------------------------------------------------------------------

    /** Item model that uses the given block model. */
    public void blockItem(Block block, ResourceLocation model) {
        itemModels().withExistingParent(name(block), model);
    }
}
