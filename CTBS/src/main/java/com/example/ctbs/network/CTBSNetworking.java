package com.example.ctbs.network;

import com.example.ctbs.state.CTBSPlayerState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.stream.Collectors;

/** Synchronizes a bounded, read-only view of the player's stored Pokémon IDs. */
public final class CTBSNetworking {
    private CTBSNetworking() {}

    public static void register() {
        PayloadTypeRegistry.playS2C().register(
            StomachContentsPayload.ID, StomachContentsPayload.CODEC
        );
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
            sendStomachContents(handler.player)
        );
    }

    private static void sendStomachContents(ServerPlayerEntity player) {
        String ids = CTBSPlayerState.getStoredPokemon(player.getUuid()).keySet().stream()
            .sorted()
            .limit(200)
            .collect(Collectors.joining("\n"));
        if (ids.length() > 32000) {
            ids = ids.substring(0, 32000);
        }
        ServerPlayNetworking.send(player, new StomachContentsPayload(ids));
    }
}
