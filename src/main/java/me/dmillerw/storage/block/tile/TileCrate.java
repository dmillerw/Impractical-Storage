package me.dmillerw.storage.block.tile;

import me.dmillerw.storage.block.BlockCrate;
import me.dmillerw.storage.block.tile.inv.CrateItemHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class TileCrate extends TileCore {

    private ItemStack contents = ItemStack.EMPTY;
    private CrateItemHandler itemHandler = new CrateItemHandler(this);

    public ItemStack getContents() {
//        return contents;
        return new ItemStack(Items.DIAMOND);
    }

    public void setContents(ItemStack itemStack) {
        this.contents = itemStack;
        this.markDirtyAndNotify();
    }

    public BlockCrate.EnumType getCrateType() {
        return world.getBlockState(pos).getValue(BlockCrate.VARIANT);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return (T) itemHandler;
    }
}
