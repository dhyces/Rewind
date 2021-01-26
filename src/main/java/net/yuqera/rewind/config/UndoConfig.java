package net.yuqera.rewind.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class UndoConfig {
    public static ForgeConfigSpec.IntValue rewind_max_size;

    public static void init(ForgeConfigSpec.Builder server, ForgeConfigSpec.Builder client) {
        server.comment("Undo Config");

        rewind_max_size = server
                .comment("Maximum value of undo/redo' allowed.")
                .defineInRange("undo.max_size", 50, 1, 1000000);
    }
}
