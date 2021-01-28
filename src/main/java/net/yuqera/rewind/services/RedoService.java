package net.yuqera.rewind.services;

import net.yuqera.rewind.enums.BlockAction;
import net.yuqera.rewind.models.BlockHistory;
import net.yuqera.rewind.world.TileEntityInteraction;

import java.util.ArrayList;
import java.util.List;

public class RedoService {
    public static List<BlockHistory> history = new ArrayList<>();
    private static boolean renew = false;

    public static void executeLatestAction() {
        if (history.size() == 0)
            return;

        BlockHistory latestBlockHistory = history.get(history.size()-1);

        UndoService.history.add(latestBlockHistory);
        history.remove(latestBlockHistory);
        if (latestBlockHistory.Action == BlockAction.Place) {
            TileEntityInteraction.placeBlockInWorld(latestBlockHistory);
        }
        else {
            TileEntityInteraction.breakBlockInWorld(latestBlockHistory);
        }
        renew = true;
    }

    public static void addNewAction(BlockHistory blockHistory) {
        if (blockHistory == null || history.contains(blockHistory))
            return;

        history.add(blockHistory);
        if (renew) {
            history = new ArrayList<>();
            renew = false;
        }
    }
}
