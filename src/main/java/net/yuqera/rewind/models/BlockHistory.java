package net.yuqera.rewind.models;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.yuqera.rewind.utils.BlockHistoryHandler;

public class BlockHistory implements Comparable<BlockHistory> {
    private final Integer TimePosition;
    private final ResourceLocation ResourceLocation;
    private final BlockPos Position;
    private final ResourceLocation Dimension;

    public BlockHistory(int timePosition, ResourceLocation resourceLocation, BlockPos position, ResourceLocation dimension) {
        TimePosition = timePosition;
        ResourceLocation = resourceLocation;
        Position = position;
        Dimension = dimension;
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
    
    public ResourceLocation getDimension() {
    	return this.Dimension;
    }

    @Override
    public int compareTo(BlockHistory o) {
        return this.getTimePosition().compareTo(o.getTimePosition());
    }
    
    public boolean matchesCompoundNBT(CompoundNBT nbt) {
    	BlockHistory nbtHistory = BlockHistoryHandler.convertCompoundNBTToBlockHistory(nbt);
    	return this.getPosition().equals(nbtHistory.getPosition()) && this.TimePosition == nbtHistory.getTimePosition() &&
    			this.ResourceLocation.equals(nbtHistory.ResourceLocation);
    }
    
    @Override
    public String toString() {
    	return "Time position: [" + this.TimePosition + "] Block position: " + this.Position + " ResourceLocation of block: "
    																						   + this.ResourceLocation;
    }
}
