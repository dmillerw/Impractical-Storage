package me.dmillerw.storage.block.tile;

import me.dmillerw.storage.block.BlockItemBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IExtendedBlockState;

/**
 * @author dmillerw
 */
public class TileItemBlock extends TileCore {

    public static boolean DROPS = true;

    public ItemStack item = null;
    private BlockPos controllerPos;

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        super.writeToDisk(compound);

        if (controllerPos != null)
            compound.setLong("controller", controllerPos.toLong());

        NBTTagCompound tag = new NBTTagCompound();
        item.writeToNBT(tag);
        compound.setTag("item", tag);
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        super.readFromDisk(compound);

        if (compound.hasKey("controller")) {
            controllerPos = BlockPos.fromLong(compound.getLong("controller"));
        } else {
            controllerPos = null;
        }

        if (compound.hasKey("item")) {
            item = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("item"));
        } else {
            item = null;
        }
    }

    public void setController(TileController controller) {
        this.controllerPos = controller.getPos();
    }

    private TileController getController() {
        if (controllerPos == null || controllerPos.equals(BlockPos.ORIGIN))
            return null;

        return (TileController) worldObj.getTileEntity(controllerPos);
    }

    public void updateItemBlock(ItemStack force) {
        TileController controller = getController();
        if (controller != null) {
            this.item = force == null ? controller.getStackForPosition(pos) : force;
            this.markDirtyAndNotify();
        } else {
            this.item = null;
            this.markDirtyAndNotify();
        }
    }

    public ItemStack getDrop() {
        if (!DROPS) return null;

        TileController controller = getController();
        if (controller != null) {
            int slot = controller.getSlotForPosition(pos);
            if (slot == -1)
                return null;

            ItemStack drop = controller.getStackInSlot(slot).copy();
            controller.setInventorySlotContents(slot, null, false, true, false);

            return drop;
        } else {
            return null;
        }
    }

    public IExtendedBlockState getExtendedBlockState(IBlockState state) {
        return ((IExtendedBlockState) state).withProperty(BlockItemBlock.ITEM, item);
    }
}