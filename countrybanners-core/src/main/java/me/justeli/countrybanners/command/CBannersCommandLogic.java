package me.justeli.countrybanners.command;

import me.justeli.countrybanners.Core;
import me.justeli.countrybanners.metrics.ReleaseVersion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * @author Eli
 * @since May 25, 2026
 */
public abstract class CBannersCommandLogic {
    private final Core core;
    public CBannersCommandLogic(Core core) {
        this.core = core;
    }

    protected static final String PERMISSION = "cbanners.manage";

    public abstract void sendMessage(CommandSender sender, String message);

    public void executeReload(CommandSender sender) {
        long millis = System.currentTimeMillis();

        core.getSectionLogger().clearWarnings();
        core.getMainConfig().parseAndReload();

        // reloading queue
        core.getBannerResolver().queueParse();
        core.getMaxmindHandler().queueClose();
        core.getMaxmindHandler().queueLogin();
        core.getVersionHandler().queueCheck();

        long duration = System.currentTimeMillis() - millis;
        sendMessage(sender, "<yellow>Config of CountryBanners has been reloaded in %,dms.".formatted(duration));

        var amount = core.getSectionLogger().getWarnings();
        if (amount != 0) {
            sendMessage(sender, "<red>Reload complete with %,d warnings. See console for details.".formatted(amount));
        }
    }

    public void executeGive(CommandSender sender, String countryCode) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "<red>This command can only be performed by in-game players.");
            return;
        }

        Optional<ItemStack> banner = core.getBannerResolver().getBanner(countryCode.toUpperCase());
        if (banner.isEmpty()) {
            sendMessage(sender, "<red>There is no registered banner for country code '%s'.".formatted(countryCode));
            return;
        }

        player.getInventory().addItem(banner.get());
        sendMessage(sender, "<yellow>Added a banner for this country code to your inventory.");
    }

    public void executeRegister(CommandSender sender, String countryCode, String countryName) {
        if (!(sender instanceof Player player)) {
            sendMessage(sender, "<red>This command can only be performed by in-game players.");
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (core.getBannerResolver().registerBanner(item, countryCode, countryName)) {
            sendMessage(sender, """
                <yellow>Added banner as '%s.json' to the banners folder. Consider submitting a pull request on GitHub to make \
                it available for all servers: <blue>https://github.com/justEli/CountryBanners/tree/main/banners"""
                .formatted(countryCode.toUpperCase())
            );
        }
        else {
            sendMessage(sender, "<red>Failed to add banner to the banners folder.");
        }
    }

    public void executeVersion(CommandSender sender) {
        core.getVersionHandler().queueCheck();

        Optional<ReleaseVersion> latest = core.getVersionHandler().getLatestVersion();
        String current = core.getAttributes().getVersion();

        sendMessage(sender, "<yellow>Version currently installed: <white>" + current);
        latest.ifPresent(version -> sendMessage(sender, "<yellow>Latest version available: <white>" + version.tag()));
        sendMessage(sender, "<yellow>Project page: <blue>" + core.getAttributes().getUrl());
    }
}
