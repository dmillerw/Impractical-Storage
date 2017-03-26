package me.dmillerw.storage.block.item;

import me.dmillerw.storage.block.BlockCrate;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemBlockCrate extends ItemBlock {

    public ItemBlockCrate(Block block) {
        super(block);

        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        BlockCrate.EnumType type = BlockCrate.EnumType.fromMetadata(stack.getMetadata());
        if (type.getBlockStorage() > 0) tooltip.add(I18n.translateToLocal("tooltip.capacity.block") + ": " + type.getBlockStorage());
        if (type.getItemStorage() > 0) tooltip.add(I18n.translateToLocal("tooltip.capacity.item") + ": " + type.getItemStorage());
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
