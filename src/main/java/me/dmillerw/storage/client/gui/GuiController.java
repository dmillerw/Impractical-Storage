package me.dmillerw.storage.client.gui;

import me.dmillerw.storage.block.tile.TileController;
import me.dmillerw.storage.client.gui.widget.GuiButtonArrow;
import me.dmillerw.storage.lib.ModInfo;
import me.dmillerw.storage.lib.data.SortingType;
import me.dmillerw.storage.network.PacketHandler;
import me.dmillerw.storage.network.packet.PacketConfig;
import me.dmillerw.storage.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.io.IOException;

/**
 * @author dmillerw
 */
public class GuiController extends GuiScreen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/controller.png");

    private static final String TEXT_SHOW_BOUNDS = "Show Boundaries";
    private static final String TEXT_HIDE_BOUNDS = "Hide Boundaries";
    private static final String TEXT_SORTING_TYPE = "Sort Type: ";

    private static final int BUTTON_X_UP = 0;
    private static final int BUTTON_X_DOWN = 1;
    private static final int BUTTON_Y_UP = 2;
    private static final int BUTTON_Y_DOWN = 3;
    private static final int BUTTON_Z_UP = 4;
    private static final int BUTTON_Z_DOWN = 5;
    private static final int BUTTON_TOGGLE_BOUNDS = 6;
    private static final int BUTTON_SORTING_TYPE = 7;

    private static final int GUI_WIDTH = 116;
    private static final int GUI_HEIGHT = 166;

    private int guiLeft;
    private int guiTop;

    private int x;
    private int y;
    private int z;

    private boolean showBounds;
    private SortingType sortingType;

    private GuiButtonExt buttonShowBounds;
    private GuiButtonExt buttonSortType;

    private GuiTextField textX;
    private GuiTextField textY;
    private GuiTextField textZ;

    private TileController tile;
    public GuiController(TileController tile) {
        this.tile = tile;
        this.x = tile.rawX;
        this.y = tile.rawY;
        this.z = tile.rawZ;
        this.showBounds = tile.showBounds;
        this.sortingType = tile.sortingType;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;

        addButton(buttonShowBounds = new GuiButtonExt(BUTTON_TOGGLE_BOUNDS, guiLeft + 8, guiTop + 93, 101, 16,
                showBounds ? TEXT_HIDE_BOUNDS : TEXT_SHOW_BOUNDS));

        addButton(buttonSortType = new GuiButtonExt(BUTTON_SORTING_TYPE, guiLeft + 8, guiTop + 113, 101, 16,
                TEXT_SORTING_TYPE + sortingType.name()));

        addButton(new GuiButtonArrow(BUTTON_X_UP,   guiLeft + 8, guiTop + 21, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_X_DOWN, guiLeft + 8, guiTop + 60, 31, 15, GuiButtonArrow.ARROW_DOWN));

        addButton(new GuiButtonArrow(BUTTON_Y_UP, guiLeft + 43, guiTop + 21, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_Y_DOWN, guiLeft + 43, guiTop + 60, 31, 15, GuiButtonArrow.ARROW_DOWN));

        addButton(new GuiButtonArrow(BUTTON_Z_UP, guiLeft + 78, guiTop + 21, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_Z_DOWN, guiLeft + 78, guiTop + 60, 31, 15, GuiButtonArrow.ARROW_DOWN));

        textX = new GuiTextField(0, fontRendererObj, guiLeft + 9,   guiTop + 40, 29, 15);
        textX.setText(Integer.toString(x));
        textY = new GuiTextField(1, fontRendererObj, guiLeft + 44,  guiTop + 40, 29, 15);
        textY.setText(Integer.toString(y));
        textZ = new GuiTextField(2, fontRendererObj, guiLeft + 79,  guiTop + 40, 29, 15);
        textZ.setText(Integer.toString(z));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (textX.textboxKeyTyped(typedChar, keyCode)) return;
        if (textY.textboxKeyTyped(typedChar, keyCode)) return;
        if (textZ.textboxKeyTyped(typedChar, keyCode)) return;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        textX.mouseClicked(mouseX, mouseY, mouseButton);
        textY.mouseClicked(mouseX, mouseY, mouseButton);
        textZ.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int ox = x;
        int oy = y;
        int oz = z;
        boolean oshowBounds = showBounds;
        SortingType osortingType= sortingType;

        switch (button.id) {
            case BUTTON_X_UP:   x += 1; break;
            case BUTTON_X_DOWN: x -= 1; break;
            case BUTTON_Y_UP:   y += 1; break;
            case BUTTON_Y_DOWN: y -= 1; break;
            case BUTTON_Z_UP:   z += 1; break;
            case BUTTON_Z_DOWN: z -= 1; break;

            case BUTTON_TOGGLE_BOUNDS: {
                showBounds = !showBounds;
                buttonShowBounds.displayString = showBounds ? TEXT_HIDE_BOUNDS : TEXT_SHOW_BOUNDS;
                break;
            }

            case BUTTON_SORTING_TYPE: {
                int ord = sortingType.ordinal();
                if (ord + 1 >= SortingType.VALUES.length) {
                    sortingType = SortingType.ROWS;
                } else {
                    sortingType = SortingType.VALUES[ord + 1];
                }

                buttonSortType.displayString = TEXT_SORTING_TYPE + sortingType.name();
            }

            default: break;
        }

        if (x <= 0) x = 1; else if (x >= CommonProxy.maxX) x = CommonProxy.maxX - 1;
        if (y <= 0) y = 1; else if (y >= CommonProxy.maxY) y = CommonProxy.maxY - 1;
        if (z <= 0) z = 1; else if (z >= CommonProxy.maxZ) z = CommonProxy.maxZ - 1;

        textX.setText(Integer.toString(x));
        textY.setText(Integer.toString(y));
        textZ.setText(Integer.toString(z));

        PacketConfig packet = new PacketConfig(tile.getPos());

        if (ox != x || oy != y || oz != z)
            packet.setBoundaryDimensions(x, y, z);

        if (oshowBounds != showBounds)
            packet.setShowBounds(showBounds);

        if (osortingType != sortingType)
            packet.setSortingType(sortingType);

        PacketHandler.INSTANCE.sendToServer(packet);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(GUI_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        fontRendererObj.drawString("Boundaries", guiLeft + 8, guiTop + 10, 4210752, false);
        fontRendererObj.drawString("Other", guiLeft + 8, guiTop + 82, 4210752, false);

        super.drawScreen(mouseX, mouseY, partialTicks);

        textX.drawTextBox();
        textY.drawTextBox();
        textZ.drawTextBox();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
