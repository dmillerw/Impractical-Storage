package me.dmillerw.storage.block.property;

/**
 * @author dmillerw
 */
public class UnlistedPropertyString extends UnlistedProperty<String> {

    public UnlistedPropertyString(String name) {
        super(name, String.class);
    }

    @Override
    public boolean isValid(String value) {
        return true;
    }

    @Override
    public String valueToString(String value) {
        return value;
    }
}
