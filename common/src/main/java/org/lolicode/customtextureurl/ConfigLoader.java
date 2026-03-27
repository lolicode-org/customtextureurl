package org.lolicode.customtextureurl;

import com.google.gson.Gson;

import static org.lolicode.customtextureurl.CustomTextureURL.LOGGER;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;

public class ConfigLoader {
    public record Config(boolean skipSignatureCheck, boolean allowAnyDomain, List<String> allowedDomains, List<String> blockedDomains) {
        static Config defaultConfig() {
            return new Config(
                    true,
                    false,
                    List.of(),
                    List.of()
            );
        }
    }

    private static final String CONFIG_FILE_NAME = CustomTextureURL.MOD_ID + ".json";
    private static final Gson GSON = new Gson().newBuilder().setPrettyPrinting().create();

    public static void load(Path parentDir) {
        Path configFile = parentDir.resolve(CONFIG_FILE_NAME);
        if (configFile.toFile().exists()) {
            try {
                CustomTextureURL.CONFIG = GSON.fromJson(Files.readString(configFile), Config.class);
            } catch (Exception e) {
                LOGGER.error("Failed to load config file: {}", configFile, e);
                CustomTextureURL.CONFIG =  Config.defaultConfig();
            }
        } else {
            Config defaultConfig = Config.defaultConfig();
            try {
                Files.writeString(configFile, GSON.toJson(defaultConfig));
            } catch (Exception e) {
                LOGGER.error("Failed to write default config file: {}", configFile, e);
            }
            CustomTextureURL.CONFIG =  defaultConfig;
        }
    }
}
