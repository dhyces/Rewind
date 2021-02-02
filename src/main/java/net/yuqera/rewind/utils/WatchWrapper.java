package net.yuqera.rewind.utils;

import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ForgeRegistries;
import net.yuqera.rewind.models.BlockHistory;

public class WatchWrapper {
	
	public static final WatchWrapper EMPTY = new WatchWrapper(ItemStack.EMPTY);
	private final ItemStack watch;
	private final LinkedList<BlockHistory> history;
	
	public WatchWrapper(ItemStack stack) {
		this.watch = stack;
		this.history = BlockHistoryHandler.convertListNBTToBlockHistory(BlockHistoryHandler.getBlockHistoryNBT(watch));
		System.out.println(this.watch.serializeNBT());
	}
	
	/** @return true if the ItemStack is empty or if the history size is equal to zero, otherwise false*/
	public boolean isEmpty() {
		return this.watch.isEmpty() || this.history.size() == 0;
	}
	
	public ItemStack getItem() {
		System.out.println(this.watch.serializeNBT());
		return this.watch;
	}
	
	public Optional<Pair<BlockPos, Block>> getLast() {
		if (this.history.isEmpty()) return Optional.empty();
		BlockHistory history = this.history.pollLast();
		removeFromNBT(history);
		return makePair(history);
	}
	
	public void removeFromNBT(BlockHistory history) {
		CompoundNBT nbt = this.watch.getOrCreateTag();
		ListNBT list = nbt.getList(BlockHistoryHandler.NBT_TAG_NAME_BLOCK_HISTORY, Constants.NBT.TAG_COMPOUND);
		ListNBT newList = list.stream().map(c -> CompoundNBT.class.cast(c))
									   .filter(history::matchesCompoundNBT)
									   .collect(Collectors.toCollection(ListNBT::new));
		CompoundNBT newNBT = new CompoundNBT();
		newNBT.put(BlockHistoryHandler.NBT_TAG_NAME_BLOCK_HISTORY, newList);
		this.watch.setTag(newNBT);
	}
	
	@SuppressWarnings("deprecation")
	private Optional<Pair<BlockPos, Block>> makePair(BlockHistory history) {
		ResourceLocation blockRL = history.getResourceLocation();
		Block block = Registry.BLOCK.getOptional(blockRL).orElseGet(() -> ForgeRegistries.BLOCKS.getValue(blockRL));
		if (block.matchesBlock(Blocks.AIR)) return Optional.empty();
		
		return Optional.of(Pair.of(history.getPosition(), block));
	}
	
	public static WatchWrapper of(ItemStack stack) {
		return new WatchWrapper(stack);
	}
}
