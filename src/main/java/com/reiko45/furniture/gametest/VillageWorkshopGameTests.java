package com.reiko45.furniture.gametest;

import com.reiko45.furniture.ReikosFurniture;
import com.reiko45.furniture.core.block.SwitchableFurnitureBlock;
import com.reiko45.furniture.core.workbench.Workbench;
import com.reiko45.furniture.villager.VillageWorkshops;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ReikosFurniture.MODID)
@PrefixGameTestTemplate(false)
public class VillageWorkshopGameTests {
    /** The wood each village's workshop furniture is made of. */
    private static final Map<String, String> WOODS = Map.of(
            "plains", "oak", "desert", "jungle", "savanna", "acacia", "snowy", "spruce", "taiga", "spruce");

    @GameTest(template = CoreGameTests.EMPTY)
    public static void workshopsLoadWithAWorkbench(GameTestHelper helper) {
        StructurePlaceSettings settings = new StructurePlaceSettings();
        for (String village : VillageWorkshops.VILLAGES) {
            ResourceLocation id = VillageWorkshops.template(village);
            StructureTemplate template = helper.getLevel().getStructureManager().get(id)
                    .orElseThrow(() -> new GameTestAssertException(id + " did not load"));
            helper.assertValueEqual(template.getSize().getX(), 7, id + " width");
            helper.assertValueEqual(template.filterBlocks(BlockPos.ZERO, settings, Workbench.BLOCK.get()).size(), 1,
                    id + " furniture workbench");
            helper.assertValueEqual(template.filterBlocks(BlockPos.ZERO, settings, Blocks.JIGSAW).size(), 2,
                    id + " jigsaws (entrance and villager)");
            List<StructureTemplate.StructureBlockInfo> lamp = template.filterBlocks(BlockPos.ZERO, settings,
                    BuiltInRegistries.BLOCK.get(ReikosFurniture.id(WOODS.get(village) + "_floor_lamp")));
            helper.assertValueEqual(lamp.size(), 2, id + " floor lamp halves");
            helper.assertTrue(lamp.stream().allMatch(info -> info.state().getValue(SwitchableFurnitureBlock.LIT)), id + " lamp is on");
            for (StructureTemplate.StructureBlockInfo info : template.filterBlocks(BlockPos.ZERO, settings, Blocks.AIR)) {
                helper.assertTrue(info.pos().getY() > 0, id + " has no holes in its floor");
            }
        }
        helper.succeed();
    }

    @GameTest(template = CoreGameTests.EMPTY)
    public static void villageHousePoolsHaveTheWorkshop(GameTestHelper helper) {
        var pools = helper.getLevel().registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);
        for (String village : VillageWorkshops.VILLAGES) {
            StructureTemplatePool pool = pools.get(VillageWorkshops.pool(village));
            helper.assertTrue(pool != null, village + " house pool exists");
            String id = VillageWorkshops.template(village).toString();
            long copies = pool.templates.stream().filter(element -> element.toString().contains(id)).count();
            helper.assertValueEqual((int) copies, 2, village + ": the workshop is in the pool with the default weight");
            helper.assertTrue(pool.rawTemplates.stream().anyMatch(pair -> pair.getFirst().toString().contains(id)),
                    village + ": the workshop is in the pool's list");
        }
        helper.succeed();
    }
}
