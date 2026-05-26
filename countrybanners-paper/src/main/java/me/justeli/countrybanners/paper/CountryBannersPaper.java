package me.justeli.countrybanners.paper;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import me.justeli.countrybanners.Core;
import me.justeli.countrybanners.api.ItemStackApi;
import me.justeli.countrybanners.api.PluginAttributes;
import me.justeli.countrybanners.paper.command.CBannersCommand;
import me.justeli.countrybanners.paper.implement.ItemStackImplement;
import me.justeli.countrybanners.paper.implement.PluginAttributesImplement;

import java.util.Collection;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class CountryBannersPaper extends Core {
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

    public void registerCommand(LiteralCommandNode<CommandSourceStack> node, String description, Collection<String> aliases) {
        getLifecycleManager().registerEventHandler(
            LifecycleEvents.COMMANDS,
            event -> event.registrar().register(node, description, aliases)
        );
    }
}
