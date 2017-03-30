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
            if (!Character.isDigit(c) && c != '-') {
                return false;
            }
        }
        return true;
    };

    private static final String TEXT_SHOW_BOUNDS = "gui.text.bounds.show";
    private static final String TEXT_HIDE_BOUNDS = "gui.text.bounds.hide";
    private static final String TEXT_SORTING_TYPE = "gui.text.sort_type.";

    private static final int BUTTON_BOUND_X_UP = 0;
    private static final int BUTTON_BOUND_X_DOWN = 1;
    private static final int BUTTON_BOUND_Y_UP = 2;
    private static final int BUTTON_BOUND_Y_DOWN = 3;
    private static final int BUTTON_BOUND_Z_UP = 4;
    private static final int BUTTON_BOUND_Z_DOWN = 5;

    private static final int BUTTON_OFFSET_X_UP = 6;
    private static final int BUTTON_OFFSET_X_DOWN = 7;
    private static final int BUTTON_OFFSET_Y_UP = 8;
    private static final int BUTTON_OFFSET_Y_DOWN = 9;
    private static final int BUTTON_OFFSET_Z_UP = 10;
    private static final int BUTTON_OFFSET_Z_DOWN = 11;

    private static final int BUTTON_TOGGLE_BOUNDS = 12;
    private static final int BUTTON_SORTING_TYPE = 13;

    private static final int GUI_WIDTH = 117;
    private static final int GUI_HEIGHT = 209;

    private int guiLeft;
    private int guiTop;

    private int x;
    private int y;
    private int z;

    private int offX;
    private int offY;
    private int offZ;

    private boolean showBounds;
    private SortingType sortingType;

    private GuiButtonExt buttonShowBounds;
    private GuiButtonExt buttonSortType;

    private GuiTextField boundX;
    private GuiTextField boundY;
    private GuiTextField boundZ;

    private GuiTextField offsetX;
    private GuiTextField offsetY;
    private GuiTextField offsetZ;

    private TileController tile;

    public GuiController(TileController tile) {
        this.tile = tile;

        this.x = tile.rawX;
        this.y = tile.rawY;
        this.z = tile.rawZ;
        this.offX = tile.offset.getX();
        this.offY = tile.offset.getY();
        this.offZ = tile.offset.getZ();

        this.showBounds = tile.showBounds;
        this.sortingType = tile.sortingType;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - GUI_WIDTH) / 2;
        this.guiTop = (this.height - GUI_HEIGHT) / 2;

        addButton(buttonShowBounds = new GuiButtonExt(BUTTON_TOGGLE_BOUNDS, guiLeft + 8, guiTop + 164, 101, 16,
                I18n.translateToLocal(showBounds ? TEXT_HIDE_BOUNDS : TEXT_SHOW_BOUNDS)));

        addButton(buttonSortType = new GuiButtonExt(BUTTON_SORTING_TYPE, guiLeft + 8, guiTop + 184, 101, 16,
                I18n.translateToLocal(TEXT_SORTING_TYPE + sortingType.getUnlocalizedName())));

        addButton(new GuiButtonArrow(BUTTON_BOUND_X_UP, guiLeft + 8, guiTop + 21, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_BOUND_X_DOWN, guiLeft + 8, guiTop + 60, 31, 15, GuiButtonArrow.ARROW_DOWN));
        addButton(new GuiButtonArrow(BUTTON_BOUND_Y_UP, guiLeft + 43, guiTop + 21, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_BOUND_Y_DOWN, guiLeft + 43, guiTop + 60, 31, 15, GuiButtonArrow.ARROW_DOWN));
        addButton(new GuiButtonArrow(BUTTON_BOUND_Z_UP, guiLeft + 78, guiTop + 21, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_BOUND_Z_DOWN, guiLeft + 78, guiTop + 60, 31, 15, GuiButtonArrow.ARROW_DOWN));
        addButton(new GuiButtonArrow(BUTTON_OFFSET_X_UP, guiLeft + 8, guiTop + 92, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_OFFSET_X_DOWN, guiLeft + 8, guiTop + 131, 31, 15, GuiButtonArrow.ARROW_DOWN));
        addButton(new GuiButtonArrow(BUTTON_OFFSET_Y_UP, guiLeft + 43, guiTop + 92, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_OFFSET_Y_DOWN, guiLeft + 43, guiTop + 131, 31, 15, GuiButtonArrow.ARROW_DOWN));
        addButton(new GuiButtonArrow(BUTTON_OFFSET_Z_UP, guiLeft + 78, guiTop + 92, 31, 15, GuiButtonArrow.ARROW_UP));
        addButton(new GuiButtonArrow(BUTTON_OFFSET_Z_DOWN, guiLeft + 78, guiTop + 131, 31, 15, GuiButtonArrow.ARROW_DOWN));

        boundX = new GuiTextField(0, fontRendererObj, guiLeft + 9, guiTop + 40, 29, 15);
        boundX.setText(Integer.toString(x));
        boundX.setValidator(NUMBER_VALIDATOR);

        boundY = new GuiTextField(1, fontRendererObj, guiLeft + 44, guiTop + 40, 29, 15);
        boundY.setText(Integer.toString(y));
        boundY.setValidator(NUMBER_VALIDATOR);

        boundZ = new GuiTextField(2, fontRendererObj, guiLeft + 79, guiTop + 40, 29, 15);
        boundZ.setText(Integer.toString(z));
        boundZ.setValidator(NUMBER_VALIDATOR);

        offsetX = new GuiTextField(3, fontRendererObj, guiLeft + 9, guiTop + 111, 29, 15);
        offsetX.setText(Integer.toString(offX));
        offsetX.setValidator(NUMBER_VALIDATOR);

        offsetY = new GuiTextField(4, fontRendererObj, guiLeft + 44, guiTop + 111, 29, 15);
        offsetY.setText(Integer.toString(offY));
        offsetY.setValidator(NUMBER_VALIDATOR);

        offsetZ = new GuiTextField(5, fontRendererObj, guiLeft + 79, guiTop + 111, 29, 15);
        offsetZ.setText(Integer.toString(offZ));
        offsetZ.setValidator(NUMBER_VALIDATOR);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN) {
            if (boundX.isFocused() || boundY.isFocused() || boundZ.isFocused() || offsetX.isFocused() || offsetY.isFocused() || offsetZ.isFocused()) {
                String sx = boundX.getText();
                String sy = boundY.getText();
                String sz = boundZ.getText();
                String sox = offsetX.getText();
                String soy = offsetY.getText();
                String soz = offsetZ.getText();

                int nx = sx.isEmpty() ? x : Integer.parseInt(sx);
                int ny = sy.isEmpty() ? y : Integer.parseInt(sy);
                int nz = sz.isEmpty() ? z : Integer.parseInt(sz);

                int ox = sox.isEmpty() ? offX : Integer.parseInt(sox);
                int oy = soy.isEmpty() ? offY : Integer.parseInt(soy);
                int oz = soz.isEmpty() ? offZ : Integer.parseInt(soz);

                boundX.setFocused(false);
                boundY.setFocused(false);
                boundZ.setFocused(false);
                offsetX.setFocused(false);
                offsetY.setFocused(false);
                offsetZ.setFocused(false);

                update(nx, ny, nz, ox, oy, oz, showBounds, sortingType);

                return;
            }
        }

        if (boundX.textboxKeyTyped(typedChar, keyCode)) return;
        if (boundY.textboxKeyTyped(typedChar, keyCode)) return;
        if (boundZ.textboxKeyTyped(typedChar, keyCode)) return;
        if (offsetX.textboxKeyTyped(typedChar, keyCode)) return;
        if (offsetY.textboxKeyTyped(typedChar, keyCode)) return;
        if (offsetZ.textboxKeyTyped(typedChar, keyCode)) return;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        boundX.mouseClicked(mouseX, mouseY, mouseButton);
        boundY.mouseClicked(mouseX, mouseY, mouseButton);
        boundZ.mouseClicked(mouseX, mouseY, mouseButton);
        offsetX.mouseClicked(mouseX, mouseY, mouseButton);
        offsetY.mouseClicked(mouseX, mouseY, mouseButton);
        offsetZ.mouseClicked(mouseX, mouseY, mouseButton);

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int nx = x;
        int ny = y;
        int nz = z;
        int ox = offX;
        int oy = offY;
        int oz = offZ;

        boolean nshowBounds = showBounds;
        SortingType nsortingType = sortingType;

        switch (button.id) {
            case BUTTON_BOUND_X_UP:
                nx += 1;
                break;
            case BUTTON_BOUND_X_DOWN:
                nx -= 1;
                break;
            case BUTTON_BOUND_Y_UP:
                ny += 1;
                break;
            case BUTTON_BOUND_Y_DOWN:
                ny -= 1;
                break;
            case BUTTON_BOUND_Z_UP:
                nz += 1;
                break;
            case BUTTON_BOUND_Z_DOWN:
                nz -= 1;
                break;
            case BUTTON_OFFSET_X_UP:
                ox += 1;
                break;
            case BUTTON_OFFSET_X_DOWN:
                ox -= 1;
                break;
            case BUTTON_OFFSET_Y_UP:
                oy += 1;
                break;
            case BUTTON_OFFSET_Y_DOWN:
                oy -= 1;
                break;
            case BUTTON_OFFSET_Z_UP:
                oz += 1;
                break;
            case BUTTON_OFFSET_Z_DOWN:
                oz -= 1;
                break;

            case BUTTON_TOGGLE_BOUNDS: {
                this.tile.showBounds = !this.tile.showBounds;
                buttonShowBounds.displayString = I18n.translateToLocal(this.tile.showBounds ? TEXT_HIDE_BOUNDS : TEXT_SHOW_BOUNDS);
                break;
            }

            case BUTTON_SORTING_TYPE: {
                int ord = nsortingType.ordinal();
                if (ord + 1 >= SortingType.VALUES.length) {
                    nsortingType = SortingType.ROWS;
                } else {
                    nsortingType = SortingType.VALUES[ord + 1];
                }

                buttonSortType.displayString = I18n.translateToLocal(TEXT_SORTING_TYPE + nsortingType.getUnlocalizedName());
            }

            default:
                break;
        }

        update(nx, ny, nz, ox, oy, oz, nshowBounds, nsortingType);
    }

    private void update(int nx, int ny, int nz, int offX, int offY, int offZ, boolean nshowBounds, SortingType nsortingType) {
        int ox = x;
        int oy = y;
        int oz = z;
        int noffX = this.offX;
        int noffY = this.offY;
        int noffZ = this.offZ;

        SortingType osortingType = sortingType;

        if (nx <= 0) nx = 1;
        else if (nx >= CommonProxy.maxX) nx = CommonProxy.maxX - 1;
        if (ny <= 0) ny = 1;
        else if (ny >= CommonProxy.maxY) ny = CommonProxy.maxY - 1;
        if (nz <= 0) nz = 1;
        else if (nz >= CommonProxy.maxZ) nz = CommonProxy.maxZ - 1;

        if (tile.getPos().getY() + offY <= 0) offY = offY + 1;

        boundX.setText(Integer.toString(nx));
        boundY.setText(Integer.toString(ny));
        boundZ.setText(Integer.toString(nz));
        offsetX.setText(Integer.toString(offX));
        offsetY.setText(Integer.toString(offY));
        offsetZ.setText(Integer.toString(offZ));

        PacketConfig packet = new PacketConfig(tile.getPos());

        if (ox != nx || oy != ny || oz != nz)
            packet.setBoundaryDimensions(nx, ny, nz);

        if (noffX != offX || noffY != offY || noffZ != offZ)
            packet.setOffsetDimension(offX, offY, offZ);

        if (osortingType != nsortingType)
            packet.setSortingType(nsortingType);

        PacketHandler.INSTANCE.sendToServer(packet);

        x = nx;
        y = ny;
        z = nz;
        this.offX = offX;
        this.offY = offY;
        this.offZ = offZ;
        showBounds = nshowBounds;
        sortingType = nsortingType;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Minecraft.getMinecraft().renderEngine.bindTexture(GUI_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);

        fontRendererObj.drawString(I18n.translateToLocal("gui.label.bounds"), guiLeft + 8, guiTop + 10, 4210752, false);
        fontRendererObj.drawString(I18n.translateToLocal("gui.label.offset"), guiLeft + 8, guiTop + 81, 4210752, false);
        fontRendererObj.drawString(I18n.translateToLocal("gui.label.other"), guiLeft + 8, guiTop + 152, 4210752, false);

        super.drawScreen(mouseX, mouseY, partialTicks);

        boundX.drawTextBox();
        boundY.drawTextBox();
        boundZ.drawTextBox();

        offsetX.drawTextBox();
        offsetY.drawTextBox();
        offsetZ.drawTextBox();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
