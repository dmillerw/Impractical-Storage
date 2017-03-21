package me.dmillerw.storage.block;

import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author dmillerw
 */
@GameRegistry.ObjectHolder(ModInfo.ID)
public class ModBlocks {

    public static final BlockItemBlock item_block = null;
    @GameRegistry.ObjectHolder(ModInfo.ID + ":item_block")
    public static final ItemBlock item_block_item = null;

    public static final BlockController controller = null;
    @GameRegistry.ObjectHolder(ModInfo.ID + ":controller")
    public static final ItemBlock controller_item = null;

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(
                    new BlockItemBlock().setRegistryName(ModInfo.ID, "item_block"),
                    new BlockController().setRegistryName(ModInfo.ID, "controller")
            );
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    new ItemBlock(item_block).setRegistryName(ModInfo.ID, "item_block"),
                    new ItemBlock(controller).setRegistryName(ModInfo.ID, "controller")
            );
        }
    }
}
