package me.dmillerw.storage.proxy;

import me.dmillerw.storage.block.tile.TileItemBlock;
import me.dmillerw.storage.client.event.RenderTickHandler;
import me.dmillerw.storage.client.model.BaseModelLoader;
import me.dmillerw.storage.client.render.RenderTileItemBlock;
import me.dmillerw.storage.item.ModItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author dmillerw
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        ModelLoaderRegistry.registerLoader(new BaseModelLoader());

        Item item = ModItems.zone_card;
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation((item).getRegistryName().toString()));

        MinecraftForge.EVENT_BUS.register(new RenderTickHandler());

        ClientRegistry.bindTileEntitySpecialRenderer(TileItemBlock.class, new RenderTileItemBlock());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
