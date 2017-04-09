package me.dmillerw.storage.block.tile.inv;

import me.dmillerw.storage.block.tile.TileCrate;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

/**
 * @author dmillerw
 */
public class CrateItemHandler implements IItemHandler {

    private TileCrate crate;
    public CrateItemHandler(TileCrate crate) {
        this.crate = crate;
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return crate.getContents();
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        ItemStack inSlot = getStackInSlot(slot);

        if (inSlot.isEmpty()) {
            int min = Math.min(stack.getMaxStackSize(), crate.getCrateType().getItemStorage());
            if (stack.getCount() <= min) {
                if (!simulate)
                    crate.setContents(stack.copy());

                return ItemStack.EMPTY;
            } else {
                stack = stack.copy();
                if (!simulate) {
                    crate.setContents(stack.splitStack(min));
                } else {
                    stack.shrink(min);
                }

                return stack;
            }
        } else {
            if (!ItemHandlerHelper.canItemStacksStack(stack, inSlot))
                return stack;

            int min = Math.min(stack.getMaxStackSize(), crate.getCrateType().getItemStorage() - inSlot.getCount());

            if (stack.getCount() <= min) {
                if (!simulate) {
                    ItemStack copy = stack.copy();
                    copy.grow(inSlot.getCount());

                    crate.setContents(copy);
                }

                return ItemStack.EMPTY;
            } else {
                stack = stack.copy();
                if (!simulate) {
                    ItemStack copy = stack.splitStack(min);
                    copy.grow(inSlot.getCount());

                    crate.setContents(copy);
                } else {
                    stack.shrink(min);
                }

                return stack;
            }
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        ItemStack inSlot = getStackInSlot(0);

        if (inSlot.isEmpty())
            return ItemStack.EMPTY;

        if (simulate) {
            if (inSlot.getCount() <= amount) {
                return inSlot.copy();
            } else {
                ItemStack copy = inSlot.copy();
                copy.setCount(amount);
                return copy;
            }
        } else {
            int max = Math.min(inSlot.getCount(), amount);
            ItemStack old = inSlot.copy();
            ItemStack dec = old.splitStack(max);

            crate.setContents(old);

            return dec;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }
}
