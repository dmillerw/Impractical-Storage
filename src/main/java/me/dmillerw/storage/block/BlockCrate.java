package me.dmillerw.storage.block;

import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * @author dmillerw
 */
public class BlockCrate extends Block {

    public BlockCrate() {
        super(Material.WOOD);

        setHardness(2F);
        setResistance(2F);

        setUnlocalizedName(ModInfo.ID + ":crate");
    }
}
