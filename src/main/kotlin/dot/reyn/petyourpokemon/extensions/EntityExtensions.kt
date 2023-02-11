package dot.reyn.petyourpokemon.extensions

import net.minecraft.entity.LivingEntity
import net.minecraft.particle.ParticleEffect
import net.minecraft.server.world.ServerWorld

/**
 * Shows some particle effects on the entity.
 * @param particleEffect The particle effect to show.
 */
fun LivingEntity.showEmoteParticle(particleEffect: ParticleEffect) {
    for (i in 0..6) {
        val d: Double = this.random.nextGaussian() * 0.02
        val e: Double = this.random.nextGaussian() * 0.02
        val f: Double = this.random.nextGaussian() * 0.02

        if (this.world is ServerWorld) {
            (this.world as ServerWorld).spawnParticles(particleEffect, this.getParticleX(1.0), this.randomBodyY + 0.5, this.getParticleZ(1.0), 1, d, e, f, 0.2)
        } else {
            this.world.addParticle(particleEffect, this.getParticleX(1.0), this.randomBodyY + 0.5, this.getParticleZ(1.0), d, e, f)
        }
    }
}