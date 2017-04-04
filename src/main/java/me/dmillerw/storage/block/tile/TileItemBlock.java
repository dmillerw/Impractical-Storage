package me.dmillerw.storage.block.tile;

import me.dmillerw.storage.block.BlockItemBlock;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

/**
 * @author dmillerw
 */
public class TileItemBlock extends TileCore {

    public static boolean DROPS = true;

    public ItemStack tileRenderItem;

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

        tileRenderItem = null;
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
            ItemStack stack = (force == null || force.stackSize <= 0) ? controller.getStackForPosition(pos) : force;

            if (stack != null && stack.stackSize > 0) {
                if (stack.getItem() instanceof ItemBlock) {
                    Block block = Block.getBlockFromItem(stack.getItem());

                    boolean n_isBlock = true;
                    String n_itemBlock = ForgeRegistries.BLOCKS.getKey(block).toString();
                    int n_itemBlockMeta = stack.getMetadata();

                    if (n_isBlock != isBlock || !(n_itemBlock.equals(itemBlock)) || n_itemBlockMeta != itemBlockMeta) {
                        isBlock = n_isBlock;
                        itemBlock = n_itemBlock;
                        itemBlockMeta = n_itemBlockMeta;

                        markDirtyAndNotify();
                    }

                    return;
                } else {
                    Item item = stack.getItem();

                    boolean n_isBlock = false;
                    String n_itemBlock = ForgeRegistries.ITEMS.getKey(item).toString();
                    int n_itemBlockMeta = stack.getMetadata();

                    if (n_isBlock != isBlock || !(n_itemBlock.equals(itemBlock)) || n_itemBlockMeta != itemBlockMeta) {
                        isBlock = n_isBlock;
                        itemBlock = n_itemBlock;
                        itemBlockMeta = n_itemBlockMeta;

                        markDirtyAndNotify();
                    }

                    return;
                }
            }
        }

        itemBlock = null;
        itemBlockMeta = 0;

        markDirtyAndNotify();
    }

    public ItemStack getDrop() {
        if (!DROPS) return null;

        TileController controller = getController();
        if (controller != null) {
            int slot = controller.getSlotForPosition(pos);

            if (slot != -1) {
                ItemStack drop = controller.getStackInSlot(slot);
                if (drop != null && drop.stackSize > 0) drop = drop.copy();

                controller.setInventorySlotContents(slot, null, false, true, false);

                return drop;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public IExtendedBlockState getExtendedBlockState(IBlockState state) {
        return ((IExtendedBlockState)state)
                .withProperty(BlockItemBlock.IS_BLOCK, isBlock)
                .withProperty(BlockItemBlock.RENDER_VALUE, itemBlock)
                .withProperty(BlockItemBlock.RENDER_VALUE_META, itemBlockMeta);
    }
}
