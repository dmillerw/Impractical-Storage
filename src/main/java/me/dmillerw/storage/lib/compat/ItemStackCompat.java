package me.dmillerw.storage.lib.compat;

import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class ItemStackCompat {

    public static ItemStack getEmptyStack() {
        return ItemStack.EMPTY;
    }

    public static boolean isStackEmpty(ItemStack itemStack) {
        return itemStack.isEmpty();
    }

    public static int getStackSize(ItemStack itemStack) {
        return itemStack.getCount();
    }

    public static ItemStack setStackSize(ItemStack itemStack, int amount) {
        itemStack.setCount(amount);
        return itemStack;
    }

    public static ItemStack grow(ItemStack itemStack, int amount) {
        itemStack.grow(amount);
        return itemStack;
    }

    public static ItemStack shrink(ItemStack itemStack, int amount) {
        itemStack.shrink(amount);
        return itemStack;
    }
}
