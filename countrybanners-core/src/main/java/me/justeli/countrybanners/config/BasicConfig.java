package me.justeli.countrybanners.config;

import me.justeli.countrybanners.Core;
import org.bukkit.configuration.file.YamlConfiguration;

import java.nio.file.Files;
import java.util.Optional;

/**
 * @author Eli
 * @since April 30, 2026
 */
public abstract class BasicConfig {
    protected final Core core;
    protected final String fileName;
    protected final SectionLogger.Named logger;

    public BasicConfig(Core core, String fileName) {
        this.core = core;
        this.fileName = fileName;
        this.logger = core.getSectionLogger().create(fileName);
    }

    public YamlConfiguration getOrCreateConfig() {
        var configFile = core.getDataFolder().toPath().resolve(fileName);
        if (!Files.exists(configFile)) {
            core.saveResource(fileName, false);
        }

        return YamlConfiguration.loadConfiguration(configFile.toFile());
    }

    public static <T extends Enum<T>> Optional<T> getEnum(Class<T> type, String value) {
        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of(Enum.valueOf(type, value.toUpperCase().replace(" ", "_")));
        }
        catch (IllegalArgumentException | NullPointerException exception) {
            return Optional.empty();
        }
    }

    public abstract void parseAndReload();
}
