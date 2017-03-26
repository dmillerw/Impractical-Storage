package me.dmillerw.storage.block.item;

import me.dmillerw.storage.block.BlockCrate;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class ItemBlockCrate extends ItemBlock {

    public ItemBlockCrate(Block block) {
        super(block);

        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + BlockCrate.EnumType.fromMetadata(stack.getMetadata()).getName();
    }
}
