package com.example.ctbs.state;

import com.google.gson.JsonParser;

import java.util.Map;
import java.util.UUID;

/** Pure validation for the persistent Pokémon storage ledger. */
public final class CTBSPokemonStorageRules {
    public static final int MAX_PAYLOAD_LENGTH = 2_000_000;

    private CTBSPokemonStorageRules() {}

    public static boolean canStore(Map<String, String> stored, UUID pokemonId, String payload, int capacity) {
        if (stored == null || pokemonId == null || capacity < 0 || capacity > 100000) return false;
        if (!isValidPayload(payload)) return false;
        return !stored.containsKey(pokemonId.toString()) && stored.size() < capacity;
    }

    /** Only JSON objects are accepted as serialized Pokémon records. */
    public static boolean isValidPayload(String payload) {
        if (payload == null || payload.isBlank() || payload.length() > MAX_PAYLOAD_LENGTH) return false;
        try {
            return JsonParser.parseString(payload).isJsonObject();
        } catch (RuntimeException invalidJson) {
            return false;
        }
    }
}
