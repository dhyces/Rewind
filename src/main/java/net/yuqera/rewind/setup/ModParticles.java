package net.yuqera.rewind.setup;

import java.util.function.Supplier;

import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.fml.RegistryObject;

public class ModParticles {

	public static final RegistryObject<BasicParticleType> REWIND_PARTICLE;
	
	static void register() {}
	
	private static <T extends BasicParticleType> RegistryObject<BasicParticleType> register(String name, Supplier<T> item) {
        return Registration.PARTICLES.register(name, item);
    }
	
	static {
		REWIND_PARTICLE = register("rewind", () -> new BasicParticleType(false));
	}
	
}
