package com.example.ctbs.client;

import net.fabricmc.api.ClientModInitializer;
import com.example.ctbs.network.StomachContentsPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import java.util.List;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class CTBSClient implements ClientModInitializer {
    private static KeyBinding openMenu;
    private static volatile List<String> storedPokemonIds = List.of();

    public static List<String> getStoredPokemonIds() { return storedPokemonIds; }

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(StomachContentsPayload.ID, (payload, context) -> {
            String value = payload.recordIds();
            storedPokemonIds = value.isBlank() ? List.of() : value.lines().filter(line -> !line.isBlank()).limit(200).toList();
        });
        openMenu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.ctbs.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "category.ctbs"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenu.wasPressed()) {
                if (client.player != null) client.setScreen(new CTBSMenuScreen());
            }
        });
    }
}
