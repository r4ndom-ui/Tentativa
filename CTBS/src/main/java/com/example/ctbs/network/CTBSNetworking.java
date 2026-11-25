package com.example.ctbs.network;

import com.example.ctbs.state.CTBSPlayerState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** Synchronizes a bounded, read-only view of the player's stored Pokémon IDs. */
public final class CTBSNetworking {
    private static final Map<UUID, String> LAST_SENT = new HashMap<>();
    private static int tickCounter;

    private CTBSNetworking() {}

    public static void register() {
        PayloadTypeRegistry.playS2C().register(
            StomachContentsPayload.ID, StomachContentsPayload.CODEC
        );
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            LAST_SENT.remove(handler.player.getUuid());
            sendIfChanged(handler.player, true);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
            LAST_SENT.remove(handler.player.getUuid())
        );
        ServerTickEvents.END_SERVER_TICK.register(CTBSNetworking::refreshOnlinePlayers);
    }

    private static void refreshOnlinePlayers(MinecraftServer server) {
        if (++tickCounter < 40) return;
        tickCounter = 0;
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            sendIfChanged(player, false);
        }
    }

    private static void sendIfChanged(ServerPlayerEntity player, boolean force) {
        String ids = CTBSPlayerState.getStoredPokemon(player.getUuid()).keySet().stream()
            .sorted()
            .limit(200)
            .collect(Collectors.joining("\n"));
        if (ids.length() > 32000) ids = ids.substring(0, 32000);
        if (force || !ids.equals(LAST_SENT.get(player.getUuid()))) {
            ServerPlayNetworking.send(player, new StomachContentsPayload(ids));
            LAST_SENT.put(player.getUuid(), ids);
        }
    }
}
