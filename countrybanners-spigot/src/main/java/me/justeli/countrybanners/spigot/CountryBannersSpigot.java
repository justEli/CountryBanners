package me.justeli.countrybanners.spigot;

import me.justeli.countrybanners.Core;
import me.justeli.countrybanners.api.ItemStackApi;
import me.justeli.countrybanners.api.PluginAttributes;
import me.justeli.countrybanners.spigot.command.CBannersCommand;
import me.justeli.countrybanners.spigot.implement.ItemStackImplement;
import me.justeli.countrybanners.spigot.implement.PluginAttributesImplement;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class CountryBannersSpigot extends Core {
    private ItemStackApi itemStackApi;
    private PluginAttributes pluginAttributes;

    @Override
    public void loadImplementations() {
        this.itemStackApi = new ItemStackImplement();
        this.pluginAttributes = new PluginAttributesImplement(this);
    }

    @Override
    public void loadCommands() {
        new CBannersCommand(this);
    }

    @Override
    public ItemStackApi getItemStackApi() {
        return itemStackApi;
    }

    @Override
    public PluginAttributes getAttributes() {
        return pluginAttributes;
    }
}
