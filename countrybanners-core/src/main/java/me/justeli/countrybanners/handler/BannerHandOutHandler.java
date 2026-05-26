package me.justeli.countrybanners.handler;

import me.justeli.countrybanners.Core;
import me.justeli.countrybanners.config.EventType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class BannerHandOutHandler implements Listener {
    private final Core core;
    public BannerHandOutHandler(Core core) {
        this.core = core;
        core.parseEventHandlers(this);
    }

    @EventHandler
    void onPlayerJoinEvent(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (player.hasPlayedBefore()) {
            if (core.getMainConfig().getEnabledEvents().contains(EventType.JOIN)) {
                giveBanner(player);
            }
        }
        else if (core.getMainConfig().getEnabledEvents().contains(EventType.FIRST_JOIN)) {
            giveBanner(player);
        }
    }

    private void giveBanner(Player player) {
        var address = player.getAddress();
        if (address == null) {
            return;
        }

        core.getMaxmindHandler().queueCountryCode(address.getAddress(), code -> {
            if (code.isEmpty()) {
                return;
            }

            Optional<ItemStack> item = core.getBannerResolver().getBanner(code.get());
            if (item.isEmpty()) {
                return;
            }

            var inventory = player.getInventory();
            switch (core.getMainConfig().getHandOutPosition()) {
                case HELMET ->  {
                    if (inventory.getHelmet() == null || inventory.getHelmet().getType() == Material.AIR) {
                        inventory.setHelmet(item.get());
                    }
                    else {
                        inventory.addItem(item.get());
                    }
                }
                case MAIN_HAND -> {
                    if (inventory.getItemInMainHand().getType() == Material.AIR) {
                        inventory.setItemInMainHand(item.get());
                    }
                    else {
                        inventory.addItem(item.get());
                    }
                }
                case OFF_HAND -> {
                    if (inventory.getItemInOffHand().getType() == Material.AIR) {
                        inventory.setItemInOffHand(item.get());
                    }
                    else {
                        inventory.addItem(item.get());
                    }
                }
                case INVENTORY -> inventory.addItem(item.get());
            }
        });
    }
}
