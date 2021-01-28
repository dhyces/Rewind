package net.yuqera.rewind.world;

import net.minecraft.block.SoundType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.yuqera.rewind.models.BlockHistory;

public class TileEntityInteraction {
    public static void placeBlockInWorld(BlockHistory block) {
        block.World.setBlockState(block.Position, block.State, 2);

        playSound(block.World, block.Position, block.State.getSoundType());
    }

    public static void breakBlockInWorld(BlockHistory block) {
        block.World.destroyBlock(block.Position, false);
    }

    private static void playSound(IWorld world, BlockPos position, SoundType type) {
        world.playSound(null, position, type.getBreakSound(), SoundCategory.BLOCKS, type.getVolume(), type.getPitch());
    }
}
