package me.dmillerw.storage.block;

import me.dmillerw.storage.lib.ModInfo;
import me.dmillerw.storage.lib.ModTab;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class BlockControllerInterface extends Block implements ITileEntityProvider {

    public BlockControllerInterface() {
        super(Material.IRON);

        setCreativeTab(ModTab.TAB);
        setUnlocalizedName(ModInfo.ID + ":controller_interface");

        setHardness(2F);
        setResistance(2F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }
}
