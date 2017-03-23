package me.dmillerw.storage.proxy;

import me.dmillerw.storage.block.ModBlocks;
import me.dmillerw.storage.block.tile.TileController;
import me.dmillerw.storage.block.tile.TileItemBlock;
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

    public static final String CATEGORY_BOUNDS = "bounds";
    public static final String CATEGORY_BLOCK_QUEUE = "block_queue";
    public static final String CATEGORY_RATES = "rates";

    public static int defaultMinX = 4;
    public static int defaultMinY = 1;
    public static int defaultMinZ = 1;

    public static int defaultMaxX = 4;
    public static int defaultMaxY = 8;
    public static int defaultMaxZ = 8;

    public static int maxX = 64;
    public static int maxY = 64;
    public static int maxZ = 64;

    public static boolean showBoundsOnSneak = true;
    public static boolean organizedStorage = false;

    public static int blockUpdateBatch = -1;
    public static int blockUpdateRate = -1;

    public static int zoneUpdateRate = 1;

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
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public void readConfigurationFile(Configuration configuration) {
        configuration.addCustomCategoryComment(CATEGORY_BOUNDS, "Control for the default and max bounds of the controller area");
        configuration.addCustomCategoryComment(CATEGORY_BLOCK_QUEUE, "Block queue will batch and delay the placing of new blocks in the world");
        configuration.addCustomCategoryComment(CATEGORY_RATES, "Various tick rates");

        maxX = configuration.getInt(
                "maxX",
                CATEGORY_BOUNDS,
                maxX,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                "Total max size on the X axis a Controller zone can take up"
        );

        maxY = configuration.getInt(
                "maxY",
                CATEGORY_BOUNDS,
                maxY,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                "Total max size on the Y axis a Controller zone can take up"
        );

        maxZ = configuration.getInt(
                "maxZ",
                CATEGORY_BOUNDS,
                maxZ,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                "Total max size on the Y axis a Controller zone can take up"
        );

        defaultMinX = getBound(configuration, "defaultMinX", 4, 1, maxX);
        defaultMinY = getBound(configuration, "defaultMinY", 1, 1, maxX);
        defaultMinZ = getBound(configuration, "defaultMinZ", 1, 1, maxX);
        defaultMaxX = getBound(configuration, "defaultMaxX", 4, 1, maxX);
        defaultMaxY = getBound(configuration, "defaultMaxY", 8, 1, maxX);
        defaultMaxZ = getBound(configuration, "defaultMaxZ", 8, 1, maxX);

        organizedStorage = configuration.getBoolean(
                "organizedStorage",
                Configuration.CATEGORY_GENERAL,
                false,
                "Whether Blocks/Items stored should be presented in neat rows, or in a more haphazard fashion");

        showBoundsOnSneak = configuration.getBoolean(
                "showBoundsOnSneak",
                Configuration.CATEGORY_GENERAL,
                true,
                "Whether to show Controller bounds normally when highlighted, or only when sneaking"
        );

        blockUpdateBatch = configuration.getInt(
                "blockUpdateBatchCount",
                CATEGORY_BLOCK_QUEUE,
                -1,
                -1,
                Integer.MAX_VALUE,
                "How many blocks should be placed each time an update is triggered (based on blockUpdateRate). If set to -1, blocks will simply be set as they're added to the inventory");

        blockUpdateRate = configuration.getInt(
                "blockUpdateRate",
                CATEGORY_BLOCK_QUEUE,
                -1,
                -1,
                Integer.MAX_VALUE,
                "How often (in ticks) should new blocks be placed. If set to -1, blocks will simply be set as they're added to the inventory");

        zoneUpdateRate = configuration.getInt(
                "zoneUpdateRate",
                CATEGORY_RATES,
                1,
                1,
                Integer.MAX_VALUE,
                "How often (in ticks) should new Blocks placed in a Controller's area (by player, machine, etc) be added to the inventory"
        );
    }

    private static int getBound(Configuration configuration, String key, int defaultValue, int min, int max) {
        return clamp(configuration.getInt(
                key,
                CATEGORY_BOUNDS,
                defaultValue,
                min,
                max,
                ""
        ), min, max);
    }

    private static int clamp(int value, int min, int max) {
        if (value < min) value = min;
        if (value > max) value = max;
        return value;
    }
}