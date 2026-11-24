package com.example.ctbs;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CTBSMod implements ModInitializer {
    public static final String MOD_ID = "ctbs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("CTBS experimental systems initialized.");
    }
}
