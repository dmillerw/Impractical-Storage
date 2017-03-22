package me.dmillerw.storage.block.tile;

import me.dmillerw.storage.block.BlockItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * @author dmillerw
 */
public class TileItemBlock extends TileCore {

    @SideOnly(Side.CLIENT)
    public EntityItem tileRenderItem;

    public boolean isBlock;
    public String itemBlock;
    public int itemBlockMeta;

    private BlockPos controllerPos;

    @Override
    public void writeToDisk(NBTTagCompound compound) {
        super.writeToDisk(compound);

        if (controllerPos != null) compound.setLong("controller", controllerPos.toLong());

        compound.setBoolean("isBlock", isBlock);
        if (itemBlock != null) compound.setString("itemBlock", itemBlock);
        compound.setInteger("itemBlockMeta", itemBlockMeta);
    }

    @Override
    public void readFromDisk(NBTTagCompound compound) {
        super.readFromDisk(compound);

        if (compound.hasKey("controller")) {
            controllerPos = BlockPos.fromLong(compound.getLong("controller"));
        } else {
            controllerPos = null;
        }

        isBlock = compound.getBoolean("isBlock");
        itemBlock = compound.getString("itemBlock");
        itemBlockMeta = compound.getInteger("itemBlockMeta");
    }

    public void setController(TileController controller) {
        this.controllerPos = controller.getPos();
    }

    private TileController getController() {
        if (controllerPos == null || controllerPos.equals(BlockPos.ORIGIN))
            return null;

        return (TileController) world.getTileEntity(controllerPos);
    }

    public void updateItemBlock(ItemStack force) {
        TileController controller = getController();
        if (controller != null) {
            ItemStack stack = force.isEmpty() ? controller.getStackForPosition(pos) : force;

            if (!stack.isEmpty()) {
                if (stack.getItem() instanceof ItemBlock) {
                    Block block = Block.getBlockFromItem(stack.getItem());

                    isBlock = true;
                    itemBlock = ForgeRegistries.BLOCKS.getKey(block).toString();
                    itemBlockMeta = stack.getItemDamage();

                    markDirtyAndNotify();

                    return;
                } else {
                    Item item = stack.getItem();

                    isBlock = false;
                    itemBlock = ForgeRegistries.ITEMS.getKey(item).toString();
                    itemBlockMeta = stack.getItemDamage();

                    markDirtyAndNotify();

                    return;
                }
            }
        }

        itemBlock = null;
        itemBlockMeta = 0;

        markDirtyAndNotify();
    }

    public ItemStack getDrop() {
        TileController controller = getController();
        if (controller != null) {
            int slot = controller.getSlotForPosition(pos);
            ItemStack drop = controller.getStackInSlot(slot).copy();
            controller.setInventorySlotContents(slot, ItemStack.EMPTY);
            return drop;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public IExtendedBlockState getExtendedBlockState(IBlockState state) {
        return ((IExtendedBlockState)state)
                .withProperty(BlockItemBlock.IS_BLOCK, isBlock)
                .withProperty(BlockItemBlock.RENDER_VALUE, itemBlock)
                .withProperty(BlockItemBlock.RENDER_VALUE_META, itemBlockMeta);
    }
}
