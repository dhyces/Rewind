package net.yuqera.rewind.utils;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.yuqera.rewind.config.RewindConfig;
import net.yuqera.rewind.models.BlockHistory;
import net.yuqera.rewind.setup.ModItems;

import java.util.*;

public class BlockHistoryHandler {

    public static final String NBT_TAG_NAME_HISTORY_COUNT = "block_history_count";
    public static final String NBT_TAG_NAME_BLOCK_HISTORY = "block_history";

    public static void pushBlockHistory(ItemStack watch, Block block, BlockPos pos, World worldIn) {
        // GATES
        if (!watch.getItem().equals(ModItems.TIME_WATCHER.get()) || getTimeWatchHistoryCount(watch) >= RewindConfig.rewind_max_size.get())
            return;

        // Get all variables ready
        CompoundNBT compoundNBT = watch.getOrCreateTag();
        int historyCount = getTimeWatchHistoryCount(watch);
        historyCount++;
        BlockHistory newBlockHistory = new BlockHistory(historyCount, block.getRegistryName(), pos, worldIn.getDimensionKey().getLocation());

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
        return compoundNBT.getList(NBT_TAG_NAME_BLOCK_HISTORY, Constants.NBT.TAG_COMPOUND);
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

    public static LinkedList<BlockHistory> convertListNBTToBlockHistory(ListNBT blockHistoryNBTList) {
        LinkedList<BlockHistory> blockHistory = new LinkedList<>();
        for (int i = 0; i < blockHistoryNBTList.size(); i++) {
            CompoundNBT blockHistoryNBT = blockHistoryNBTList.getCompound(i);
            blockHistory.add(convertCompoundNBTToBlockHistory(blockHistoryNBT));
        }
        Collections.sort(blockHistory);
        return blockHistory;
    }
    
    public static BlockHistory convertCompoundNBTToBlockHistory(CompoundNBT nbt) {
    	int timeLocation = nbt.getInt(NBT_TAG_NAME_TIME_LOCATION);
    	ResourceLocation resourceLocation = ResourceLocation.create(nbt.getString(NBT_TAG_NAME_HISTORY_BLOCK), ':');
    	BlockPos blockPos = makeBlockPosFromArray(nbt.getIntArray(NBT_TAG_NAME_BLOCK_LOCATION));
    	return new BlockHistory(timeLocation, resourceLocation, blockPos, null);
    }
    
    public static CompoundNBT convertBlockHistoryToCompoundNBT(BlockHistory blockHistory) {
        CompoundNBT newCompound = new CompoundNBT();
        newCompound.putInt(NBT_TAG_NAME_TIME_LOCATION, blockHistory.getTimePosition());
        newCompound.putString(NBT_TAG_NAME_HISTORY_BLOCK, blockHistory.getResourceLocation().toString());
        newCompound.putIntArray(NBT_TAG_NAME_BLOCK_LOCATION, new int[] {blockHistory.getPosition().getX(), blockHistory.getPosition().getY(), blockHistory.getPosition().getZ()});
        return newCompound;
    }
    
    private static BlockPos makeBlockPosFromArray(int[] array) {
    	if (array.length != 3)
    		throw new IllegalStateException();
    	return new BlockPos(array[0], array[1], array[2]);
    }
    
    public static final String NBT_TAG_NAME_TIME_LOCATION = "time_location";
    public static final String NBT_TAG_NAME_HISTORY_BLOCK = "history_block";
    public static final String NBT_TAG_NAME_BLOCK_LOCATION = "block_location";
}
