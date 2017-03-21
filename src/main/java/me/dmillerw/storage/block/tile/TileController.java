package me.dmillerw.storage.block.tile;

import me.dmillerw.storage.block.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class TileController extends TileCore {

    private static final int NUM_X_BITS = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;

    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;

    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    private static boolean isInBounds(BlockPos pos, BlockPos pos1, BlockPos pos2) {
//        return pos.getX() >= pos1.getX() && pos.getX() <= pos2.getX() &&
//                pos.getY() >= pos1.getY() && pos.getY() <= pos2.getY() &&
//                pos.getZ() >= pos2.getZ() && pos.getZ() <= pos2.getZ();
        return true;
    }

    private static long getLongFromPosition(int x, int y, int z) {
        return ((long) x & X_MASK) << X_SHIFT | ((long) y & Y_MASK) << Y_SHIFT | ((long) z & Z_MASK) << 0;
    }

    public static class ItemHandler implements IItemHandler {

        private TileController controller;

        private ItemHandler(TileController controller) {
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
            if (stack.isEmpty())
                return ItemStack.EMPTY;

            if (!(stack.getItem() instanceof ItemBlock))
                return ItemStack.EMPTY;

            ItemStack stackInSlot = controller.getStackInSlot(slot);

            int m;
            if (!stackInSlot.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                    return stack;

                m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot)) - stackInSlot.getCount();

                if (stack.getCount() <= m) {
                    if (!simulate) {
                        ItemStack copy = stack.copy();
                        copy.grow(stackInSlot.getCount());
                        controller.setInventorySlotContents(slot, copy);
                        controller.markDirty();
                    }

                    return ItemStack.EMPTY;
                } else {
                    // copy the stack to not modify the original one
                    stack = stack.copy();
                    if (!simulate) {
                        ItemStack copy = stack.splitStack(m);
                        copy.grow(stackInSlot.getCount());
                        controller.setInventorySlotContents(slot, copy);
                        controller.markDirty();
                        return stack;
                    } else {
                        stack.shrink(m);
                        return stack;
                    }
                }
            } else {
                m = Math.min(stack.getMaxStackSize(), getSlotLimit(slot));
                if (m < stack.getCount()) {
                    // copy the stack to not modify the original one
                    stack = stack.copy();
                    if (!simulate) {
                        controller.setInventorySlotContents(slot, stack.splitStack(m));
                        controller.markDirty();
                        return stack;
                    } else {
                        stack.shrink(m);
                        return stack;
                    }
                } else {
                    if (!simulate) {
                        controller.setInventorySlotContents(slot, stack);
                        controller.markDirty();
                    }
                    return ItemStack.EMPTY;
                }
            }
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            slot = getSlots() - slot - 1;

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

                controller.setInventorySlotContents(slot, old);
                controller.markDirty();

                return decr;
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }
    }

    private ItemHandler itemHandler = new ItemHandler(this);
    public NonNullList<ItemStack> inventory;

    private boolean isReady = false;

    private BlockPos origin = BlockPos.ORIGIN;
    private BlockPos end = BlockPos.ORIGIN;

    private int height = 1;
    private int xLength = 1;
    private int zLength = 1;
    private int totalSize;

    private long[] slotToWorldMap = new long[0];
    // [Y][X][Z]
    private int[][][] worldToSlotMap = new int[0][0][0];

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        compound.setBoolean("ready", isReady);

        if (isReady) {
            compound.setInteger("size", totalSize);

            NBTTagCompound inv = new NBTTagCompound();
            ItemStackHelper.saveAllItems(inv, inventory);
            compound.setTag("inventory", inv);

            compound.setLong("origin", origin.toLong());
            compound.setLong("end", end.toLong());
        }
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        isReady = compound.getBoolean("ready");

        if (isReady) {
            totalSize = compound.getInteger("size");

            NBTTagCompound inv = compound.getCompoundTag("inventory");
            ItemStackHelper.loadAllItems(inv, inventory);

            origin = BlockPos.fromLong(compound.getLong("origin"));
            end = BlockPos.fromLong(compound.getLong("end"));

            updateBounds(origin, end, false);
        }
    }

    public void updateBounds(BlockPos pos1, BlockPos pos2, boolean initInventory) {
        origin = pos1.subtract(pos);
        end = pos2.subtract(pos);

        height = 1 + end.getY() - origin.getY();
        xLength = 1 + end.getX() - origin.getX();
        zLength = 1 + end.getZ() - origin.getZ();

        totalSize = height * xLength * zLength;

        initializeArrays(initInventory);
    }

    private void initializeArrays(boolean initInventory) {
        if (initInventory) inventory = NonNullList.withSize(totalSize, ItemStack.EMPTY);

        slotToWorldMap = new long[totalSize];
        worldToSlotMap = new int[height][xLength][zLength];

        int slot = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < zLength; z++) {
                for (int x = 0; x < xLength; x++) {
                    slotToWorldMap[slot] = getLongFromPosition(x, y, z);
                    worldToSlotMap[y][x][z] = slot;

                    slot++;
                }
            }
        }
    }

    public void clearInventory() {
        for (int i=0; i<inventory.size(); i++) setInventorySlotContents(0, ItemStack.EMPTY);
    }

    public int getSlotForPosition(BlockPos pos) {
        try {
            pos = pos.subtract(this.pos);

            if (!isInBounds(pos, origin, end))
                return -1;

            int x = pos.subtract(origin).getX();
            int y = pos.subtract(origin).getY();
            int z = pos.subtract(origin).getZ();

            return worldToSlotMap[y][x][z];
        } catch (Exception ex) {
            return 0;
        }
    }

    public ItemStack getStackForPosition(BlockPos pos) {
        return getStackInSlot(getSlotForPosition(pos));
    }

    public ItemStack getStackInSlot(int slot) {
        return inventory.get(slot);
    }

    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        BlockPos pos = BlockPos.fromLong(slotToWorldMap[slot]).add(origin).add(getPos());
        IBlockState state = world.getBlockState(pos);

        inventory.set(slot, itemStack);

        if (itemStack.isEmpty()) {
            if (state != null && state.getBlock() == ModBlocks.item_block) {
                world.setBlockToAir(pos);
            }
        } else {
            if (state != null && state.getBlock() == ModBlocks.item_block) {
                TileEntity tile = world.getTileEntity(pos);
                if (tile != null && tile instanceof TileItemBlock)
                    ((TileItemBlock) tile).updateItemBlock(itemStack);
            } else {
                world.setBlockState(pos, ModBlocks.item_block.getDefaultState());
                TileEntity tile = world.getTileEntity(pos);
                if (tile != null && tile instanceof TileItemBlock) {
                    ((TileItemBlock) tile).setController(this);
                    ((TileItemBlock) tile).updateItemBlock(itemStack);
                }
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) itemHandler;
        return super.getCapability(capability, facing);
    }
}
