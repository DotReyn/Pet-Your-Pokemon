package dot.reyn.petyourpokemon

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import dot.reyn.petyourpokemon.config.PetYourPokemonConfig
import dot.reyn.petyourpokemon.extensions.showEmoteParticle
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.MinecraftServer
import net.minecraft.text.Style
import net.minecraft.text.Text
import java.util.UUID

/**
 * Simple tick based animation task for petting a Pokémon.
 */
class PettingAnimation(
    val config: PetYourPokemonConfig,
    private var ticksLeft: Int = 30,
    val playerId: UUID,
    val pokemonId: UUID,
) {

    /**
     * Handles a tick of the animation.
     * @param server The server instance.
     */
    fun handleTick(server: MinecraftServer) {
        if (this.hasFinished()) {
            return
        }

        val player = server.playerManager.getPlayer(this.playerId)
        if (player == null) {
            this.ticksLeft = 0
            return
        }

        // Send hand swing packet every 10 ticks
        if (this.ticksLeft % 10 == 0) {
            server.playerManager.sendToAll(EntityAnimationS2CPacket(player, 0))
        }

        if (this.ticksLeft > 0) {
            this.ticksLeft--
        }

        // When the animation has finished, increase friendship and show a heart particle
        if (this.hasFinished()) {
            val entity = player.getWorld().getEntity(this.pokemonId)

            if (entity != null && entity is PokemonEntity) {
                if (this.config.awardFriendship) {
                    entity.pokemon.incrementFriendship(5)
                }

                if (this.config.showMessage) {
                    player.sendMessage(
                        Text.literal("Your friendship with ")
                            .append(entity.pokemon.displayName)
                            .append(" has grown! ❤").setStyle(Style.EMPTY.withColor(0xea999c)), false)
                }

                entity.showEmoteParticle(ParticleTypes.HEART)
            }
        }
    }

    /**
     * Returns whether the animation has finished.
     */
    fun hasFinished(): Boolean {
        return this.ticksLeft == 0
    }

}