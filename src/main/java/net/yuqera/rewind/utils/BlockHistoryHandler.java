package net.yuqera.rewind.utils;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;
import net.yuqera.rewind.RewindMod;
import net.yuqera.rewind.models.BlockHistory;
import net.yuqera.rewind.setup.AnnotatedHolder;
import net.yuqera.rewind.setup.ModItems;

import java.util.*;

public class BlockHistoryHandler {

    public static final String NBT_TAG_NAME_HISTORY_COUNT = "block_history_count";
    public static final String NBT_TAG_NAME_BLOCK_HISTORY = "block_history";

    public static void pushBlockHistory(ItemStack watch, Block block, BlockPos pos) {
        // GATES
        if (!watch.getItem().equals(ModItems.TIME_WATCHER.get()))
            return;

        // Get all variables ready
        CompoundNBT compoundNBT = watch.getOrCreateTag();
        int historyCount = getTimeWatchHistoryCount(watch);
        historyCount++;
        BlockHistory newBlockHistory = new BlockHistory(historyCount, block.getRegistryName(), pos);

        // Create the new compound;
        CompoundNBT blockHistoryCompound = convertBlockHistoryToCompoundNBT(newBlockHistory);
        ListNBT blockHistoryCompoundList = compoundNBT.getList(NBT_TAG_NAME_BLOCK_HISTORY, 10);
        blockHistoryCompoundList.add(blockHistoryCompound);

        System.out.println(blockHistoryCompoundList);

        compoundNBT.put(NBT_TAG_NAME_BLOCK_HISTORY, blockHistoryCompoundList);
        compoundNBT.putInt(NBT_TAG_NAME_HISTORY_COUNT, historyCount);
        watch.setTag(compoundNBT);

        ListNBT optionalINBTS = getBlockHistoryNBT(watch);
        convertListNBTToBlockHistory(optionalINBTS);
    }
    public static ListNBT getBlockHistoryNBT(ItemStack watch) {
        CompoundNBT compoundNBT = watch.getTag();
        if (compoundNBT == null || !compoundNBT.contains(NBT_TAG_NAME_BLOCK_HISTORY))  {
            return new ListNBT();
        }
        return compoundNBT.getList(NBT_TAG_NAME_BLOCK_HISTORY, 10);
    }

    public static int getTimeWatchHistoryCount(ItemStack watch) {
        ListNBT blockHistoryNBTList = getBlockHistoryNBT(watch);
        if (blockHistoryNBTList.isEmpty()) {
            return 0;
        }
        return convertListNBTToBlockHistory(blockHistoryNBTList).size();
    }

    public static void putInNBT(LinkedList<BlockHistory> blockHistories) {

    }

    private static LinkedList<BlockHistory> convertListNBTToBlockHistory(ListNBT blockHistoryNBTList) {
        LinkedList<BlockHistory> blockHistory = new LinkedList<>();
        for (int i = 0; i < blockHistoryNBTList.size(); i++) {
            CompoundNBT blockHistoryNBT = blockHistoryNBTList.getCompound(i);
            int timePosition = blockHistoryNBT.getInt(NBT_TAG_NAME_HISTORY_COUNT);
            ResourceLocation blockResourceLocation = new ResourceLocation(RewindMod.MOD_ID, blockHistoryNBT.getString(NBT_TAG_NAME_BLOCK_HISTORY));
            int[] blockPosList = blockHistoryNBT.getIntArray(NBT_TAG_NAME_BLOCK_LOCATION);
            BlockPos blockPosition = new BlockPos(blockPosList[0], blockPosList[1], blockPosList[2]);
            blockHistory.add(new BlockHistory(timePosition, blockResourceLocation, blockPosition));
        }
        Collections.sort(blockHistory);
        return blockHistory;
    }
    private static CompoundNBT convertBlockHistoryToCompoundNBT(BlockHistory blockHistory) {
        CompoundNBT newCompound = new CompoundNBT();
        newCompound.putInt(NBT_TAG_NAME_TIME_LOCATION, blockHistory.getTimePosition());
        newCompound.putString(NBT_TAG_NAME_HISTORY_BLOCK, blockHistory.getResourceLocation().toString());
        newCompound.putIntArray(NBT_TAG_NAME_BLOCK_LOCATION, new int[] {blockHistory.getPosition().getX(), blockHistory.getPosition().getY(), blockHistory.getPosition().getZ()});
        return newCompound;
    }

    private static final String NBT_TAG_NAME_TIME_LOCATION = "time_location";
    private static final String NBT_TAG_NAME_HISTORY_BLOCK = "history_block";
    private static final String NBT_TAG_NAME_BLOCK_LOCATION = "block_location";
}
