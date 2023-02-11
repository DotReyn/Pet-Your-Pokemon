package dot.reyn.petyourpokemon.properties

import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeature
import com.google.gson.JsonObject
import net.minecraft.nbt.NbtCompound
import java.time.Instant

/**
 * A property that is applied to a Pokémon containing petting data.
 */
class PettedFeature(
    var nextPettableTime: Instant? = null,
    var timesPetted: Int = 0,
    override val name: String = "petted"
) : SpeciesFeature {

    /**
     * Loads the petted data from JSON.
     * @param pokemonJSON The JSON to load from.
     */
    override fun loadFromJSON(pokemonJSON: JsonObject): SpeciesFeature {
        var lastPetted: Instant? = null
        if (pokemonJSON.has("nextPettableTime")) {
            lastPetted = Instant.ofEpochSecond(pokemonJSON.get("nextPettableTime").asLong)
        }

        var timesPetted = 0
        if (pokemonJSON.has("timesPetted")) {
            timesPetted = pokemonJSON.get("timesPetted").asInt
        }
        return PettedFeature(lastPetted, timesPetted)
    }

    /**
     * Loads the petted data from NBT.
     * @param pokemonNBT The NBT to load from.
     */
    override fun loadFromNBT(pokemonNBT: NbtCompound): SpeciesFeature {
        if (pokemonNBT.contains("nextPettableTime")) {
            nextPettableTime = Instant.ofEpochSecond(pokemonNBT.getLong("nextPettableTime"))
        }
        val timesPetted = pokemonNBT.getInt("timesPetted")
        return PettedFeature(nextPettableTime, timesPetted)
    }

    /**
     * Saves the petted data to JSON.
     * @param pokemonJSON The JSON to save to.
     */
    override fun saveToJSON(pokemonJSON: JsonObject): JsonObject {
        if (this.nextPettableTime != null) {
            pokemonJSON.addProperty("nextPettableTime", this.nextPettableTime!!.epochSecond)
        }
        pokemonJSON.addProperty("timesPetted", this.timesPetted)
        return pokemonJSON
    }

    /**
     * Saves the petted data to NBT.
     * @param pokemonNBT The NBT to save to.
     */
    override fun saveToNBT(pokemonNBT: NbtCompound): NbtCompound {
        if (this.nextPettableTime != null) {
            pokemonNBT.putLong("nextPettableTime", this.nextPettableTime!!.epochSecond)
        }
        pokemonNBT.putInt("timesPetted", this.timesPetted)
        return pokemonNBT
    }

    /**
     * Returns whether the Pokémon is currently pettable..
     */
    fun canPetAgain(): Boolean {
        if (this.nextPettableTime == null) {
            return true
        }
        return Instant.now().isAfter(this.nextPettableTime)
    }

}