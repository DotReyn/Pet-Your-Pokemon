package dot.reyn.petyourpokemon.config

/**
 * Configuration for Pet Your Pokémon.
 */
data class PetYourPokemonConfig(
    val awardFriendship: Boolean = true,
    val showMessage: Boolean = true,
    val cooldown: Long = 10 * 60
)
