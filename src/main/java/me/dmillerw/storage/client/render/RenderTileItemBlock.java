package me.dmillerw.storage.client.render;

import me.dmillerw.storage.block.BlockItemBlock;
import me.dmillerw.storage.block.tile.TileItemBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IExtendedBlockState;

/**
 * @author dmillerw
 */
public class RenderTileItemBlock extends TileEntitySpecialRenderer<TileItemBlock> {

    @Override
    public void renderTileEntityAt(TileItemBlock te, double x, double y, double z, float partialTicks, int destroyStage) {
        boolean block = te.isBlock;

        if (block) {
            IExtendedBlockState extendedBlockState = te.getExtendedBlockState(te.getWorld().getBlockState(te.getPos()));

            String renderValue = extendedBlockState.getValue(BlockItemBlock.RENDER_VALUE);
            int renderValueMeta = extendedBlockState.getValue(BlockItemBlock.RENDER_VALUE_META).intValue();

            if (renderValue != null && !renderValue.isEmpty()) {
                final ItemStack itemStack = new ItemStack(Item.getByNameOrId(renderValue), 1, renderValueMeta);
                IBakedModel inventoryModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(itemStack);
                if (!inventoryModel.isGui3d()) {
                    block = false;
                }
            }
        }

        te.isBlock = block;

        if (!block) {
            if (te.tileRenderItem == null) {
                Item item;
                if (te.itemBlock == null) item = Item.getItemFromBlock(Blocks.BARRIER);
                else item = Item.getByNameOrId(te.itemBlock);

                if (item == null) item = Item.getItemFromBlock(Blocks.BARRIER);

                ItemStack itemStack = new ItemStack(item, 1, te.itemBlockMeta);

                te.tileRenderItem = new EntityItem(te.getWorld(), 0, 0, 0, itemStack);
                te.tileRenderItem.hoverStart = 0;
            }

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

                Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(te.tileRenderItem.getEntityItem(), 0, 0);

                GlStateManager.disableBlend();
                GlStateManager.enableAlpha();

                GlStateManager.disablePolygonOffset();

                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
        }
    }
}
