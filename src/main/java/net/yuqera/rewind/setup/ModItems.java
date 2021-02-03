package net.yuqera.rewind.setup;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.yuqera.rewind.item.time_watcher.TimeWatcherItem;

import java.util.function.Supplier;

public class ModItems {
    public static final RegistryObject<Item> TIME_WATCHER;
    public static final RegistryObject<Item> TIME_CRYSTAL;
    public static final RegistryObject<Item> UNWOUND_JUKEBOX;

    static void register() {}

    private static <T extends Item> RegistryObject<Item> register(String name, Supplier<T> item) {
        return Registration.ITEMS.register(name, item);
    }

    static {
        TIME_WATCHER = register("time_watcher", TimeWatcherItem::new);
        TIME_CRYSTAL = register("time_crystal", () -> new Item(new Item.Properties().group(Registration.REWIND_ITEM_GROUP)));
        UNWOUND_JUKEBOX = register("unwound_jukebox", () -> makeItemFromBlock(ModBlocks.UNWOUND_JUKEBOX.get(), Registration.REWIND_ITEM_GROUP));
    }
    
    private static BlockItem makeItemFromBlock(Block block, ItemGroup group) {
    	return new BlockItem(block, new Item.Properties().group(group));
    }
}
