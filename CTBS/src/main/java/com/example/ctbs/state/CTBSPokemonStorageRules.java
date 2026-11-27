package com.example.ctbs.state;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;

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

    /** Returns a safe display species from the stored Cobblemon JSON, or an empty string. */
    public static String displaySpecies(String payload) {
        if (!isValidPayload(payload)) return "";
        try {
            JsonObject root = JsonParser.parseString(payload).getAsJsonObject();
            JsonElement species = root.get("species");
            if (species == null || !species.isJsonPrimitive() || !species.getAsJsonPrimitive().isString()) return "";
            String value = species.getAsString();
            if (value.length() > 64 || !value.matches("[a-zA-Z0-9_:.-]+")) return "";
            return value;
        } catch (RuntimeException invalid) {
            return "";
        }
    }

    /** Returns a valid stored level, or -1 when this payload has no supported level field. */
    public static int displayLevel(String payload) {
        if (!isValidPayload(payload)) return -1;
        try {
            JsonElement level = JsonParser.parseString(payload).getAsJsonObject().get("level");
            if (level == null || !level.isJsonPrimitive() || !level.getAsJsonPrimitive().isNumber()) return -1;
            // Reject fractional and overflowing values instead of silently truncating them.
            int value = level.getAsBigDecimal().intValueExact();
            return value >= 1 && value <= 100 ? value : -1;
        } catch (RuntimeException invalid) {
            return -1;
        }
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
