package io.github.skippyall.minions.minion;

import io.github.skippyall.minions.Minions;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class ModuleInventory implements ImplementedInventory {
    private static final TagKey<Item> MODULES = TagKey.of(RegistryKeys.ITEM, new Identifier(Minions.MOD_ID,"modules"));
    private DefaultedList<ItemStack> stacks = DefaultedList.ofSize(27, ItemStack.EMPTY);

    public ModuleInventory() {
    }

    @Override
    public int getMaxCountPerStack() {
        return 1;
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return (stack.getCount() <= getMaxCountPerStack()) && stack.isIn(MODULES);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

    public void readNbt(NbtCompound nbt) {
        Inventories.readNbt(nbt, stacks);
    }

    public void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, stacks);
    }
}
