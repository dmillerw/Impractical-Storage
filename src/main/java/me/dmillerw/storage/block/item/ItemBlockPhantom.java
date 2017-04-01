package me.dmillerw.storage.block.item;

import me.dmillerw.storage.block.BlockPhantom;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemBlockPhantom extends ItemBlock {

    public ItemBlockPhantom(Block block) {
        super(block);

        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        BlockPhantom.EnumType type = BlockPhantom.EnumType.fromMetadata(stack.getMetadata());
        tooltip.add(I18n.translateToLocal("tooltip.phantom.type." + type.getName()));
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "." + BlockPhantom.EnumType.fromMetadata(stack.getMetadata()).getName();
    }
}
