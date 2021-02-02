package net.yuqera.rewind.setup;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.yuqera.rewind.block.UnwoundJukeboxBlock;

public class ModBlocks {
	public static final RegistryObject<Block> UNWOUND_JUKEBOX;

	static void register() {}
	
	private static <T extends Block> RegistryObject<Block> register(String name, Supplier<T> item) {
        return Registration.BLOCKS.register(name, item);
    }
	
	static {
		UNWOUND_JUKEBOX = register("unwound_jukebox", UnwoundJukeboxBlock::new);
	}
	
}
