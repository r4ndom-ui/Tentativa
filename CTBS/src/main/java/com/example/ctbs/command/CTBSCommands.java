package com.example.ctbs.command;

import com.example.ctbs.state.CTBSPlayerState;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class CTBSCommands {
    private CTBSCommands() {}

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
            dispatcher.register(CommandManager.literal("ctbs")
                .then(CommandManager.literal("capacity")
                    .then(CommandManager.literal("get")
                        .then(CommandManager.argument("species", StringArgumentType.word())
                            .executes(context -> {
                                ServerPlayerEntity player = requirePlayer(context.getSource());
                                if (player == null) return 0;
                                String species = StringArgumentType.getString(context, "species");
                                int value = CTBSPlayerState.getCapacity(player.getUuid(), species);
                                int base = CTBSPlayerState.getBaseCapacity(species);
                                boolean custom = CTBSPlayerState.hasCapacityOverride(player.getUuid(), species);
                                context.getSource().sendFeedback(() -> Text.literal(species + " capacity: " + value + " (base: " + base + ", " + (custom ? "custom" : "default") + ")"), false);
                                return 1;
                            })))
                    .then(CommandManager.literal("reset")
                        .then(CommandManager.argument("species", StringArgumentType.word())
                            .executes(context -> {
                                ServerPlayerEntity player = requirePlayer(context.getSource());
                                if (player == null) return 0;
                                String species = StringArgumentType.getString(context, "species");
                                CTBSPlayerState.resetCapacity(player.getUuid(), species);
                                int base = CTBSPlayerState.getBaseCapacity(species);
                                context.getSource().sendFeedback(() -> Text.literal("Reset " + species + " capacity to base value: " + base), false);
                                return 1;
                            })))
                    .then(CommandManager.literal("set")
                        .then(CommandManager.argument("species", StringArgumentType.word())
                            .then(CommandManager.argument("value", IntegerArgumentType.integer(0, 100000))
                                .executes(context -> {
                                    ServerPlayerEntity player = requirePlayer(context.getSource());
                                    if (player == null) return 0;
                                    String species = StringArgumentType.getString(context, "species");
                                    int value = IntegerArgumentType.getInteger(context, "value");
                                    CTBSPlayerState.setCapacity(player.getUuid(), species, value);
                                    context.getSource().sendFeedback(() -> Text.literal("Saved " + species + " capacity: " + value), false);
                                    return 1;
                                })))))
                .then(CommandManager.literal("stomach")
                    .then(CommandManager.literal("status").executes(context -> {
                        ServerPlayerEntity player = requirePlayer(context.getSource());
                        if (player == null) return 0;
                        CTBSPlayerState.PlayerData data = CTBSPlayerState.get(player.getUuid());
                        context.getSource().sendFeedback(() -> Text.literal(
                            "Stomach mode: " + data.getMode() + " | posture: " + data.getPosture()
                                + " | digestion multiplier: x" + data.getDigestionMultiplier()), false);
                        return 1;
                    }))
                    .then(CommandManager.literal("mode")
                        .then(CommandManager.literal("hold").executes(c -> setMode(c.getSource(), CTBSPlayerState.StomachMode.HOLD)))
                        .then(CommandManager.literal("heal").executes(c -> setMode(c.getSource(), CTBSPlayerState.StomachMode.HEAL)))
                        .then(CommandManager.literal("digest").executes(c -> setMode(c.getSource(), CTBSPlayerState.StomachMode.DIGEST)))))
                .then(CommandManager.literal("action")
                    .then(CommandManager.literal("stand").executes(c -> setPosture(c.getSource(), CTBSPlayerState.Posture.STAND)))
                    .then(CommandManager.literal("sit").executes(c -> setPosture(c.getSource(), CTBSPlayerState.Posture.SIT)))
                    .then(CommandManager.literal("lie").executes(c -> setPosture(c.getSource(), CTBSPlayerState.Posture.LIE)))
                    .then(CommandManager.literal("nap").executes(c -> setPosture(c.getSource(), CTBSPlayerState.Posture.NAP))))
            )
        );
    }

    private static int setMode(ServerCommandSource source, CTBSPlayerState.StomachMode mode) {
        ServerPlayerEntity player = requirePlayer(source);
        if (player == null) return 0;
        CTBSPlayerState.setMode(player.getUuid(), mode);
        source.sendFeedback(() -> Text.literal("Stomach mode saved: " + mode), false);
        return 1;
    }

    private static int setPosture(ServerCommandSource source, CTBSPlayerState.Posture posture) {
        ServerPlayerEntity player = requirePlayer(source);
        if (player == null) return 0;
        CTBSPlayerState.setPosture(player.getUuid(), posture);
        float multiplier = CTBSPlayerState.digestionMultiplier(posture);
        source.sendFeedback(() -> Text.literal("Posture saved: " + posture + " (digestion multiplier x" + multiplier + ")"), false);
        return 1;
    }

    private static ServerPlayerEntity requirePlayer(ServerCommandSource source) {
        if (source.getEntity() instanceof ServerPlayerEntity player) return player;
        source.sendError(Text.literal("This command can only be used by a player."));
        return null;
    }
}
