package net.yuqera.rewind.models;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.yuqera.rewind.enums.BlockAction;

public class BlockHistory {
    public IWorld World;
    public BlockPos Position;
    public BlockState State;
    public BlockAction Action;

    public BlockHistory(IWorld world, BlockPos position, BlockState state, BlockAction action) {
        World = world;
        Position = position;
        State = state;
        Action = action;
    }
}
