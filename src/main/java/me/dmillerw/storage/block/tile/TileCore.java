package me.dmillerw.storage.block.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

/**
 * Created by dmillerw
 */
public class TileCore extends TileEntity {

    @Override
    public final NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        writeToDisk(compound);
        return compound;
    }

    @Override
    public final void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readFromDisk(compound);
    }

    public void writeToDisk(NBTTagCompound compound) {

    }

    public void readFromDisk(NBTTagCompound compound) {

    }

    public void markDirtyAndNotify() {
        markDirty();

        IBlockState state = worldObj.getBlockState(pos);
        worldObj.notifyBlockUpdate(pos, state, state, 3);
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeToNBT(new NBTTagCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
        worldObj.markBlockRangeForRenderUpdate(pos, pos);
    }
}