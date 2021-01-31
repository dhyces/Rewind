package net.yuqera.rewind.setup;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.yuqera.rewind.RewindMod;

public class Registration {
    public static final RewindItemGroup REWIND_ITEM_GROUP = new RewindItemGroup("rewind");

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RewindMod.MOD_ID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, RewindMod.MOD_ID);

    public static void register() {
        // SETUP ITEMS/BLOCKS
        RewindMod.LOGGER.info("Setting up modEventBusRegister.");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ITEMS.register(modEventBus);
        CONTAINERS.register(modEventBus);

        // REGISTER ITEMS/BLOCKS
        RewindMod.LOGGER.info("Registering Mod.");
        ModItems.register();
        ModContainers.register();
    }
}

