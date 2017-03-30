package me.dmillerw.storage.block;

import me.dmillerw.storage.block.tile.TileControllerInterface;
import me.dmillerw.storage.lib.ModInfo;
import me.dmillerw.storage.lib.ModTab;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

/**
 * @author dmillerw
 */
public class BlockControllerInterface extends Block implements ITileEntityProvider {

    public static final PropertyEnum<InterfaceState> STATE = PropertyEnum.create("state", InterfaceState.class);

    public BlockControllerInterface() {
        super(Material.IRON);

        setDefaultState(blockState.getBaseState().withProperty(STATE, InterfaceState.INACTIVE));

        setCreativeTab(ModTab.TAB);
        setUnlocalizedName(ModInfo.ID + ":controller_interface");

        setHardness(2F);
        setResistance(2F);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return blockState.getBaseState().withProperty(STATE, InterfaceState.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STATE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { STATE });
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileControllerInterface();
    }

    public static enum InterfaceState implements IStringSerializable {
        INACTIVE("inactive"),
        ACTIVE("active"),
        ERROR("error");

        private final String name;
        private InterfaceState(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }
}
