package net.yuqera.rewind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yuqera.rewind.enums.BlockAction;
import net.yuqera.rewind.models.BlockHistory;
import net.yuqera.rewind.services.RedoService;
import net.yuqera.rewind.services.UndoService;

import java.util.Map;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class EventHandler {

    @SubscribeEvent
    public static void placeBlock(BlockEvent.EntityPlaceEvent event) {
        // GATES
        if (event.isCanceled() || event.getResult() == Event.Result.DENY)
            return;

        // VARIABLES
        IWorld world = event.getWorld();
        BlockHistory blockHistory = new BlockHistory(world, event.getPos(), event.getPlacedBlock(), BlockAction.Place);

        // EXECUTION
        UndoService.addNewAction(blockHistory);
    }

    @SubscribeEvent
    public static void breakBlock(BlockEvent.BreakEvent event) {
        // GATES
        if (event.isCanceled() || event.getResult() == Event.Result.DENY)
            return;

        // VARIABLES
        IWorld world = event.getWorld();
        BlockHistory blockHistory = new BlockHistory(world, event.getPos(), event.getState(), BlockAction.Break);

        // EXECUTION
        UndoService.addNewAction(blockHistory);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
    public static void keyPress(InputEvent.KeyInputEvent event) {
        Map<String, KeyBinding> keyBindings = RewindMod.MOD_CONTROLS;

        if (keyBindings.get("UNDO").isKeyDown()) {
            UndoService.executeLatestAction();
        }
        else if (keyBindings.get("REDO").isKeyDown()) {
            RedoService.executeLatestAction();
        }
    }
}
