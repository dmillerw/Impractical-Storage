package me.dmillerw.storage.item;

import me.dmillerw.storage.block.ModBlocks;
import me.dmillerw.storage.block.tile.TileController;
import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import java.util.List;

/**
 * @author dmillerw
 */
public class ItemZoneCard extends Item {

    private static final String TAG_POS_1 = "pos1";
    private static final String TAG_POS_2 = "pos2";

    public static boolean isEmpty(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        return !tag.hasKey(TAG_POS_1) && !tag.hasKey(TAG_POS_2);
    }

    public static boolean isInProgress(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        return tag.hasKey(TAG_POS_1) && !tag.hasKey(TAG_POS_2);
    }

    public static boolean isComplete(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        return tag.hasKey(TAG_POS_1) && tag.hasKey(TAG_POS_2);
    }

    public static BlockPos getPositionOne(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        return BlockPos.fromLong(tag.getLong(TAG_POS_1));
    }

    public static void setPositionOne(ItemStack itemStack, BlockPos pos) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        tag.setLong(TAG_POS_1, pos.toLong());
        itemStack.setTagCompound(tag);
    }

    public static BlockPos getPositionTwo(ItemStack itemStack) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        return BlockPos.fromLong(tag.getLong(TAG_POS_2));
    }

    public static void setPositionTwo(ItemStack itemStack, BlockPos pos) {
        NBTTagCompound tag = itemStack.getTagCompound();
        if (tag == null) tag = new NBTTagCompound();
        tag.setLong(TAG_POS_2, pos.toLong());
        itemStack.setTagCompound(tag);
    }

    public ItemZoneCard() {
        super();

        setCreativeTab(CreativeTabs.MISC);

        setMaxStackSize(1);

        setUnlocalizedName(ModInfo.ID + ":zone_card");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        BlockPos pos1 = getPositionOne(stack);
        BlockPos pos2 = getPositionTwo(stack);

        tooltip.add("POSITION 1: " + (pos1.equals(BlockPos.ORIGIN) ? "NOT SET" : pos1));
        tooltip.add("POSITION 2: " + (pos2.equals(BlockPos.ORIGIN) ? "NOT SET" : pos2));
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!world.isRemote) {
            ItemStack itemStack = player.getHeldItem(hand);
            if (player.isSneaking()) {
                if (isEmpty(itemStack)) {
                    pos = pos.offset(side);

                    setPositionOne(itemStack, pos);

                    player.sendMessage(new TextComponentString("Set position 1 to " + pos));

                    return EnumActionResult.SUCCESS;
                } else if (isInProgress(itemStack)) {
                    BlockPos pos1 = getPositionOne(itemStack);
                    BlockPos pos2 = pos.offset(side);

                    player.sendMessage(new TextComponentString("Set position 2 to " + pos));

                    BlockPos npos1 = new BlockPos(
                            Math.min(pos1.getX(), pos2.getX()),
                            Math.min(pos1.getY(), pos2.getY()),
                            Math.min(pos1.getZ(), pos2.getZ())
                    );

                    BlockPos npos2 = new BlockPos(
                            Math.max(pos1.getX(), pos2.getX()),
                            Math.max(pos1.getY(), pos2.getY()),
                            Math.max(pos1.getZ(), pos2.getZ())
                    );

                    setPositionOne(itemStack, npos1);
                    setPositionTwo(itemStack, npos2);

                    return EnumActionResult.SUCCESS;
                } else {
                    return EnumActionResult.FAIL;
                }
            } else {
                if (isEmpty(itemStack) || isInProgress(itemStack))
                    return EnumActionResult.FAIL;

                IBlockState state = world.getBlockState(pos);
                if (state.getBlock() == ModBlocks.controller) {
                    TileEntity tile = world.getTileEntity(pos);
                    if (tile != null) {
                        ((TileController)tile).updateBounds(getPositionOne(itemStack), getPositionTwo(itemStack), true);

                        player.sendMessage(new TextComponentString("Updated Controller bounds"));

                        return EnumActionResult.SUCCESS;
                    }
                    return EnumActionResult.FAIL;
                } else {
                    return EnumActionResult.FAIL;
                }
            }
        } else {
            return EnumActionResult.PASS;
        }
    }
}
