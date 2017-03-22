
package me.dmillerw.storage.block.property;

/**
 * @author dmillerw
 */
public class UnlistedPropertyBoolean extends UnlistedProperty<Boolean> {

    public UnlistedPropertyBoolean(String name) {
        super(name, Boolean.class);
    }

    @Override
    public boolean isValid(Boolean value) {
        return true;
    }

    @Override
    public String valueToString(Boolean value) {
        return value.toString();
    }
}
