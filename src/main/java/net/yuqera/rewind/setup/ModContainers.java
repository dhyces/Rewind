package net.yuqera.rewind.setup;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public class ModContainers {
    static void register() {}

    private static <T extends ContainerType<?>> RegistryObject<T> register(String name, Supplier<T> item) {
        return Registration.CONTAINERS.register(name, item);
    }

    static {

    }
}