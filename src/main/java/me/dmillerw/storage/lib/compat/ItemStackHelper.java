package me.dmillerw.storage.lib.compat;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ItemStackHelper {

    public static boolean isEmpty(ItemStack itemStack) {
        return itemStack == null || itemStack.getItem() == null || itemStack.stackSize <= 0;
    }

    public static NBTTagCompound saveAllItems(NBTTagCompound tag, ItemStack[] list) {
        return saveAllItems(tag, list, true);
    }

    public static NBTTagCompound saveAllItems(NBTTagCompound tag, ItemStack[] list, boolean p_191281_2_) {
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < list.length; ++i) {
            ItemStack itemstack = (ItemStack) list[i];

            if (itemstack != null && itemstack.stackSize > 0) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }

        if (!nbttaglist.hasNoTags() || p_191281_2_) {
            tag.setTag("Items", nbttaglist);
        }

        return tag;
    }

    public static void loadAllItems(NBTTagCompound tag, ItemStack[] list) {
        NBTTagList nbttaglist = tag.getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            int j = nbttagcompound.getByte("Slot") & 255;

            if (j >= 0 && j < list.length) {
                list[j] = ItemStack.loadItemStackFromNBT(nbttagcompound);
            }
        }
    }
}