package com.amuzil.omegasource.entity.modules;

import com.amuzil.omegasource.entity.api.IEntityModule;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public class ModuleRegistry {
    private static final Map<String, Supplier<IEntityModule>> FACTORIES = new HashMap<>();

    /** Register a module factory under a unique ID. */
    public static <T extends IEntityModule> void register(Supplier<T> factory) {
        FACTORIES.put(factory.get().id(), factory::get);
    }

    /** Instantiate a fresh module by ID (for JSON defaults).
     * NOTE: Modules are not synced between client and server unless added to the entity's constructor!*/
    public static IEntityModule create(String id) {
        Supplier<IEntityModule> sup = FACTORIES.get(id);
        return sup != null ? sup.get() : null;
    }

    /** Instantiate and load from NBT, returning null if none. */
    public static IEntityModule create(String id, CompoundTag tag) {
        IEntityModule module = create(id);
        if (module != null) {
            module.load(tag);
        }
        return module;
    }
}
