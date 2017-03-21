package me.dmillerw.storage.client.model;

import com.google.common.collect.Maps;
import me.dmillerw.storage.lib.ModInfo;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.Map;
import java.util.function.Predicate;

/**
 * @author dmillerw
 */
public class BaseModelLoader implements ICustomModelLoader {

    private static Map<Predicate<String>, IModel> modelRegistry = Maps.newHashMap();
    static {
        modelRegistry.put((new Predicate<String>() {
            @Override
            public boolean test(String path) {
                return path.contains("block") && path.contains("item_block");
            }
        }), new ItemBlockModel());
    }

    @Override
    public boolean accepts(ResourceLocation modelLocation) {
        if (!modelLocation.getResourceDomain().equals(ModInfo.ID))
            return false;

        final String path = modelLocation.getResourcePath();

        for (Map.Entry<Predicate<String>, IModel> entry : modelRegistry.entrySet()) {
            if (entry.getKey().test(path))
                return true;
        }

        return false;
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        final String path = modelLocation.getResourcePath();

        for (Map.Entry<Predicate<String>, IModel> entry : modelRegistry.entrySet()) {
            if (entry.getKey().test(path))
                return entry.getValue();
        }

        return null;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {

    }
}