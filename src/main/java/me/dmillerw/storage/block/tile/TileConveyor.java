package me.dmillerw.storage.block.tile;

import me.dmillerw.storage.block.BlockConveyor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by dmillerw on 1/20/14
 */
public class TileConveyor extends TileCore implements ITickable {

    public float progress = 0.0F;
    public float previousProgress = 0.0F;

    public IBlockState conveyorState = null;

    private EnumFacing getFacing() {
        return worldObj.getBlockState(pos).getValue(BlockConveyor.FACING).getOpposite();
    }

    @SideOnly(Side.CLIENT)
    public float getOffsetX(float ticks) {
        return (float) this.getFacing().getOpposite().getFrontOffsetX() * getProgress(ticks);
    }

    @SideOnly(Side.CLIENT)
    public float getOffsetY(float ticks) {
        return (float) this.getFacing().getOpposite().getFrontOffsetY() * getProgress(ticks);
    }

    @SideOnly(Side.CLIENT)
    public float getOffsetZ(float ticks) {
        return (float) this.getFacing().getOpposite().getFrontOffsetZ() * getProgress(ticks);
    }

    private float getProgress(float ticks) {
        return -(previousProgress + (progress - previousProgress) * ticks);
    }

    @Override
    public void update() {
        final float STEP = 0.1F;

        this.previousProgress = this.progress;
        if (this.progress >= 1.0F) {
            BlockPos inFront = pos.offset(getFacing());
            if (worldObj.isAirBlock(inFront.up())) {
                worldObj.setBlockState(inFront.up(), getBlockState());

                this.conveyorState = null;
                this.previousProgress = 0F;
                this.progress = 0F;

                this.markDirtyAndNotify();
            }
        } else {
            if (conveyorState != null) {
                this.progress += STEP;
            } else {
                // Check to see if there's a valid block above
                if (!worldObj.isAirBlock(pos.up())) {
                    // If so, check to see if we would be placing it on a conveyor, or if there's room
                    BlockPos inFront = pos.offset(getFacing());

                    if (worldObj.isAirBlock(inFront.up())) {
                        TileEntity tile = worldObj.getTileEntity(inFront);

                        if (tile instanceof TileConveyor) {
                            // and the conveyor isn't currently working on anything, we can begin
                            if (((TileConveyor) tile).conveyorState == null) {
                                this.conveyorState = worldObj.getBlockState(pos.up());
                            }
                        } else {
                            this.conveyorState = worldObj.getBlockState(pos.up());
                        }

                        if (this.conveyorState != null) {
                            worldObj.setBlockToAir(pos.up());
                            markDirtyAndNotify();
                        }
                    }
                } else {
                    this.previousProgress = 0F;
                    this.progress = 0F;
                }
            }
        }
    }

    public IBlockState getBlockState() {
        return conveyorState;
    }

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        if (conveyorState != null) {
            compound.setInteger("stateId", Block.getIdFromBlock(conveyorState.getBlock()));
            compound.setInteger("stateMeta", conveyorState.getBlock().getMetaFromState(conveyorState));
        }

        compound.setFloat("previousProgress", previousProgress);
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        if (compound.hasKey("stateId") && compound.hasKey("stateMeta")) {
            this.conveyorState = Block.getBlockById(compound.getInteger("stateId")).getStateFromMeta(compound.getInteger("stateMeta"));
        }

        previousProgress = compound.getFloat("previousProgress");
        progress = previousProgress;
    }
}