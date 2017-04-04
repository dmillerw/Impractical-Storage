package me.dmillerw.storage.block;

import me.dmillerw.storage.lib.ModInfo;
import me.dmillerw.storage.lib.ModTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class BlockPhantom extends Block {

    public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", EnumType.class);

    public BlockPhantom() {
        super(Material.BARRIER);

        this.setBlockUnbreakable();
        this.setResistance(6000001.0F);
        this.disableStats();

        setCreativeTab(ModTab.TAB);

        this.translucent = true;

        setDefaultState(blockState.getBaseState().withProperty(TYPE, EnumType.BLOCK));

        setUnlocalizedName(ModInfo.ID + ":phantom");
    }

    //TODO Hide bounding-box unless holding item


    @Nullable
    @Override
    protected RayTraceResult rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox) {
        return super.rayTrace(pos, start, end, boundingBox);
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.INVISIBLE;
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
//        for (BlockPhantom.EnumType type : BlockPhantom.EnumType.values()) {
//            list.add(new ItemStack(itemIn, 1, type.getMetadata()));
//        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return blockState.getBaseState().withProperty(TYPE, EnumType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(TYPE).ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, TYPE);
    }

    public static enum EnumType implements IStringSerializable {
        BLOCK("block", 0),
        COLUMN("column", 1);

        private static final BlockPhantom.EnumType[] META_LOOKUP = new BlockPhantom.EnumType[values().length];

        private final String name;
        private final int metadata;
        private EnumType(String name, int metadata) {
            this.name = name;
            this.metadata = metadata;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public int getMetadata() {
            return this.metadata;
        }

        public static BlockPhantom.EnumType fromMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) meta = 0;
            return META_LOOKUP[meta];
        }

        static {
            for (BlockPhantom.EnumType type : values()) {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
