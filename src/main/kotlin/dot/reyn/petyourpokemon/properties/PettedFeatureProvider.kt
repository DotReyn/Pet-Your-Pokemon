package dot.reyn.petyourpokemon.properties

import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatureProvider
import com.cobblemon.mod.common.pokemon.Pokemon
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import java.time.Instant

/**
 * Provides a [PettedFeature] for a Pokémon.
 */
class PettedFeatureProvider() : SpeciesFeatureProvider<PettedFeature> {

    /**
     * Returns a new [PettedFeature] instance.
     * @param pokemon The Pokémon to create the feature for.
     */
    override fun invoke(pokemon: Pokemon): PettedFeature {
        return PettedFeature()
    }

    override fun invoke(json: JsonObject): PettedFeature {
        var lastPetted: Instant? = null
        if (json.has("nextPettableTime")) {
            lastPetted = Instant.ofEpochSecond(json.get("nextPettableTime").asLong)
        }

        var timesPetted = 0
        if (json.has("timesPetted")) {
            timesPetted = json.get("timesPetted").asInt
        }
        return PettedFeature(lastPetted, timesPetted)
    }

    override fun invoke(nbt: NbtCompound): PettedFeature {
        var nextPettableTime: Instant? = null
        if (nbt.contains("nextPettableTime")) {
            nextPettableTime = Instant.ofEpochSecond(nbt.getLong("nextPettableTime"))
        }
        val timesPetted = nbt.getInt("timesPetted")
        return PettedFeature(nextPettableTime, timesPetted)
    }

}