package me.dmillerw.storage.block.tile;

import me.dmillerw.storage.block.BlockController;
import me.dmillerw.storage.block.ModBlocks;
import me.dmillerw.storage.lib.compat.ItemStackHelper;
import me.dmillerw.storage.lib.compat.MathHelper_1_11;
import me.dmillerw.storage.lib.data.SortingType;
import me.dmillerw.storage.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Random;

/**
 * @author dmillerw
 */
public class TileController extends TileCore implements ITickable {

    private static final int NUM_X_BITS = 1 + MathHelper_1_11.log2(MathHelper_1_11.smallestEncompassingPowerOfTwo(30000000));
    private static final int NUM_Z_BITS = NUM_X_BITS;
    private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;

    private static final int Y_SHIFT = NUM_Z_BITS;
    private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;

    private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
    private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
    private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;

    private static final int MAX_BLOCK_STACK_SIZE = 1;
    private static final int MAX_ITEM_STACK_SIZE = 16;

    private static long getLongFromPosition(int x, int y, int z) {
        return ((long) x & X_MASK) << X_SHIFT | ((long) y & Y_MASK) << Y_SHIFT | ((long) z & Z_MASK) << 0;
    }

    private static int getMaxStackSize(ItemStack itemStack) {
        return itemStack.getItem() instanceof ItemBlock ? MAX_BLOCK_STACK_SIZE : MAX_ITEM_STACK_SIZE;
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

        @Override
        public ItemStack getStackInSlot(int slot) {
            return controller.getStackInSlot(slot);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack == null || stack.stackSize <= 0)
                return null;

            slot = getSlots() - slot - 1;

            ItemStack stackInSlot = controller.getStackInSlot(slot);

            int m;
            if (stackInSlot != null) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot))
                    return stack;

                m = Math.min(stack.getMaxStackSize(), getMaxStackSize(stackInSlot)) - stackInSlot.stackSize;

                if (stack.stackSize <= m) {
                    if (!simulate) {
                        ItemStack copy = stack.copy();
                        copy.stackSize += stackInSlot.stackSize;
                        controller.setInventorySlotContents(slot, copy);
                        controller.markDirty();
                    }

                    return null;
                } else {
                    // copy the stack to not modify the original one
                    stack = stack.copy();
                    if (!simulate) {
                        ItemStack copy = stack.splitStack(m);
                        copy.stackSize += stackInSlot.stackSize;
                        controller.setInventorySlotContents(slot, copy);
                        controller.markDirty();
                        return stack;
                    } else {
                        stack.stackSize -= m;
                        return stack;
                    }
                }
            } else {
                m = Math.min(stack.getMaxStackSize(), getMaxStackSize(stack));
                if (m < stack.stackSize) {
                    // copy the stack to not modify the original one
                    stack = stack.copy();
                    if (!simulate) {
                        controller.setInventorySlotContents(slot, stack.splitStack(m));
                        controller.markDirty();
                        return stack;
                    } else {
                        stack.stackSize -= m;
                        return stack;
                    }
                } else {
                    if (!simulate) {
                        controller.setInventorySlotContents(slot, stack);
                        controller.markDirty();
                    }
                    return null;
                }
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (amount == 0)
                return null;

            ItemStack stackInSlot = controller.getStackInSlot(slot);

            if (stackInSlot == null || stackInSlot.stackSize <= 0)
                return null;

            if (simulate) {
                if (stackInSlot.stackSize < amount) {
                    return stackInSlot.copy();
                } else {
                    ItemStack copy = stackInSlot.copy();
                    copy.stackSize = amount;
                    return copy;
                }
            } else {
                int m = Math.min(stackInSlot.stackSize, amount);
                ItemStack old = controller.getStackInSlot(slot);
                ItemStack decr = old.splitStack(m);

                controller.setInventorySlotContents(slot, old);
                controller.markDirty();

                return decr;
            }
        }
    }

    private static class QueueElement {

        public int slot;
        public ItemStack itemStack;
    }


    private Random random = new Random();

    private ItemHandler itemHandler = new ItemHandler(this);
    public ItemStack[] inventory = new ItemStack[0];

    public BlockPos origin = null;
    public BlockPos end = null;

    public int rawX = CommonProxy.defaultX;
    public int rawY = CommonProxy.defaultY;
    public int rawZ = CommonProxy.defaultZ;

    public int height = 1;
    public int xLength = 1;
    public int zLength = 1;
    private int totalSize;

    public SortingType sortingType = SortingType.ROWS;

    private int scanCounter = 0;

    public boolean showBounds = false;

    private long[] slotToWorldMap = new long[0];

    // [Y][X][Z]
    private int[][][] worldToSlotMap = new int[0][0][0];
    private boolean[][][] worldOcclusionMap = new boolean[0][0][0];

    // Block Queue
    private ArrayDeque<QueueElement> blockQueue = new ArrayDeque<>();
    private int blockQueueTickCounter = 0;

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        if (isReady()) {
            compound.setLong("origin", origin.toLong());
            compound.setLong("end", end.toLong());

            compound.setInteger("rawX", rawX);
            compound.setInteger("rawY", rawY);
            compound.setInteger("rawZ", rawZ);

            compound.setInteger("height", height);
            compound.setInteger("xLength", xLength);
            compound.setInteger("zLength", zLength);

            compound.setInteger("sortingType", sortingType.ordinal());

            compound.setBoolean("showBounds", showBounds);

            NBTTagList nbt_slotToWorldMap = new NBTTagList();
            for (int i = 0; i < slotToWorldMap.length; i++) {
                nbt_slotToWorldMap.appendTag(new NBTTagLong(slotToWorldMap[i]));
            }
            compound.setTag("slotToWorldMap", nbt_slotToWorldMap);

            NBTTagList nbt_worldToSlotMap = new NBTTagList();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < xLength; x++) {
                    for (int z = 0; z < zLength; z++) {
                        int slot = worldToSlotMap[y][x][z];
                        if (slot != -1) {
                            NBTTagCompound tag = new NBTTagCompound();

                            tag.setInteger("_x", x);
                            tag.setInteger("_y", y);
                            tag.setInteger("_z", z);
                            tag.setInteger("slot", slot);

                            nbt_worldToSlotMap.appendTag(tag);
                        }
                    }
                }
            }
            compound.setTag("worldToSlotMap", nbt_worldToSlotMap);

            NBTTagList nbt_worldOcclusionMap = new NBTTagList();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < xLength; x++) {
                    for (int z = 0; z < zLength; z++) {
                        if (worldOcclusionMap[y][x][z]) {
                            NBTTagCompound tag = new NBTTagCompound();

                            tag.setInteger("_x", x);
                            tag.setInteger("_y", y);
                            tag.setInteger("_z", z);

                            nbt_worldOcclusionMap.appendTag(tag);
                        }
                    }
                }
            }
            compound.setTag("worldOcclusionMap", nbt_worldOcclusionMap);

            NBTTagCompound inv = new NBTTagCompound();
            ItemStackHelper.saveAllItems(inv, inventory);
            compound.setTag("inventory", inv);

            // Block Queue
            NBTTagList nbt_blockQueue = new NBTTagList();
            for (QueueElement element : blockQueue) {
                NBTTagCompound tag = new NBTTagCompound();

                tag.setInteger("slot", element.slot);

                NBTTagCompound item = new NBTTagCompound();
                element.itemStack.writeToNBT(item);
                tag.setTag("item", item);

                nbt_blockQueue.appendTag(tag);
            }
            compound.setTag("blockQueue", nbt_blockQueue);

            compound.setInteger("blockQueueCounter", blockQueueTickCounter);
        }
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        if (compound.hasKey("origin") && compound.hasKey("end")) {
            origin = BlockPos.fromLong(compound.getLong("origin"));
            end = BlockPos.fromLong(compound.getLong("end"));

            rawX = compound.getInteger("rawX");
            rawY = compound.getInteger("rawY");
            rawZ = compound.getInteger("rawZ");

            height = compound.getInteger("height");
            xLength = compound.getInteger("xLength");
            zLength = compound.getInteger("zLength");
            totalSize = height * xLength * zLength;

            sortingType = SortingType.VALUES[compound.getInteger("sortingType")];

            showBounds = compound.getBoolean("showBounds");

            inventory = new ItemStack[totalSize];

            slotToWorldMap = new long[totalSize];
            NBTTagList nbt_slotToWorldMap = compound.getTagList("slotToWorldMap", Constants.NBT.TAG_LONG);
            for (int i = 0; i < nbt_slotToWorldMap.tagCount(); i++) {
                slotToWorldMap[i] = ((NBTTagLong) nbt_slotToWorldMap.get(i)).getLong();
            }

            worldToSlotMap = new int[height][xLength][zLength];
            NBTTagList nbt_worldToSlotMap = compound.getTagList("worldToSlotMap", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < nbt_worldToSlotMap.tagCount(); i++) {
                NBTTagCompound tag = nbt_worldToSlotMap.getCompoundTagAt(i);

                int x = tag.getInteger("_x");
                int y = tag.getInteger("_y");
                int z = tag.getInteger("_z");
                int slot = tag.getInteger("slot");

                worldToSlotMap[y][x][z] = slot;
            }

            worldOcclusionMap = new boolean[height][xLength][zLength];
            NBTTagList nbt_worldOcclusionMap = compound.getTagList("worldOcclusionMap", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < nbt_worldOcclusionMap.tagCount(); i++) {
                NBTTagCompound tag = nbt_worldOcclusionMap.getCompoundTagAt(i);

                int x = tag.getInteger("_x");
                int y = tag.getInteger("_y");
                int z = tag.getInteger("_z");

                worldOcclusionMap[y][x][z] = true;
            }

            NBTTagCompound inv = compound.getCompoundTag("inventory");
            ItemStackHelper.loadAllItems(inv, inventory);

            // Block Queue
            NBTTagList nbt_blockQueue = compound.getTagList("blockQueue", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < nbt_blockQueue.tagCount(); i++) {
                NBTTagCompound tag = nbt_blockQueue.getCompoundTagAt(i);
                QueueElement element = new QueueElement();
                element.slot = tag.getInteger("slot");
                element.itemStack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("item"));
                blockQueue.add(element);
            }

            blockQueueTickCounter = compound.getInteger("blockQueueCounter");
        }
    }

    public void initialize(EnumFacing facing) {
        if (!getWorld().isRemote) {
            if (facing != null) {
                updateRawBounds(facing, rawX, rawY, rawZ);
            }
        }
    }

    public void setShowBounds(boolean showBounds) {
        this.showBounds = showBounds;
    }

    public void setSortingType(SortingType sortingType) {
        this.sortingType = sortingType;
        this.updateRawBounds(getWorld().getBlockState(pos).getValue(BlockController.FACING), rawX, rawY, rawZ);
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            if (CommonProxy.useBlockQueue()) {
                blockQueueTickCounter++;

                if (blockQueueTickCounter >= CommonProxy.blockUpdateRate) {
                    for (int i = 0; i < Math.min(blockQueue.size(), CommonProxy.blockUpdateBatch); i++) {
                        QueueElement element = blockQueue.pop();
                        setBlock(element.slot, element.itemStack);
                    }

                    blockQueueTickCounter = 0;
                }
            }

            if (scanCounter >= CommonProxy.blockUpdateRate) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < zLength; z++) {
                        for (int x = 0; x < xLength; x++) {
                            if (!worldOcclusionMap[y][x][z]) {
                                BlockPos pos = new BlockPos(x, y, z).add(origin);
                                IBlockState state = getWorld().getBlockState(pos);
                                Block block = state.getBlock();

                                if (block != ModBlocks.controller && block != ModBlocks.item_block && !getWorld().isAirBlock(pos)) {
                                    ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
                                    getWorld().setBlockToAir(pos);

                                    int slot = getSlotForPosition(pos);
                                    if (slot == -1) {
                                        int i = 0;
                                        for (i = 0; i < totalSize; i++) {
                                            long world = slotToWorldMap[i];
                                            if (world == -1) {
                                                slotToWorldMap[i] = getLongFromPosition(x, y, z);
                                                worldToSlotMap[y][x][z] = i;

                                                break;
                                            }
                                        }

                                        slot = i;
                                    }

                                    setInventorySlotContents(slot, stack);
                                }
                            }
                        }
                    }
                }
            } else {
                scanCounter++;
            }
        }
    }

    public boolean isReady() {
        return origin != null && end != null;
    }

    public void updateRawBounds(EnumFacing facing, int x, int y, int z) {
        EnumFacing posX = facing.rotateY();
        EnumFacing negX = posX.getOpposite();

        int modx = 1;
        if (x == 1) modx = 0;
        else if (x % 2 == 0) modx = x / 2;
        else modx = (x - 1) / 2;

        this.rawX = x;
        this.rawY = y;
        this.rawZ = z;

        //TODO: offsets

        BlockPos origin = new BlockPos(pos);
        origin = origin.offset(negX, modx);
        origin = origin.offset(EnumFacing.UP, 0);
        origin = origin.offset(facing, 1);

        BlockPos end = new BlockPos(pos);
        end = end.offset(posX, modx);
        end = end.offset(EnumFacing.UP, y);
        end = end.offset(facing, z);

        BlockPos low = new BlockPos(
                Math.min(origin.getX(), end.getX()),
                Math.min(origin.getY(), end.getY()),
                Math.min(origin.getZ(), end.getZ())
        );

        BlockPos high = new BlockPos(
                Math.max(origin.getX(), end.getX()),
                Math.max(origin.getY(), end.getY()),
                Math.max(origin.getZ(), end.getZ())
        );

        setBounds(low, high);
    }

    public void setBounds(BlockPos pos1, BlockPos pos2) {
        if (origin != null && end != null) {
            dropInventory();
            clearInventory();
        }

        origin = pos1;
        end = pos2;

        height = end.getY() - origin.getY();
        xLength = 1 + end.getX() - origin.getX();
        zLength = 1 + end.getZ() - origin.getZ();

        worldOcclusionMap = new boolean[height][xLength][zLength];

        int occludedSpots = 0;
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < zLength; z++) {
                for (int x = 0; x < xLength; x++) {
                    if (!getWorld().isAirBlock(origin.add(x, y, z))) {
                        worldOcclusionMap[y][x][z] = true;
                        occludedSpots++;
                    }
                }
            }
        }

        totalSize = (height * xLength * zLength) - occludedSpots;

        inventory = new ItemStack[totalSize];

        slotToWorldMap = new long[totalSize];
        worldToSlotMap = new int[height][xLength][zLength];

        if (sortingType == SortingType.COLUMNS) {
            int slot = totalSize - 1;
            for (int x = 0; x < xLength; x++) {
                for (int z = 0; z < zLength; z++) {
                    for (int y = 0; y < height; y++) {
                        slotToWorldMap[slot] = getLongFromPosition(x, y, z);
                        worldToSlotMap[y][x][z] = slot;

                        slot--;
                    }
                }
            }
        } else {
            int slot = totalSize - 1;
            for (int y = 0; y < height; y++) {
                for (int z = 0; z < zLength; z++) {
                    for (int x = 0; x < xLength; x++) {
                        if (!worldOcclusionMap[y][x][z]) {
                            if (sortingType == SortingType.ROWS) {
                                slotToWorldMap[slot] = getLongFromPosition(x, y, z);
                                worldToSlotMap[y][x][z] = slot;
                            } else {
                                slotToWorldMap[slot] = -1;
                                worldToSlotMap[y][x][z] = -1;
                            }

                            slot--;
                        }
                    }
                }
            }
        }
    }

    public void onBlockBreak() {
        if (CommonProxy.dropBlocks) {
            dropInventory();
        } else {
            for (int i = 0; i < totalSize; i++) {
                ItemStack item = getStackInSlot(i);
                if (!ItemStackHelper.isEmpty(item) && (item.getItem() instanceof ItemBlock)) {
                    BlockPos pos = BlockPos.fromLong(slotToWorldMap[i]).add(origin);
                    getWorld().setBlockState(pos, Block.getBlockFromItem(item.getItem()).getStateFromMeta(item.getItemDamage()));
                    inventory[i] = null;
                }
            }

            dropInventory();
        }

        clearInventory();
    }

    public void dropInventory() {
        for (ItemStack item : this.inventory)
            if (!ItemStackHelper.isEmpty(item))
                InventoryHelper.spawnItemStack(getWorld(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
    }

    public void clearInventory() {
        for (int i = 0; i < inventory.length; i++)
            setInventorySlotContents(i, null);
    }

    public int getSlotForPosition(BlockPos pos) {
        pos = pos.subtract(origin);
        return worldToSlotMap[pos.getY()][pos.getX()][pos.getZ()];
    }

    public ItemStack getStackForPosition(BlockPos pos) {
        return getStackInSlot(getSlotForPosition(pos));
    }

    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    private BlockPos getNextRandomPosition() {
        int x = random.nextInt(xLength);
        int y = 0;
        int z = random.nextInt(zLength);

        boolean failed = false;

        BlockPos pos = new BlockPos(x, y, z).add(origin);
        while (worldOcclusionMap[y][x][z] || !getWorld().isAirBlock(pos)) {
            pos = pos.up();
            y++;

            if (y >= height) {
                failed = true;
                break;
            }
        }

        if (failed) return getNextRandomPosition();

        pos = pos.subtract(origin);
        return pos;
    }

    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        if (!ItemStackHelper.isEmpty(itemStack)) {
            if (sortingType == SortingType.MESSY) {
                long longPos = slotToWorldMap[slot];
                if (longPos == -1) {
                    BlockPos pos = getNextRandomPosition();

                    slotToWorldMap[slot] = pos.toLong();
                    worldToSlotMap[pos.getY()][pos.getX()][pos.getZ()] = slot;
                }
            }
        }

        inventory[slot] = itemStack;

        if (CommonProxy.useBlockQueue()) {
            blockQueueTickCounter = 0;

            QueueElement element = new QueueElement();
            element.slot = slot;
            element.itemStack = itemStack;
            blockQueue.add(element);
        } else {
            setBlock(slot, itemStack);
        }
    }

    private void setBlock(int slot, ItemStack itemStack) {
        BlockPos pos = BlockPos.fromLong(slotToWorldMap[slot]);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        pos = pos.add(origin);

        IBlockState state = getWorld().getBlockState(pos);

        if (ItemStackHelper.isEmpty(itemStack)) {
            if (state != null && state.getBlock() == ModBlocks.item_block) {
                if (sortingType == SortingType.MESSY) {
                    slotToWorldMap[slot] = -1;
                    worldToSlotMap[y][x][z] = -1;
                }

                getWorld().setBlockToAir(pos);
            }
        } else {
            if (state != null && state.getBlock() == ModBlocks.item_block) {
                TileEntity tile = getWorld().getTileEntity(pos);
                if (tile != null && tile instanceof TileItemBlock)
                    ((TileItemBlock) tile).updateItemBlock(itemStack);
            } else {
                getWorld().setBlockState(pos, ModBlocks.item_block.getDefaultState());
                TileEntity tile = getWorld().getTileEntity(pos);
                if (tile != null && tile instanceof TileItemBlock) {
                    ((TileItemBlock) tile).setController(this);
                    ((TileItemBlock) tile).updateItemBlock(itemStack);
                }
            }
        }
    }

    public int getRedstoneLevel() {
        int i = 0;
        float f = 0;

        for (int j = 0; j < totalSize; j++) {
            ItemStack stack = getStackInSlot(j);
            if (stack != null && stack.stackSize > 0) {
                f += (float) stack.stackSize / (float) Math.min(getMaxStackSize(stack), stack.getMaxStackSize());
                ++i;
            }
        }

        f = f / (float)totalSize;

        return MathHelper_1_11.floor(f * 14F) + (i > 0 ? 1 : 0);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && isReady();
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (isReady())
            if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) itemHandler;
        return super.getCapability(capability, facing);
    }
}