package me.dmillerw.storage.proxy;

import me.dmillerw.storage.block.tile.TileItemBlock;
import me.dmillerw.storage.client.event.ControllerBoundsRenderer;
import me.dmillerw.storage.client.model.BaseModelLoader;
import me.dmillerw.storage.client.render.RenderTileItemBlock;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
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

        MinecraftForge.EVENT_BUS.register(ControllerBoundsRenderer.class);

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

    @Override
    public void readConfigurationFile(Configuration configuration) {
        super.readConfigurationFile(configuration);
    }
}
