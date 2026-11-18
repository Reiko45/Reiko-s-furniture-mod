package com.reiko45.furniture.core.workbench;

import com.reiko45.furniture.core.registry.ModMenus;
import com.reiko45.furniture.core.registry.ModSounds;
import com.reiko45.furniture.core.registry.ModRecipes;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

/**
 * Furniture workbench screen logic, modelled on the vanilla stonecutter: put planks in the input slot,
 * pick a piece of furniture, and take it from the result slot. Each piece costs a number of planks.
 */
public class WorkbenchMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT = 0;
    public static final int RESULT_SLOT = 1;
    private static final int INV_START = 2;
    private static final int INV_END = 29;
    private static final int HOTBAR_END = 38;

    private final ContainerLevelAccess access;
    private final Level level;
    private final DataSlot selectedRecipe = DataSlot.standalone();
    private List<RecipeHolder<CarpentryRecipe>> recipes = new ArrayList<>();
    private ItemStack lastInput = ItemStack.EMPTY;
    private long lastSoundTime;
    private Runnable updateListener = () -> {
    };

    private final Container input = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            slotsChanged(this);
            updateListener.run();
        }
    };
    private final ResultContainer result = new ResultContainer();
    private final Slot inputSlot;
    private final Slot resultSlot;

    /** Client-side constructor, used by the menu type. */
    public WorkbenchMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public WorkbenchMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenus.WORKBENCH.get(), containerId);
        this.access = access;
        this.level = inventory.player.level();
        this.inputSlot = addSlot(new Slot(input, 0, 20, 33));
        this.resultSlot = addSlot(new Slot(result, 1, 143, 33) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                stack.onCraftedBy(player.level(), player, stack.getCount());
                RecipeHolder<CarpentryRecipe> recipe = selected();
                if (recipe != null) {
                    inputSlot.remove(recipe.value().count());
                }
                setupResultSlot();
                access.execute((lvl, pos) -> {
                    long time = lvl.getGameTime();
                    if (lastSoundTime != time) {
                        lvl.playSound(null, pos, ModSounds.WORKBENCH_SAW.get(), SoundSource.BLOCKS, 0.7F, 1.0F);
                        lastSoundTime = time;
                    }
                });
                super.onTake(player, stack);
            }
        });
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
        addDataSlot(selectedRecipe);
    }

    public List<RecipeHolder<CarpentryRecipe>> getRecipes() {
        return recipes;
    }

    public int getSelectedRecipeIndex() {
        return selectedRecipe.get();
    }

    public boolean hasInputItem() {
        return inputSlot.hasItem() && !recipes.isEmpty();
    }

    /** Whether there are enough planks in the input slot for this recipe. */
    public boolean canAfford(RecipeHolder<CarpentryRecipe> recipe) {
        return recipe.value().canAfford(inputSlot.getItem());
    }

    public void registerUpdateListener(Runnable listener) {
        this.updateListener = listener;
    }

    private RecipeHolder<CarpentryRecipe> selected() {
        int index = selectedRecipe.get();
        return index >= 0 && index < recipes.size() ? recipes.get(index) : null;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < recipes.size()) {
            selectedRecipe.set(id);
            setupResultSlot();
        }
        return true;
    }

    @Override
    public void slotsChanged(Container container) {
        ItemStack stack = inputSlot.getItem();
        if (!stack.is(lastInput.getItem())) {
            lastInput = stack.copy();
            recipes = new ArrayList<>();
            selectedRecipe.set(-1);
            if (!stack.isEmpty()) {
                recipes = new ArrayList<>(level.getRecipeManager()
                        .getRecipesFor(ModRecipes.CARPENTRY.get(), new SingleRecipeInput(stack), level));
            }
        }
        // The plank count may have changed, which decides whether the result is available.
        setupResultSlot();
    }

    private void setupResultSlot() {
        RecipeHolder<CarpentryRecipe> recipe = selected();
        if (recipe != null && recipe.value().canAfford(inputSlot.getItem())) {
            result.setRecipeUsed(recipe);
            resultSlot.set(recipe.value().assemble(new SingleRecipeInput(inputSlot.getItem()), level.registryAccess()));
        } else {
            resultSlot.set(ItemStack.EMPTY);
        }
        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, Workbench.BLOCK.get());
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != result && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == RESULT_SLOT) {
            stack.getItem().onCraftedBy(stack, player.level(), player);
            if (!moveItemStackTo(stack, INV_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, original);
        } else if (index == INPUT_SLOT) {
            if (!moveItemStackTo(stack, INV_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (level.getRecipeManager().getRecipeFor(ModRecipes.CARPENTRY.get(), new SingleRecipeInput(stack), level).isPresent()) {
            if (!moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < INV_END) {
            if (!moveItemStackTo(stack, INV_END, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, INV_START, INV_END, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        }
        slot.setChanged();
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        broadcastChanges();
        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        result.removeItemNoUpdate(1);
        access.execute((lvl, pos) -> clearContainer(player, input));
    }
}
