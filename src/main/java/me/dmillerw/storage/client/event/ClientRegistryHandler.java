package me.dmillerw.storage.client.event;

import me.dmillerw.storage.block.BlockCrate;
import me.dmillerw.storage.block.ModBlocks;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;
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
        registerItemModel(ModBlocks.controller_item);
        registerItemModel(ModBlocks.controller_interface_item);
        registerItemModel(ModBlocks.phantom_item);
        registerItemModel(ModBlocks.conveyor_item);
        registerItemModel(ModBlocks.gravity_inducer_item);
        registerItemModel(ModBlocks.itemizer_item);

        for (BlockCrate.EnumType type : BlockCrate.EnumType.values()) {
            registerItemModel(ModBlocks.crate_item, "variant", type);
        }
    }

    private static void registerItemModel(Item item) {
        ModelResourceLocation resourceLocation = new ModelResourceLocation(item.getRegistryName(), "inventory");
        ModelLoader.setCustomModelResourceLocation(item, 0, resourceLocation);
    }

    private static void registerItemModel(Item item, String tag, Enum<? extends IStringSerializable> variant) {
        ModelResourceLocation resourceLocation = new ModelResourceLocation(item.getRegistryName(), tag + "=" + ((IStringSerializable)variant).getName());
        ModelLoader.setCustomModelResourceLocation(item, variant.ordinal(), resourceLocation);
    }
}
