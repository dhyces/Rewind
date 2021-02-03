package net.yuqera.rewind.setup;

import java.util.function.Supplier;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.yuqera.rewind.tileentity.UnwoundJukeboxTileEntity;

public class ModTiles {

	public static final RegistryObject<TileEntityType<?>> UNWOUND_JUKEBOX_TILE;
	
	static void register() {}
	
	private static <T extends TileEntityType<?>> RegistryObject<TileEntityType<?>> register(String name, Supplier<T> item) {
        return Registration.TILE_ENTITIES.register(name, item);
    }
	
	static {
		UNWOUND_JUKEBOX_TILE = register("unwound_jukebox_tile", () -> TileEntityType.Builder.create(UnwoundJukeboxTileEntity::new, ModBlocks.UNWOUND_JUKEBOX.get()).build(null));
	}
}
