package net.yuqera.rewind.services;

import net.yuqera.rewind.config.UndoConfig;
import net.yuqera.rewind.enums.BlockAction;
import net.yuqera.rewind.models.BlockHistory;

import java.util.ArrayList;
import java.util.LinkedList;
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
            BlockService.breakBlockInWorld(latestBlockHistory);
        }
        else {
            BlockService.placeBlockInWorld(latestBlockHistory);
        }
        RedoService.addNewAction(latestBlockHistory);
    }

    public static void addNewAction(BlockHistory blockHistory) {
        if (blockHistory == null || history.contains(blockHistory))
            return;
        if (history.size() >= UndoConfig.rewind_max_size.get()) {
            history.remove(0);
        }
        history.add(blockHistory);
    }
}
