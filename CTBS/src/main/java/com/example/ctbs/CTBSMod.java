package com.example.ctbs;

import com.example.ctbs.command.CTBSCommands;
import com.example.ctbs.network.CTBSNetworking;
import com.example.ctbs.state.CTBSPlayerState;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CTBSMod implements ModInitializer {
    public static final String MOD_ID = "ctbs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        CTBSPlayerState.load();
        CTBSCommands.register();
        CTBSNetworking.register();
        LOGGER.info("CTBS persistent player settings initialized.");
    }
}
