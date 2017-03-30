package me.dmillerw.storage.lib.data;

import static me.dmillerw.storage.lib.data.PositionHandler.*;

/**
 * @author dmillerw
 */
public enum SortingType {

    ROWS("rows", BAKED, ROW_HANDLER),
    COLUMNS("columns", BAKED, COLUMN_HANDLER),
    MESSY("messy", RUNTIME, MESSY_HANDLER);

    private final String name;
    private final byte type;
    private final PositionHandler positionHandler;
    private SortingType(String name, byte type, PositionHandler positionHandler) {
        this.name = name;
        this.type = type;
        this.positionHandler = positionHandler;
    }

    public String getUnlocalizedName() {
        return this.name;
    }

    public PositionHandler getPositionHandler() {
        return positionHandler;
    }

    public boolean isBaked() {
        return this.type == BAKED;
    }

    public static final SortingType[] VALUES = values();
}
