package me.dmillerw.storage.block.property;

/**
 * @author dmillerw
 */
public class UnlistedPropertyNumber extends UnlistedProperty<Number> {

    public UnlistedPropertyNumber(String name) {
        super(name, Number.class);
    }

    @Override
    public boolean isValid(Number value) {
        return true;
    }

    @Override
    public String valueToString(Number value) {
        return value.toString();
    }
}
