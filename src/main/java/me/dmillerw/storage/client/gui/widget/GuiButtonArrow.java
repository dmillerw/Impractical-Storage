package me.dmillerw.storage.client.gui.widget;

import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;

/**
 * @author dmillerw
 */
public class GuiButtonArrow extends GuiButtonExt {

    private static final ResourceLocation BUTTON_ARROWS = new ResourceLocation(ModInfo.ID, "textures/gui/arrows.png");

    public static final int ARROW_DOWN = 0;
    public static final int ARROW_UP = 1;
    public static final int ARROW_LEFT = 2;
    public static final int ARROW_RIGHT = 3;

    private static final int ARROW_WIDTH = 11;
    private static final int ARROW_HEIGHT = 7;

    private int arrowType;

    public GuiButtonArrow(int id, int xPos, int yPos, int arrowType) {
        super(id, xPos, yPos, "");

        this.arrowType = arrowType;
    }

    public GuiButtonArrow(int id, int xPos, int yPos, int width, int height, int arrowType) {
        super(id, xPos, yPos, width, height, "");

        this.arrowType = arrowType;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int k = this.getHoverState(this.hovered);
            GuiUtils.drawContinuousTexturedBox(BUTTON_TEXTURES, this.xPosition, this.yPosition, 0, 46 + k * 20, this.width, this.height, 200, 20, 2, 3, 2, 2, this.zLevel);
            this.mouseDragged(mc, mouseX, mouseY);
            int color = 14737632;

            if (packedFGColour != 0) {
                color = packedFGColour;
            } else if (!this.enabled) {
                color = 10526880;
            } else if (this.hovered) {
                color = 16777120;
            }

            Minecraft.getMinecraft().renderEngine.bindTexture(BUTTON_ARROWS);

            double centerX = this.width / 2D;
            double centerY = this.height / 2D;

            double halfW = ARROW_WIDTH / 2D;
            double halfH = ARROW_HEIGHT / 2D;

            double drawX;
            double drawY;
            double drawW;
            double drawH;

            int drawU;
            int drawV;

            switch (arrowType) {
                case ARROW_UP: {
                    drawX = centerX - halfW;
                    drawY = centerY - halfH;
                    drawW = ARROW_WIDTH;
                    drawH = ARROW_HEIGHT;
                    drawU = 22 + (hovered ? 11 : 0);
                    drawV = 0;
                    break;
                }

                case ARROW_DOWN: {
                    drawX = centerX - halfW;
                    drawY = centerY - halfH;
                    drawW = ARROW_WIDTH;
                    drawH = ARROW_HEIGHT;
                    drawU = (hovered ? 11 : 0);
                    drawV = 0;
                    break;
                }

                case ARROW_LEFT: {
                    drawX = centerX - halfH;
                    drawY = centerY - halfW;
                    drawW = ARROW_HEIGHT;
                    drawH = ARROW_WIDTH;
                    drawU = 0;
                    drawV = (hovered ? 7 : 0);
                    break;
                }

                case ARROW_RIGHT:
                default: {
                    drawX = centerX - halfH;
                    drawY = centerY - halfW;
                    drawW = ARROW_HEIGHT;
                    drawH = ARROW_WIDTH;
                    drawU = 0;
                    drawV = 14 + (hovered ? 7 : 0);
                    break;
                }
            }

            drawTexturedModalRect(this.xPosition + drawX, this.yPosition + drawY, drawU, drawV, drawW, drawH, this.zLevel);
        }
    }

    public static void drawTexturedModalRect(double x, double y, int u, int v, double width, double height, float zLevel) {
        float uScale = 1f / 0x100;
        float vScale = 1f / 0x100;
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer wr = tessellator.getBuffer();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX);
        wr.pos(x, y + height, zLevel).tex(u * uScale, ((v + height) * vScale)).endVertex();
        wr.pos(x + width, y + height, zLevel).tex((u + width) * uScale, ((v + height) * vScale)).endVertex();
        wr.pos(x + width, y, zLevel).tex((u + width) * uScale, (v * vScale)).endVertex();
        wr.pos(x, y, zLevel).tex(u * uScale, (v * vScale)).endVertex();
        tessellator.draw();
    }
}