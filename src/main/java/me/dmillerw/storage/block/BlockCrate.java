package me.dmillerw.storage.block;

import me.dmillerw.storage.lib.ModInfo;
import me.dmillerw.storage.lib.ModTab;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;

/**
 * @author dmillerw
 */
public class BlockCrate extends Block {

    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);

    public BlockCrate() {
        super(Material.WOOD);

        setHardness(2F);
        setResistance(2F);

        setCreativeTab(ModTab.TAB);

        setUnlocalizedName(ModInfo.ID + ":crate");

        setDefaultState(blockState.getBaseState().withProperty(VARIANT, EnumType.WOOD));
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(VARIANT, EnumType.fromMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumType type : EnumType.values()) {
            list.add(new ItemStack(this, 1, type.meta));
        }
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] { VARIANT });
    }

    public static enum EnumType implements IStringSerializable {
        WOOD(    0, "wood",     0,  8),
        IRON(    1, "iron",     0, 16),
        GOLD(    2, "gold",     0, 32),
        DIAMOND( 3, "diamond",  0, 64),
        OBSIDIAN(4, "obsidian", 8, 64);

        private static final EnumType[] META_LOOKUP = new EnumType[values().length];

        private final int meta;
        private final String name;

        private final int blockStorage;
        private final int itemStorage;

        private EnumType(int meta, String name, int blockStorage, int itemStorage) {
            this.meta = meta;
            this.name = name;
            this.blockStorage = blockStorage;
            this.itemStorage = itemStorage;
        }

        public int getMetadata() {
            return this.meta;
        }

        public int getBlockStorage() {
            return this.blockStorage;
        }

        public int getItemStorage() {
            return this.itemStorage;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public static EnumType fromMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) meta = 0;
            return META_LOOKUP[meta];
        }

        static {
            for (EnumType type : values()) {
                META_LOOKUP[type.getMetadata()] = type;
            }
        }
    }
}
