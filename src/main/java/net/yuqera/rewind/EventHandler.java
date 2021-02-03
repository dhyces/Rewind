package net.yuqera.rewind;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.yuqera.rewind.config.RewindConfig;
import net.yuqera.rewind.item.time_watcher.TimeWatcherItem;
import net.yuqera.rewind.setup.ModItems;
import net.yuqera.rewind.utils.BlockHistoryHandler;
import net.yuqera.rewind.utils.PlayerInventoryUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Mod.EventBusSubscriber(bus = Bus.FORGE, modid = RewindMod.MOD_ID)
public class EventHandler {

    @SubscribeEvent
    public static void breakBlock(final BlockEvent.BreakEvent event) {
        // GATES
        if (event.isCanceled() || event.getResult() == Event.Result.DENY)
            return;

        ArrayList<ItemStack> foundWatches = PlayerInventoryUtilities.getAllItemStacksWhere(event.getPlayer().inventory, ModItems.TIME_WATCHER.get());
        ItemStack watch = getFirstActiveWatch(foundWatches);
        if (watch.isEmpty()) return;
        event.setCanceled(true);
        BlockHistoryHandler.pushBlockHistory(watch, event.getState().getBlock(), event.getPos(), (World)event.getWorld());
        event.getWorld().removeBlock(event.getPos(), false);
        System.out.println("\n" + getFirstActiveWatch(foundWatches).getTag());

    }
    
    // TODO: Remove, as it doesn't fit how the watches will function. Only one watch should be active at a time
    private static Optional<List<ItemStack>> getNonFullActiveWatches(List<ItemStack> watches) {
        return Optional.of(watches).filter(c -> c.stream().anyMatch(x -> {
        	return TimeWatcherItem.getActivationState(x).getState() && BlockHistoryHandler.getTimeWatchHistoryCount(x) < RewindConfig.rewind_max_size.get();
        }));
    }

    private static ItemStack getFirstActiveWatch(List<ItemStack> watches) {
    	ItemStack firstActiveWatch = ItemStack.EMPTY;
        for (ItemStack watch : watches) {
            if (watch.getOrCreateTag().getFloat(TimeWatcherItem.NBT_TAG_NAME_ACTIVATION) != 0.0F) {
                firstActiveWatch = watch;
                break;
            }
        }
        return firstActiveWatch;
    }
}
