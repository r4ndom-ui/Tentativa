package com.example.ctbs.compat;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.RegistryAccess;

/**
 * Serializes complete Cobblemon Pokemon data using Cobblemon's own codec-backed
 * JSON methods and the active world's registry access.
 */
public final class CTBSCobblemonCodec {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private CTBSCobblemonCodec() {}

    public static String encode(Pokemon pokemon, RegistryAccess registryAccess) {
        if (pokemon == null || registryAccess == null) {
            throw new IllegalArgumentException("Pokemon and registry access are required");
        }
        JsonObject data = pokemon.saveToJSON(registryAccess, new JsonObject());
        return GSON.toJson(data);
    }

    public static Pokemon decode(String serialized, RegistryAccess registryAccess) {
        if (serialized == null || serialized.isBlank() || registryAccess == null) {
            throw new IllegalArgumentException("Serialized Pokemon data and registry access are required");
        }
        JsonObject data = JsonParser.parseString(serialized).getAsJsonObject();
        Pokemon pokemon = new Pokemon();
        pokemon.loadFromJSON(registryAccess, data);
        return pokemon;
    }
}
