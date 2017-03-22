package me.dmillerw.storage.proxy;

import me.dmillerw.storage.block.ModBlocks;
import me.dmillerw.storage.block.tile.TileController;
import me.dmillerw.storage.block.tile.TileItemBlock;
import me.dmillerw.storage.item.ModItems;
import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author dmillerw
 */
public class CommonProxy implements IProxy {

    public static boolean organizedStorage = false;

    public static int blockUpdateBatch = -1;
    public static int blockUpdateRate = -1;

    public static boolean useBlockQueue() {
        return blockUpdateBatch != -1 && blockUpdateRate != -1;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerTileEntity(TileItemBlock.class, ModInfo.ID + ":item_block");
        GameRegistry.registerTileEntity(TileController.class, ModInfo.ID + ":controller");

        // Controller - Crafting Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.controller),
                "IRI",
                "ICI",
                "III",
                'I', "ingotIron",
                'R', Items.REDSTONE,
                'C', "chest"));

        // Zone Card - Crafting Recipe
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.zone_card),
                " I ",
                "RPR",
                " I ",
                'I', "ingotIron",
                'R', Items.REDSTONE,
                'P', Items.PAPER));

        // Zone Card - Erasing Recipe
        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.zone_card), ModItems.zone_card);
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void readConfigurationFile(Configuration configuration) {
        organizedStorage = configuration.getBoolean(
                "organizedStorage",
                Configuration.CATEGORY_GENERAL,
                false,
                "Whether Blocks/Items stored should be presented in neat rows, or in a more haphazard fashion");

        blockUpdateBatch = configuration.getInt(
                "blockUpdateBatchCount",
                Configuration.CATEGORY_GENERAL,
                -1,
                -1,
                Integer.MAX_VALUE,
                "How many blocks should be placed each time an update is triggered (based on blockUpdateRate). If set to -1, blocks will simply be set as they're added to the inventory");

        blockUpdateRate = configuration.getInt(
                "blockUpdateRate",
                Configuration.CATEGORY_GENERAL,
                -1,
                -1,
                Integer.MAX_VALUE,
                "How often (in ticks) should new blocks be placed. If set to -1, blocks will simply be set as they're added to the inventory");
    }
}
