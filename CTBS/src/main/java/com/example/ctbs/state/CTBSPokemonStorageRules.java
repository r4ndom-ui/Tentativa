package com.example.ctbs.state;

import java.util.Map;
import java.util.UUID;

/** Pure validation for the persistent Pokémon storage ledger. */
public final class CTBSPokemonStorageRules {
    private CTBSPokemonStorageRules() {}

    public static boolean canStore(Map<String, String> stored, UUID pokemonId, String payload, int capacity) {
        if (stored == null || pokemonId == null || payload == null || payload.isBlank()) return false;
        if (payload.length() > 2_000_000 || capacity < 0 || capacity > 100000) return false;
        return !stored.containsKey(pokemonId.toString()) && stored.size() < capacity;
    }
}
