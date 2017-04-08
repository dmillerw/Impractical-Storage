package me.dmillerw.storage.block;

import me.dmillerw.storage.block.property.UnlistedItemStack;
import me.dmillerw.storage.block.tile.TileItemBlock;
import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class BlockItemBlock extends Block implements ITileEntityProvider {

    public static final UnlistedItemStack ITEM = new UnlistedItemStack("item");

    public BlockItemBlock() {
        super(Material.ROCK);

        setUnlocalizedName(ModInfo.ID + ":item_block");

        setBlockUnbreakable();
        setResistance(100F);

        setDefaultState(this.blockState.getBaseState());
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile != null && tile instanceof TileItemBlock)
                ((TileItemBlock) tile).updateItemBlock(null);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile != null && tile instanceof TileItemBlock)
            ((TileItemBlock) tile).updateItemBlock(null);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (!worldIn.isRemote) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if (tile != null && tile instanceof TileItemBlock)
                ((TileItemBlock) tile).getDrop();
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileItemBlock) {
            return ((TileItemBlock) tile).item;
        }

        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if (tile != null) {
                    ItemStack drop = ((TileItemBlock)tile).getDrop();
                    if (drop != null) {
                        InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), drop);
                    }

                    worldIn.setBlockToAir(pos);
                }

                return true;
            } else {
                return false;
            }
        } else {
            return playerIn.isSneaking();
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return true;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileItemBlock)
            return ((TileItemBlock) tile).getExtendedBlockState(state);

        return super.getExtendedState(state, world, pos);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[0], new IUnlistedProperty[]{ITEM});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileItemBlock();
    }
}