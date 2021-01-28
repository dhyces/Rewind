package net.yuqera.rewind.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class SoundManager {
    public static void playSoundAtPlayer(World world, PlayerEntity playerIn, SoundEvent event, SoundCategory category) {
        if (world.isRemote) { // Client side
            world.playSound(playerIn, playerIn.getPosition(), event, category, 1.0F, 1.0F);
        }
        else { // Server side
            if (playerIn instanceof ServerPlayerEntity) {
                ServerPlayerEntity entityPlayerMP = (ServerPlayerEntity)playerIn;
                world.playSound(entityPlayerMP, entityPlayerMP.getPosition(), event, category, 1.0F, 1.0F);
            }
        }
    }
}
