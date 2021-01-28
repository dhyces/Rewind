package net.yuqera.rewind.services;

import net.yuqera.rewind.config.RewindConfig;
import net.yuqera.rewind.enums.BlockAction;
import net.yuqera.rewind.models.BlockHistory;
import net.yuqera.rewind.world.TileEntityInteraction;

import java.util.ArrayList;
import java.util.List;

public class UndoService {
    public static List<BlockHistory> history = new ArrayList<>();

    public static void executeLatestAction() {
        if(history.size() == 0)
            return;

        BlockHistory latestBlockHistory = history.get(history.size()-1);

        RedoService.history.add(latestBlockHistory);
        history.remove(latestBlockHistory);
        if (latestBlockHistory.Action == BlockAction.Place) {
            TileEntityInteraction.breakBlockInWorld(latestBlockHistory);
        }
        else {
            TileEntityInteraction.placeBlockInWorld(latestBlockHistory);
        }
        RedoService.addNewAction(latestBlockHistory);
    }

    public static void addNewAction(BlockHistory blockHistory) {
        if (blockHistory == null || history.contains(blockHistory))
            return;
        if (history.size() >= RewindConfig.rewind_max_size.get()) {
            history.remove(0);
        }
        history.add(blockHistory);
    }
}
