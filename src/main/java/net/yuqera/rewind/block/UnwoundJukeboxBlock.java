package net.yuqera.rewind.block;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.google.common.base.Supplier;

import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.yuqera.rewind.item.time_watcher.TimeWatcherItem;
import net.yuqera.rewind.setup.ModParticles;
import net.yuqera.rewind.setup.ModTiles;
import net.yuqera.rewind.tileentity.UnwoundJukeboxTileEntity;

public class UnwoundJukeboxBlock extends HorizontalBlock {

	public UnwoundJukeboxBlock() {
		super(Properties.create(Material.WOOD, MaterialColor.BLUE_TERRACOTTA).hardnessAndResistance(2.0F, 6.0F));
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		//This could be unnecessary or moved into a utils class
		Supplier<Integer> negative = () -> {if (rand.nextFloat() > 0.5F) return -1; else return 1;};
		float x = pos.getX() + rand.nextFloat();
		float y = pos.getY() + 1.2F;
		float z = pos.getZ() + rand.nextFloat();
		float speedModifier = 0.1F;
		float xSpeed = rand.nextFloat() * speedModifier * negative.get();
		float ySpeed = rand.nextFloat() * speedModifier * negative.get();
		float zSpeed = rand.nextFloat() * speedModifier * negative.get();
		worldIn.addParticle(ModParticles.REWIND_PARTICLE.get(), x, y, z, xSpeed, ySpeed, zSpeed);
	}
	
	public boolean setWatch(World worldIn, BlockPos pos, BlockState state, ItemStack stack) {
		if (!(stack.getItem() instanceof TimeWatcherItem)) return false;
		Optional<TileEntity> tileOptional = getUnwoundTile(worldIn, pos);
		if (tileOptional.isPresent()) {
			UnwoundJukeboxTileEntity tile = ((UnwoundJukeboxTileEntity)tileOptional.get());
			if (tile.isWatchEmpty()) {
				tile.setWatch(stack.copy());
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		Optional<TileEntity> tileOptional = getUnwoundTile(worldIn, pos);
		if (!worldIn.isRemote && tileOptional.isPresent())
			((UnwoundJukeboxTileEntity)tileOptional.get()).dropWatch();
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}
	
	private Optional<TileEntity> getUnwoundTile(World worldIn, BlockPos pos) {
		Optional<TileEntity> tile = Optional.ofNullable(worldIn.getTileEntity(pos));
		if (tile.isPresent() && tile.get() instanceof UnwoundJukeboxTileEntity) return tile;
		return Optional.empty();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTiles.UNWOUND_JUKEBOX_TILE.get().create();
	}
	
}
