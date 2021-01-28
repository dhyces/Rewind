package net.yuqera.rewind;

import net.minecraft.block.Blocks;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.yuqera.rewind.config.Config;
import net.yuqera.rewind.item.TimeWatcherItem;
import net.yuqera.rewind.setup.AnnotatedHolder;
import net.yuqera.rewind.setup.ModItems;
import net.yuqera.rewind.setup.Registration;
import net.yuqera.rewind.setup.RewindItemGroup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(RewindMod.MOD_ID)
public class RewindMod
{
    public static final Map<String, KeyBinding> MOD_CONTROLS = new HashMap<>();
    public static final String MOD_ID = "rewind";

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public RewindMod() {
        Registration.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.server_config);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.client_config);

        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        Config.loadConfig(Config.client_config, FMLPaths.CONFIGDIR.get().resolve("rewindmod-client.toml").toString());
        Config.loadConfig(Config.server_config, FMLPaths.CONFIGDIR.get().resolve("rewindmod-server.toml").toString());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);

        // SETUP CONTROLS
        LOGGER.info("Setting up keybindings for controls.");
        initKeyBindings();
        for (KeyBinding keyBinding : MOD_CONTROLS.values()) {
            ClientRegistry.registerKeyBinding(keyBinding);
        }

        event.enqueueWork(RewindMod::registerPropertyOverride);
    }

    private static void registerPropertyOverride() {
        ItemModelsProperties.registerProperty(AnnotatedHolder.timeWatcher, new ResourceLocation("activated"),TimeWatcherItem::getActivatedPropertyOverride);
    }

    private static void initKeyBindings() {
        KeyBinding undo = new KeyBinding("Debug Undo",90,"Rewind");
        MOD_CONTROLS.put("UNDO", undo);
        KeyBinding redo = new KeyBinding("Debug Redo",82,"Rewind");
        MOD_CONTROLS.put("REDO", redo);
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("rewind", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
