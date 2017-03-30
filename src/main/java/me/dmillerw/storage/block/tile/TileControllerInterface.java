package me.dmillerw.storage.block.tile;

import me.dmillerw.storage.block.BlockControllerInterface;
import me.dmillerw.storage.block.ModBlocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class TileControllerInterface extends TileCore {

    public BlockPos selectedController;

    public void registerController(TileController tile) {
        if (selectedController == null) {
            selectedController = tile.getPos();
            setState(BlockControllerInterface.InterfaceState.ACTIVE);
        } else {
            if (selectedController != tile.getPos()) {
                setState(BlockControllerInterface.InterfaceState.ERROR);
            }
        }
    }

    private void setState(BlockControllerInterface.InterfaceState state) {
        worldObj.setBlockState(pos,
                ModBlocks.controller_interface.getDefaultState().withProperty(BlockControllerInterface.STATE, state));
    }

    private TileController getController() {
        if (selectedController == null || selectedController == BlockPos.ORIGIN)
            return null;

        if (worldObj.getBlockState(pos).getValue(BlockControllerInterface.STATE) == BlockControllerInterface.InterfaceState.ERROR)
            return null;

        TileEntity tile = worldObj.getTileEntity(selectedController);
        if (tile == null || !(tile instanceof TileController))
            return null;

        return (TileController) tile;
    }

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        if (selectedController != null)
            compound.setLong("selected", selectedController.toLong());
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        if (compound.hasKey("selected"))
            selectedController = BlockPos.fromLong(compound.getLong("selected"));
        else
            selectedController = null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getController() != null;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return (T) getController().itemHandler;
    }
}