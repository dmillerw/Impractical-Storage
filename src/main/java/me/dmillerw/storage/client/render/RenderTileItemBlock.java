package me.dmillerw.storage.client.render;

import me.dmillerw.storage.block.tile.TileItemBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author dmillerw
 */
public class RenderTileItemBlock extends TileEntitySpecialRenderer<TileItemBlock> {

    @Override
    public void renderTileEntityAt(TileItemBlock te, double x, double y, double z, float partialTicks, int destroyStage) {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        boolean block = te.isBlock;

        if (te.tileRenderItem == null) {
            String renderValue = te.itemBlock;
            int renderValueMeta = te.itemBlockMeta;

            if (renderValue != null && !renderValue.isEmpty()) {
                Item item;
                if (te.itemBlock == null) item = Item.getItemFromBlock(Blocks.BARRIER);
                else item = Item.getByNameOrId(te.itemBlock);

                if (item == null) item = Item.getItemFromBlock(Blocks.BARRIER);

                te.tileRenderItem = new ItemStack(item, 1, renderValueMeta);
            }
        }

        if (block) block = renderItem.shouldRenderItemIn3D(te.tileRenderItem);

        te.isBlock = block;

        if (!block) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);

            for (int i=0; i<4; i++) {
                GlStateManager.pushMatrix();

                GlStateManager.translate(.5f, .5f, .5f);
                GlStateManager.rotate(90 * i, 0, 1, 0);
                GlStateManager.translate(-.5f, -.5f, -.5f);

                GlStateManager.translate(0, 1, 1);
                GlStateManager.scale(1 / 16f, -1 / 16f, 0.00001);

                GlStateManager.translate(4, 4, 0.);
                GlStateManager.scale(0.5, 0.5, 1);

                GlStateManager.pushMatrix();

                GlStateManager.scale(2.6f, 2.6f, 1);
                GlStateManager.rotate(171.6f, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(84.9f, 1.0F, 0.0F, 0.0F);

                RenderHelper.enableStandardItemLighting();
                GlStateManager.popMatrix();

                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset(-1, -1);

                GlStateManager.pushAttrib();
                GlStateManager.enableRescaleNormal();
                GlStateManager.popAttrib();

                renderItem.renderItemIntoGUI(te.tileRenderItem, 0, 0);

                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();

                GlStateManager.disablePolygonOffset();

                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
        }
    }
}
