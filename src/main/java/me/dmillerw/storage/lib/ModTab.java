package me.dmillerw.storage.lib;

import me.dmillerw.storage.block.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * @author dmillerw
 */
public class ModTab extends CreativeTabs {

    public static final ModTab TAB = new ModTab();

    public ModTab() {
        super(ModInfo.ID);
    }

    @Override
    public Item getTabIconItem() {
        return ModBlocks.crate_item;
    }
}
