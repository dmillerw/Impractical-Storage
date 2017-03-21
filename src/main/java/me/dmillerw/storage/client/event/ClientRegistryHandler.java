package me.dmillerw.storage.client.event;

import me.dmillerw.storage.block.ModBlocks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author dmillerw
 */
@Mod.EventBusSubscriber
public class ClientRegistryHandler {

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ItemBlock item = ModBlocks.controller_item;
        ModelResourceLocation resourceLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, resourceLocation);
    }
}
