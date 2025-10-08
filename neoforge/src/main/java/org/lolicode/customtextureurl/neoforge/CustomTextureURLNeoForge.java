package org.lolicode.customtextureurl.neoforge;

import net.neoforged.fml.common.Mod;

import net.neoforged.fml.loading.FMLPaths;
import org.lolicode.customtextureurl.ConfigLoader;
import org.lolicode.customtextureurl.CustomTextureURL;

@Mod(CustomTextureURL.MOD_ID)
public final class CustomTextureURLNeoForge {
    public CustomTextureURLNeoForge() {
        // Run our common setup.
        CustomTextureURL.init();

        ConfigLoader.load(FMLPaths.CONFIGDIR.get());
    }
}
