package me.dmillerw.storage.block;

import me.dmillerw.storage.block.tile.TileController;
import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class BlockController extends Block implements ITileEntityProvider {

    public BlockController() {
        super(Material.IRON);

        setCreativeTab(CreativeTabs.MISC);
        setUnlocalizedName(ModInfo.ID + ":controller");
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile != null) {
                for (ItemStack item : ((TileController)tile).inventory) {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item);
                }
                ((TileController)tile).clearInventory();
            }
        }

        super.breakBlock(worldIn, pos, state);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileController();
    }
}
