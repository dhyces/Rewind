package net.yuqera.rewind.tileentity;

import java.util.List;
import java.util.stream.Collectors;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.RangedInteger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.TickRangeConverter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.yuqera.rewind.RewindMod;
import net.yuqera.rewind.setup.ModTiles;
import net.yuqera.rewind.utils.WatchWrapper;

public class UnwoundJukeboxTileEntity extends TileEntity implements ITickableTileEntity {

	private WatchWrapper watch = WatchWrapper.EMPTY;
	private int tick = 0;
	// This is to make it easier to defer between how many seconds it takes to perform its function. Only the minimum is used right now
	private RangedInteger doTick = TickRangeConverter.convertRange(1, 2);
	
	public UnwoundJukeboxTileEntity() {
		super(ModTiles.UNWOUND_JUKEBOX_TILE.get());
		
	}
	
	public void setWatch(ItemStack watchIn) {
		this.watch = WatchWrapper.of(watchIn);
	}
	
	/** @return true if the ItemStack is empty or if the history size is equal to zero, otherwise false*/
	public boolean isWatchEmpty() {
		return this.watch.isEmpty();
	}
	
	@Override
	public void tick() {
		++tick;
		if (isTick()) {
			if (!getWorld().isRemote) {
				if (this.watch.getItem().isEmpty()) return;
				Pair<BlockPos, Block> pair = this.watch.getLast().orElseGet(() -> Pair.of(getPos(), Blocks.JUKEBOX));
				setStateOrSpawnLoot(pair);
				getWorld().playSound((PlayerEntity)null, getPos(), SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.RECORDS, 1F, 1.8F);
			}
		}
	}
	
	protected boolean isTick() {
		return (this.tick %= doTick.getMinInclusive()) == 0;
	}
	
	/** @return True if loot was dropped and block was replaced with air, false otherwise*/
	private boolean setStateOrSpawnLoot(Pair<BlockPos, Block> pair) {
		// TODO: Eventually move this somewhere else, a loot table should be stored in a separate static instance.
		ResourceLocation resourcelocation = new ResourceLocation(RewindMod.MOD_ID, "gameplay/unwind");
		LootTable table = getWorld().getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);
		List<ItemStack> loot = table.generate(new LootContext.Builder((ServerWorld)getWorld())
															 .withRandom(getWorld().rand)
															 .withParameter(LootParameters.BLOCK_STATE, getBlockState())
															 .withParameter(LootParameters.field_237457_g_, Vector3d.copy(getPos()))
															 .withParameter(LootParameters.TOOL, ItemStack.EMPTY)
															 .build(LootParameterSets.BLOCK));
		loot = loot.stream().filter(c -> !c.isEmpty()).collect(Collectors.toList());
		if (loot.isEmpty() || pair.getFirst().equals(getPos())) {
			setBlockStateFromPair(pair);
			SoundEvent sound = pair.getSecond().getSoundType(pair.getSecond().getDefaultState()).getPlaceSound();
			getWorld().playSound((PlayerEntity)null, pair.getFirst(), sound, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return false;
		} else {
			InventoryHelper.dropItems(getWorld(), pair.getFirst(), NonNullList.from(ItemStack.EMPTY, loot.toArray(new ItemStack[loot.size()])));
			getWorld().playSound((PlayerEntity)null, pair.getFirst(), SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
			return true;
		}
	} 
	
	// TODO: This sets the block at the position no matter what, doesn't matter if a diamond block was placed there in the mean time.
	private void setBlockStateFromPair(Pair<BlockPos, Block> pair) {
		getWorld().setBlockState(pair.getFirst(), pair.getSecond().getDefaultState());
	}
	
	// TODO: This drops an empty watch, it loses any of the block history that it should have.
	public void dropWatch() {
		BlockPos pos = getPos();
		ItemEntity entity = new ItemEntity(getWorld(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, this.watch.getItem());
		System.out.println(this.watch.getItem().serializeNBT() + " " + entity.serializeNBT() + " " + this.watch.getItem().copy().serializeNBT());
		getWorld().addEntity(entity);
	}
	
	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		if (!nbt.getCompound("Item").isEmpty())
			this.watch = WatchWrapper.of(ItemStack.read(nbt.getCompound("Item")));
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		if (!this.watch.getItem().isEmpty())
			compound.put("Item", this.watch.getItem().serializeNBT());
		return compound;
	}
}
