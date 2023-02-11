package dot.reyn.petyourpokemon

import com.cobblemon.mod.common.api.pokemon.feature.GlobalSpeciesFeatures
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import com.google.gson.GsonBuilder
import dot.reyn.petyourpokemon.config.PetYourPokemonConfig
import dot.reyn.petyourpokemon.properties.PettedFeature
import dot.reyn.petyourpokemon.properties.PettedFeatureProvider
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import net.minecraft.util.Formatting
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.time.Instant
import java.util.*

/**
 * Pet Your Pokémon sidemod for Cobblemon.
 */
class PetYourPokemon : ModInitializer {

    private lateinit var config: PetYourPokemonConfig
    private val pettingAnimations = mutableListOf<PettingAnimation>()
    private val interactCooldowns = mutableMapOf<UUID, Instant>()

    /**
     * Initializes the mod.
     */
    override fun onInitialize() {
        this.loadConfig()

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register { _, _, -> this.loadConfig() }
        GlobalSpeciesFeatures.register("petted", PettedFeatureProvider())

        UseEntityCallback.EVENT.register { player, world, _, entity, _ ->
            // Ignore if any of these conditions are true
            if (world.isClient || entity !is PokemonEntity || !player.isSneaking || entity.ownerUuid != player.uuid) {
                return@register ActionResult.PASS;
            }

            // This event fires very often, so simple cooldown to relax on the spam
            if (this.interactCooldowns.containsKey(player.uuid) && Instant.now().isBefore(this.interactCooldowns[player.uuid]!!)) {
                return@register ActionResult.PASS
            }
            this.interactCooldowns[player.uuid] = Instant.now().plusSeconds(1)

            val property = entity.pokemon.getFeature<PettedFeature>("petted")

            // If they can be pet again, pet them :)
            if (property != null && property.canPetAgain()) {
                property.nextPettableTime = Instant.now().plusSeconds(this.config.cooldown)
                property.timesPetted++
                this.pettingAnimations.add(PettingAnimation(config = this.config, playerId = player.uuid, pokemonId = entity.uuid))
                return@register ActionResult.SUCCESS
            } else {
                player.sendMessage(Text.literal("You've already pet ")
                        .append(entity.pokemon.displayName)
                        .append(" recently.").setStyle(Style.EMPTY.withColor(Formatting.GRAY)), false)
            }
            return@register ActionResult.PASS
        }

        ServerTickEvents.START_SERVER_TICK.register { server ->
            pettingAnimations.forEach { it.handleTick(server) }
            pettingAnimations.removeIf { it.hasFinished() }
        }
    }

    /**
     * Loads the configuration file.
     * If the config does not exist, it will be created.
     */
    private fun loadConfig() {
        val configDir = File("./config/")
        if (!configDir.exists()) {
            configDir.mkdirs()
        }

        val configFile = File(configDir, "petyourpokemon.json")
        val gson = GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create()

        if (!configFile.exists()) {
            this.config = PetYourPokemonConfig()
            val fileWriter = FileWriter(configFile, Charsets.UTF_8)

            gson.toJson(this.config, fileWriter)

            fileWriter.flush()
            fileWriter.close()
        } else {
            val fileReader = FileReader(configFile, Charsets.UTF_8)
            this.config = gson.fromJson(fileReader, PetYourPokemonConfig::class.java)
            fileReader.close()
        }
    }

}