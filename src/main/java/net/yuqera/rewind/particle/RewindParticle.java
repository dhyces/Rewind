package net.yuqera.rewind.particle;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RewindParticle extends SimpleAnimatedParticle {

	protected RewindParticle(ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, IAnimatedSprite spriteWithAge) {
		super(world, x, y, z, spriteWithAge, -0.01F);
		setMaxAge(7);
		this.motionX = xSpeed;
		this.motionY = ySpeed;
		this.motionZ = zSpeed;
		this.setBaseAirFriction(0.6F);
	}
	
	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Factory implements IParticleFactory<BasicParticleType> {

		private final IAnimatedSprite spriteSet;
		
		public Factory(IAnimatedSprite spriteSetIn) {
			this.spriteSet = spriteSetIn;
		}
		
		@Override
		public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z,
				double xSpeed, double ySpeed, double zSpeed) {
			RewindParticle particle = new RewindParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
			particle.selectSpriteWithAge(this.spriteSet);
			return particle;
		}
		
	}

}
