package net.yuqera.rewind.setup;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;
import net.yuqera.rewind.RewindMod;
import net.yuqera.rewind.item.time_watcher.TimeWatcherItem;

@ObjectHolder(RewindMod.MOD_ID)
public class AnnotatedHolder {

    @ObjectHolder("rewind:time_watcher")
    public static final TimeWatcherItem timeWatcher = null;
}
