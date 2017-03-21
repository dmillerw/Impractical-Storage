package me.dmillerw.storage.item;

import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author dmillerw
 */
@GameRegistry.ObjectHolder(ModInfo.ID)
public class ModItems {

    public static final ItemZoneCard zone_card = null;

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    new ItemZoneCard().setRegistryName(ModInfo.ID, "zone_card")
            );
        }
    }
}