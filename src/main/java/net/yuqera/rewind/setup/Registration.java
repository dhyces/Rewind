package net.yuqera.rewind.setup;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import java.util.HashMap;
import java.util.Map;

public class Registration {
    public static final Map<String, KeyBinding> MOD_CONTROLS = new HashMap<>();

    public static void register() {

        initKeyBindings();

        for (KeyBinding keyBinding : MOD_CONTROLS.values()) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }
    }
    
    private static void initKeyBindings() {
        KeyBinding undo = new KeyBinding("Undo",90,"Rewind");
        MOD_CONTROLS.put("UNDO", undo);
        KeyBinding redo = new KeyBinding("Redo",82,"Rewind");
        MOD_CONTROLS.put("REDO", redo);
    }
}
