package me.dmillerw.storage.lib.data;

/**
 * @author dmillerw
 */
public enum SortingType {

    ROWS("rows", PositionHandler.BAKED, PositionHandler.ROW_HANDLER, SizeCalculator.DEFAULT),
    COLUMNS("columns", PositionHandler.BAKED, PositionHandler.COLUMN_HANDLER, SizeCalculator.DEFAULT),
    PYRAMID("pyramid", PositionHandler.BAKED, PositionHandler.PYRAMID_HANDLER, SizeCalculator.PYRAMID),
    MESSY("messy", PositionHandler.RUNTIME, PositionHandler.MESSY_HANDLER, SizeCalculator.DEFAULT);

    private final String name;
    private final byte type;
    private final PositionHandler positionHandler;
    private final SizeCalculator sizeCalculator;
    private SortingType(String name, byte type, PositionHandler positionHandler, SizeCalculator sizeCalculator) {
        this.name = name;
        this.type = type;
        this.positionHandler = positionHandler;
        this.sizeCalculator = sizeCalculator;
    }

    public String getUnlocalizedName() {
        return this.name;
    }

    public PositionHandler getPositionHandler() {
        return positionHandler;
    }

    public SizeCalculator getSizeCalculator() {
        return sizeCalculator;
    }

    public boolean isBaked() {
        return this.type == PositionHandler.BAKED;
    }

    public static final SortingType[] VALUES = values();
}
