package me.dmillerw.storage.block.tile.inv;

import me.dmillerw.storage.block.tile.TileController;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * @author dmillerw
 */
public class ControllerItemHandler implements IItemHandler {

    private static final int MAX_BLOCK_STACK_SIZE = 1;
    private static final int MAX_ITEM_STACK_SIZE = 16;

    public static int getMaxStackSize(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock ? MAX_BLOCK_STACK_SIZE : MAX_ITEM_STACK_SIZE;
    }

    private TileController controller;
    public ControllerItemHandler(TileController controller) {
        this.controller = controller;
    }

    @Override
    public int getSlots() {
        return controller.totalSize;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return controller.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (TileController.INVENTORY_BLOCK)
            return ItemStack.EMPTY;

        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack stackInSlot = controller.getStackInSlot(slot);

        int m;
        if (!stackInSlot.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                return stack;

            m = Math.min(stack.getMaxStackSize(), getMaxStackSize(stackInSlot)) - stackInSlot.getCount();

            if (stack.getCount() <= m) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(stackInSlot.getCount());
                    controller.setInventorySlotContents(slot, copy, false, true, true);
                    controller.markDirty();
                }

                return ItemStack.EMPTY;
            } else {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.splitStack(m);
                    copy.grow(stackInSlot.getCount());
                    controller.setInventorySlotContents(slot, copy, false, true, true);
                    controller.markDirty();
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            }
        } else {
            m = Math.min(stack.getMaxStackSize(), getMaxStackSize(stack));
            if (m < stack.getCount()) {
                // copy the stack to not modify the original one
                stack = stack.copy();
                if (!simulate) {
                    controller.setInventorySlotContents(slot, stack.splitStack(m), false, true, true);
                    controller.markDirty();
                    return stack;
                } else {
                    stack.shrink(m);
                    return stack;
                }
            } else {
                if (!simulate) {
                    controller.setInventorySlotContents(slot, stack, false, true, true);
                    controller.markDirty();
                }
                return ItemStack.EMPTY;
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (TileController.INVENTORY_BLOCK)
            return ItemStack.EMPTY;

        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack stackInSlot = controller.getStackInSlot(slot);

        if (stackInSlot.isEmpty())
            return ItemStack.EMPTY;

        if (simulate) {
            if (stackInSlot.getCount() < amount) {
                return stackInSlot.copy();
            } else {
                ItemStack copy = stackInSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int m = Math.min(stackInSlot.getCount(), amount);
            ItemStack old = controller.getStackInSlot(slot);
            ItemStack decr = old.splitStack(m);

            controller.setInventorySlotContents(slot, old, true, true, true);
            controller.markDirty();

            return decr;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}
