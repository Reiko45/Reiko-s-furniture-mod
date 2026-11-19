package com.reiko45.furniture.core.mail;

import com.reiko45.furniture.core.FurnitureWood;
import com.reiko45.furniture.core.Room;
import com.reiko45.furniture.datagen.FurnitureBlockStateProvider;
import com.reiko45.furniture.datagen.FurnitureRecipeProvider;
import com.reiko45.furniture.datagen.RoomData;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ModelFile;

/** Models, recipe and texts for the mailbox. P = planks, C = chest, L = log. */
public class MailData implements RoomData {
    @Override
    public void models(FurnitureBlockStateProvider models) {
        for (FurnitureWood wood : FurnitureWood.values()) {
            Block block = Mail.MAILBOX.get(wood).get();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            ModelFile down = models.woodModel(name, models.template(Room.OUTDOOR, "mailbox"), wood);
            ModelFile up = models.woodModel(name + "_flag", models.template(Room.OUTDOOR, "mailbox_flag"), wood);
            models.facingBlock(block, state -> state.getValue(MailboxBlock.FLAG) ? up : down);
            models.itemModels().withExistingParent(name, models.modLoc("block/" + name));
        }
    }

    @Override
    public void recipes(FurnitureRecipeProvider recipes, RecipeOutput output) {
        recipes.woodShaped(output, Mail.MAILBOX, 1, wood -> Map.of('C', Ingredient.of(Items.CHEST)), "PPP", "PCP", " L ");
    }

    @Override
    public void translations(BiConsumer<String, String> add) {
        add.accept("container.reikos_furniture.mailbox", "%s's Mailbox");
        add.accept("container.reikos_furniture.mailbox.not_yours", "Only %s can read the mail in this mailbox. You can send mail from here.");
        add.accept("container.reikos_furniture.mailbox.send_to", "Send to:");
        add.accept("container.reikos_furniture.mailbox.send", "Send");
        add.accept("message.reikos_furniture.mail_sent", "Sent to %s");
        add.accept("message.reikos_furniture.mail_partly_sent", "%s's mailbox is full: what didn't fit is still here");
        add.accept("message.reikos_furniture.mail_no_mailbox", "%s has no mailbox");
        add.accept("message.reikos_furniture.mail_unknown_player", "There is no player called %s");
        add.accept("message.reikos_furniture.mail_nothing", "Put what you want to send in the slots under the name");
        add.accept("message.reikos_furniture.mail_received", "You've got mail from %s");
    }
}
