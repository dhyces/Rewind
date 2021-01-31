package net.yuqera.rewind.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class RewindConfig {
    public static ForgeConfigSpec.IntValue rewind_max_size;
    public static ForgeConfigSpec.IntValue creative_rewind_max_size;

    public static void init(ForgeConfigSpec.Builder server, ForgeConfigSpec.Builder client) {
        server.comment("Rewind Config");

        rewind_max_size = server
                .comment("Maximum value of the survival rewinds (Undo/Redo) allowed.")
                .defineInRange("rewind.max_size", 100, 1, 10000);

        creative_rewind_max_size = server
                .comment("Maximum value of the creative rewinds (Undo/Redo) allowed.")
                .defineInRange("rewind.creative_max_size", 1000, 1, 100000);
    }
}
