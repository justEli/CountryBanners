package me.justeli.countrybanners.spigot.implement;

import me.justeli.countrybanners.Core;
import me.justeli.countrybanners.api.PluginAttributes;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class PluginAttributesImplement implements PluginAttributes {
    private final Core core;
    public PluginAttributesImplement(Core core) {
        this.core = core;
    }

    @Override
    public String getVersion() {
        return core.getDescription().getVersion();
    }

    @Override
    public String getUrl() {
        var website = core.getDescription().getWebsite();
        return website == null? "" : website;
    }

    @Override
    public String getDescription() {
        var description = core.getDescription().getDescription();
        return description == null? "" : description;
    }
}
