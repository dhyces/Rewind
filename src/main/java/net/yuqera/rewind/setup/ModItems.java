package net.yuqera.rewind.setup;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.yuqera.rewind.item.time_watcher.TimeWatcherItem;

import java.util.function.Supplier;

public class ModItems {
    public static final RegistryObject<Item> TIME_WATCHER;

    static void register() {}

    private static <T extends Item> RegistryObject<Item> register(String name, Supplier<T> item) {
        return Registration.ITEMS.register(name, item);
    }

    static {
        TIME_WATCHER = register("time_watcher", TimeWatcherItem::new);
    }
}
