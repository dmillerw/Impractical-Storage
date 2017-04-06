package me.dmillerw.storage.block.tile;

import me.dmillerw.storage.block.BlockConveyor;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Dylan Miller on 1/20/14
 */
public class TileConveyor extends TileCore implements ITickable {

    public float progressStep = 0.0F;
    public float progress = 0.0F;

    public ItemStack stored = ItemStack.EMPTY;

    public boolean redstoneState = false;
    private boolean clearNextTick = false;

    private EnumFacing getFacing() {
        return world.getBlockState(pos).getValue(BlockConveyor.FACING);
    }

    @SideOnly(Side.CLIENT)
    public float getProgress(float ticks) {
        if (ticks > 1.0F) {
            ticks = 1.0F;
        }

        return this.progressStep + (this.progress - this.progressStep) * ticks;
    }

    @SideOnly(Side.CLIENT)
    public float getOffsetX(float ticks) {
        return (float) this.getFacing().getFrontOffsetX() * (-progress + ticks);
    }

    @SideOnly(Side.CLIENT)
    public float getOffsetY(float ticks) {
        return (float) this.getFacing().getFrontOffsetY() * (-progress + ticks);
    }

    @SideOnly(Side.CLIENT)
    public float getOffsetZ(float ticks) {
        return (float) this.getFacing().getFrontOffsetZ() * (-progress + ticks);
    }

    private float getExtendedProgress(float progress) {
        return progress - 1.0F;
    }

    @Override
    public void update() {
        if (clearNextTick) {
            stored = ItemStack.EMPTY;
            progress = 0;

            markDirtyAndNotify();

            clearNextTick = false;

            return;
        }

        if (!stored.isEmpty()) {
            if (progressStep < 0.1F && !redstoneState) {
                progressStep += 0.01F;
            }

            if (progressStep > 0F && redstoneState) {
                progressStep -= 0.01F;
            }

            if (progress < 1.0F) {
                progress += progressStep;
            } else if (progress > 1.0F) {
                progress = 1.0F;
            }
        }

        if (!world.isRemote) {
            if (redstoneState) {
                return;
            }

            int mX = pos.getX() + getFacing().getOpposite().getDirectionVec().getX();
            int mY = pos.getY() + 1;
            int mZ = pos.getZ() + getFacing().getOpposite().getDirectionVec().getZ();

            BlockPos mPos = new BlockPos(mX, mY, mZ);

            if (!world.isAirBlock(mPos)) {
                return;
            }

            List<EntityLivingBase> intersectingEntities = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(mPos, mPos.up()));
            if (intersectingEntities != null && intersectingEntities.size() > 0) {
                return;
            }

            TileEntity potentialConveyor = world.getTileEntity(new BlockPos(mX, pos.getY(), mZ));
            if (potentialConveyor != null && potentialConveyor instanceof TileConveyor) {
                TileConveyor tile = (TileConveyor) potentialConveyor;
                if (!tile.stored.isEmpty()) {
                    return;
                } else {
                    if (progress >= 0.99F && !stored.isEmpty()) {
                        tile.stored = this.stored;
                        tile.progress = 0F;

                        tile.markDirtyAndNotify();

                        clearNextTick = true;

                        return;
                    }
                }
            }

            if (stored.isEmpty()) {
                BlockPos pos = new BlockPos(this.pos);
                IBlockState state = world.getBlockState(pos.up());

                if (!world.isAirBlock(pos.up()) && !state.getBlock().hasTileEntity(state)) {
                    stored = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));
                    world.setBlockToAir(pos.up());

                    markDirtyAndNotify();
                }
            } else if (progress >= 1.0F && !stored.isEmpty()) {
                world.setBlockState(mPos, getBlockState());

                clearNextTick = true;
            }
        }
    }

    public float getProgress() {
        return progress;
    }

    public IBlockState getBlockState() {
        if (stored.isEmpty())
            return null;

        Block block = Block.getBlockFromItem(stored.getItem());
        return block.getStateFromMeta(stored.getMetadata());
    }

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        compound.setFloat("progress", progress);

        NBTTagCompound tag = new NBTTagCompound();
        stored.writeToNBT(tag);
        compound.setTag("item", tag);
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        progress = compound.getFloat("progress");
        progressStep = progress;
        stored = new ItemStack(compound.getCompoundTag("item"));
    }
}