package me.dmillerw.storage.block.property;

import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * @author dmillerw
 */
public abstract class UnlistedProperty<V> implements IUnlistedProperty<V> {

    private String name;
    private Class<V> type;

    public UnlistedProperty(String name, Class<V> type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final Class<V> getType() {
        return this.type;
    }
}
