package me.dmillerw.storage.client.gui;

import com.google.common.base.Predicate;
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
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author dmillerw
 */
public class GuiController extends GuiScreen {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/controller.png");

    private static final Predicate<String> NUMBER_VALIDATOR = s -> {
        for (char c : s.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    };

    private static final String TEXT_SHOW_BOUNDS = "gui.text.bounds.show";
    private static final String TEXT_HIDE_BOUNDS = "gui.text.bounds.hide";
    private static final String TEXT_SORTING_TYPE = "gui.text.sort_type";

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
                I18n.translateToLocal(showBounds ? TEXT_HIDE_BOUNDS : TEXT_SHOW_BOUNDS)));

        addButton(buttonSortType = new GuiButtonExt(BUTTON_SORTING_TYPE, guiLeft + 8, guiTop + 113, 101, 16,
                I18n.translateToLocal(TEXT_SORTING_TYPE + sortingType.getUnlocalizedName())));

        addButton(new GuiButtonArrow(BUTTON_X_UP,   guiLeft + 8, guiTop + 21, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_X_DOWN, guiLeft + 8, guiTop + 60, 31, 15, GuiButtonArrow.ARROW_DOWN));

        addButton(new GuiButtonArrow(BUTTON_Y_UP, guiLeft + 43, guiTop + 21, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_Y_DOWN, guiLeft + 43, guiTop + 60, 31, 15, GuiButtonArrow.ARROW_DOWN));

        addButton(new GuiButtonArrow(BUTTON_Z_UP, guiLeft + 78, guiTop + 21, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_Z_DOWN, guiLeft + 78, guiTop + 60, 31, 15, GuiButtonArrow.ARROW_DOWN));

        textX = new GuiTextField(0, fontRendererObj, guiLeft + 9,   guiTop + 40, 29, 15);
        textX.setText(Integer.toString(x));
        textX.setValidator(NUMBER_VALIDATOR);

        textY = new GuiTextField(1, fontRendererObj, guiLeft + 44,  guiTop + 40, 29, 15);
        textY.setText(Integer.toString(y));
        textY.setValidator(NUMBER_VALIDATOR);

        textZ = new GuiTextField(2, fontRendererObj, guiLeft + 79,  guiTop + 40, 29, 15);
        textZ.setText(Integer.toString(z));
        textZ.setValidator(NUMBER_VALIDATOR);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN) {
            if (textX.isFocused() || textY.isFocused() || textZ.isFocused()) {
                String sx = textX.getText();
                String sy = textY.getText();
                String sz = textZ.getText();

                int nx = sx.isEmpty() ? x : Integer.parseInt(sx);
                int ny = sy.isEmpty() ? y : Integer.parseInt(sy);
                int nz = sz.isEmpty() ? z : Integer.parseInt(sz);

                textX.setFocused(false);
                textY.setFocused(false);
                textZ.setFocused(false);

                update(nx, ny, nz, showBounds, sortingType);

                return;
            }
        }

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
        int nx = x;
        int ny = y;
        int nz = z;
        boolean nshowBounds = showBounds;
        SortingType nsortingType = sortingType;

        switch (button.id) {
            case BUTTON_X_UP:   nx += 1; break;
            case BUTTON_X_DOWN: nx -= 1; break;
            case BUTTON_Y_UP:   ny += 1; break;
            case BUTTON_Y_DOWN: ny -= 1; break;
            case BUTTON_Z_UP:   nz += 1; break;
            case BUTTON_Z_DOWN: nz -= 1; break;

            case BUTTON_TOGGLE_BOUNDS: {
                nshowBounds = !nshowBounds;
                buttonShowBounds.displayString = I18n.translateToLocal(nshowBounds ? TEXT_HIDE_BOUNDS : TEXT_SHOW_BOUNDS);
                break;
            }

            case BUTTON_SORTING_TYPE: {
                int ord = nsortingType.ordinal();
                if (ord + 1 >= SortingType.VALUES.length) {
                    nsortingType = SortingType.ROWS;
                } else {
                    nsortingType = SortingType.VALUES[ord + 1];
                }

                buttonSortType.displayString = I18n.translateToLocal(TEXT_SORTING_TYPE + nsortingType.name());
            }

            default: break;
        }

        update(nx, ny, nz, nshowBounds, nsortingType);
    }

    private void update(int nx, int ny, int nz, boolean nshowBounds, SortingType nsortingType) {
        int ox = x;
        int oy = y;
        int oz = z;
        boolean oshowBounds = showBounds;
        SortingType osortingType= sortingType;

        if (nx <= 0) nx = 1; else if (nx >= CommonProxy.maxX) nx = CommonProxy.maxX - 1;
        if (ny <= 0) ny = 1; else if (ny >= CommonProxy.maxY) ny = CommonProxy.maxY - 1;
        if (nz <= 0) nz = 1; else if (nz >= CommonProxy.maxZ) nz = CommonProxy.maxZ - 1;

        textX.setText(Integer.toString(nx));
        textY.setText(Integer.toString(ny));
        textZ.setText(Integer.toString(nz));

        PacketConfig packet = new PacketConfig(tile.getPos());

        if (ox != nx || oy != ny || oz != nz)
            packet.setBoundaryDimensions(nx, ny, nz);

        if (oshowBounds != nshowBounds)
            packet.setShowBounds(nshowBounds);

        if (osortingType != nsortingType)
            packet.setSortingType(nsortingType);

        PacketHandler.INSTANCE.sendToServer(packet);

        x = nx;
        y = ny;
        z = nz;
        showBounds = nshowBounds;
        sortingType = nsortingType;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(GUI_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        fontRendererObj.drawString(I18n.translateToLocal("gui.label.bounds"), guiLeft + 8, guiTop + 10, 4210752, false);
        fontRendererObj.drawString(I18n.translateToLocal("gui.label.other"), guiLeft + 8, guiTop + 82, 4210752, false);

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