package me.dmillerw.storage.block.property;

import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class UnlistedItemStack extends UnlistedProperty<ItemStack> {

    public UnlistedItemStack(String name) {
        super(name, ItemStack.class);
    }

    @Override
    public boolean isValid(ItemStack value) {
        return true;
    }

    @Override
    public String valueToString(ItemStack value) {
        return value.toString();
    }
}