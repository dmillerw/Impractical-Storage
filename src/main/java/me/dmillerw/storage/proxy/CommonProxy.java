package me.dmillerw.storage.proxy;

import me.dmillerw.storage.block.ModBlocks;
import me.dmillerw.storage.block.tile.TileController;
import me.dmillerw.storage.block.tile.TileItemBlock;
import me.dmillerw.storage.item.ModItems;
import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * @author dmillerw
 */
public class CommonProxy implements IProxy {

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
}
