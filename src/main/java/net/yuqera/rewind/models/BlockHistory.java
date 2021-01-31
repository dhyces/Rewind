package net.yuqera.rewind.models;

import net.minecraft.block.BlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.yuqera.rewind.enums.BlockAction;

public class BlockHistory implements Comparable<BlockHistory> {
    private final Integer TimePosition;
    private final ResourceLocation ResourceLocation;
    private final BlockPos Position;

    public BlockHistory(int timePosition, ResourceLocation resourceLocation, BlockPos position) {
        TimePosition = timePosition;
        ResourceLocation = resourceLocation;
        Position = position;
    }

    public Integer getTimePosition() {
        return this.TimePosition;
    }

    public ResourceLocation getResourceLocation() {
        return this.ResourceLocation;
    }

    public BlockPos getPosition() {
        return this.Position;
    }

    @Override
    public int compareTo(BlockHistory o) {
        return this.getTimePosition().compareTo(o.getTimePosition());
    }
}
