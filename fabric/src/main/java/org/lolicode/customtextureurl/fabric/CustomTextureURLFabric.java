package org.lolicode.customtextureurl.fabric;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.lolicode.customtextureurl.ConfigLoader;
import org.lolicode.customtextureurl.CustomTextureURL;

public final class CustomTextureURLFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        CustomTextureURL.init();

        ConfigLoader.load(FabricLoader.getInstance().getConfigDir());
    }
}
