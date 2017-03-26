package me.dmillerw.storage.lib.data;

/**
 * @author dmillerw
 */
public enum SortingType {

    ROWS("rows"),
    COLUMNS("columns"),
    MESSY("messy");

    private final String name;
    private SortingType(String name) {
        this.name = name;
    }

    public String getUnlocalizedName() {
        return this.name;
    }

    public static final SortingType[] VALUES = values();
}
