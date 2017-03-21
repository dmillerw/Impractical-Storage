package me.dmillerw.storage.client.event;

import me.dmillerw.storage.item.ItemZoneCard;
import me.dmillerw.storage.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * @author dmillerw
 */
public class RenderTickHandler {

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player == null)
            return;

        ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);

        if (held.isEmpty() || held.getItem() != ModItems.zone_card)
            return;

        if (!ItemZoneCard.isComplete(held))
            return;

        BlockPos start = ItemZoneCard.getPositionOne(held);
        BlockPos end = ItemZoneCard.getPositionTwo(held);

        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)event.renderTickTime;
        double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)event.renderTickTime;
        double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)event.renderTickTime;

        AxisAlignedBB aabb = new AxisAlignedBB(start, end).expandXyz(0.0020000000949949026D).offset(-d0, -d1, -d2);

        RenderGlobal.drawSelectionBoundingBox(aabb, 0F, 0F, 0F, 0.4F);

        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
    }
}
