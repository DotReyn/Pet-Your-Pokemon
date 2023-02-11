package dot.reyn.petyourpokemon.properties

import com.cobblemon.mod.common.api.pokemon.feature.SpeciesFeatureProvider
import com.cobblemon.mod.common.pokemon.Pokemon

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

}