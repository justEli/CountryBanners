package me.justeli.countrybanners.spigot.command;

import me.justeli.countrybanners.spigot.CountryBannersSpigot;

import java.util.logging.Level;

/**
 * @author Eli
 * @since May 26, 2026
 */
public final class CBannersCommand {
    public CBannersCommand(CountryBannersSpigot plugin) {
        plugin.getLogger().log(Level.INFO,
            "Command /cbanners is currently not supported on Spigot. Consider upgrading your server software to Paper."
        );
    }
}
