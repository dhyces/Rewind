package net.yuqera.rewind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yuqera.rewind.item.time_watcher.TimeWatcherItem;
import net.yuqera.rewind.setup.ModItems;
import net.yuqera.rewind.utils.BlockHistoryHandler;
import net.yuqera.rewind.utils.PlayerInventoryUtilities;

import java.util.ArrayList;
import java.util.Map;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class EventHandler {

    @SubscribeEvent
    public static void breakBlock(BlockEvent.BreakEvent event) {
        // GATES
        if (event.isCanceled() || event.getResult() == Event.Result.DENY)
            return;

        event.setCanceled(true);
        BlockHistoryHandler.pushBlockHistory(event.getPlayer().getHeldItemMainhand(), event.getState().getBlock(), event.getPos());
        event.getWorld().removeBlock(event.getPos(), false);
        System.out.println();
        ArrayList<ItemStack> foundWatches = PlayerInventoryUtilities.getAllItemStacksWhere(event.getPlayer().inventory, ModItems.TIME_WATCHER.get());
        System.out.println(getFirstActiveWatch(foundWatches).getTag());

    }

    private static ItemStack getFirstActiveWatch(ArrayList<ItemStack> watches) {
        ItemStack firstActiveWatch = new ItemStack(Items.AIR, 1);
        for (ItemStack watch : watches) {
            if (watch.getOrCreateTag().getFloat(TimeWatcherItem.NBT_TAG_NAME_ACTIVATION) != 0.0F) {
                firstActiveWatch = watch;
                break;
            }
        }
        return firstActiveWatch;
    }
}
