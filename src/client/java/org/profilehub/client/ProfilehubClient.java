package org.profilehub.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfilehubClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(ProfilehubClient.class.getName());
    private boolean initscreen = false;
    private int ticks = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!initscreen) {
                ticks++;
                if (ticks >= 40) {
                    initscreen = true;
                    client.setScreen(new InitScreen());
                }
            }
        });

        LOGGER.info("Profilehub loaded");
    }
}