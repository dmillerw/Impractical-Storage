package me.dmillerw.storage.core.handler;

import me.dmillerw.storage.ImpracticalStorage;
import me.dmillerw.storage.block.tile.TileController;
import me.dmillerw.storage.client.gui.GuiController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

/**
 * @author dmillerw
 */
public class GuiHandler implements IGuiHandler {

    public static void register() {
        NetworkRegistry.INSTANCE.registerGuiHandler(ImpracticalStorage.instance, new GuiHandler());
    }

    public static enum Gui {

        CONTROLLER;

        public void openGui(EntityPlayer player) {
            player.openGui(ImpracticalStorage.instance, this.ordinal(), player.world, 0, 0, 0);
        }

        public void openGui(EntityPlayer player, BlockPos pos) {
            player.openGui(ImpracticalStorage.instance, this.ordinal(), player.world, pos.getX(), pos.getY(), pos.getZ());
        }

        public static final Gui[] VALUES = values();
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (Gui.VALUES[id]) {
            case CONTROLLER:
            default: return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        switch (Gui.VALUES[id]) {
            case CONTROLLER: return new GuiController((TileController) world.getTileEntity(new BlockPos(x, y, z)));
            default: return null;
        }
    }
}
