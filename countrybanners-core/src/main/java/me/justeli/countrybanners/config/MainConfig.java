package me.justeli.countrybanners.config;

import me.justeli.countrybanners.Core;
import me.justeli.countrybanners.api.GeoIpClient;
import org.bukkit.inventory.ItemRarity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class MainConfig extends BasicConfig {
    public MainConfig(Core core) {
        super(core, "config.yml");

        // parse when it is initialized
        parseAndReload();
    }

    private String bannerName;
    private ItemRarity bannerRarity = ItemRarity.UNCOMMON;
    private boolean bannerPatternsHide;
    private int maxmindAccountId;
    private String maxmindLicenseKey;
    private final Set<EventType> enabledEvents = new HashSet<>();
    private HandOutPosition handOutPosition = HandOutPosition.HELMET;
    private boolean notifyOnUpdate;

    public String getBannerName() {
        return bannerName;
    }

    public @NotNull ItemRarity getBannerRarity() {
        return bannerRarity;
    }

    public boolean isBannerPatternsHide() {
        return bannerPatternsHide;
    }

    public int getMaxmindAccountId() {
        return maxmindAccountId;
    }

    public String getMaxmindLicenseKey() {
        return maxmindLicenseKey;
    }

    public Set<EventType> getEnabledEvents() {
        return enabledEvents;
    }

    public HandOutPosition getHandOutPosition() {
        return handOutPosition;
    }

    public boolean isNotifyOnUpdate() {
        return notifyOnUpdate;
    }

    @Override
    public void parseAndReload() {
        var config = getOrCreateConfig();

        // banner settings
        this.bannerName = config.getString("banner.name", "Flag of {country}");

        String rawRarity = config.getString("banner.rarity");
        Optional<ItemRarity> rarity = getEnum(ItemRarity.class, rawRarity);
        if (rarity.isPresent()) {
            this.bannerRarity = rarity.get();
        }
        else {
            logger.warn("Found an invalid %s '%s' at `%s`.".formatted(
                "item rarity type", rawRarity, "banner.rarity"
            ));
        }

        this.bannerPatternsHide = config.getBoolean("banner.patterns.hide", true);

        // maxmind
        this.maxmindAccountId = config.getInt("maxmind.account-id");
        this.maxmindLicenseKey = config.getString("maxmind.license-key");

        // enabled events
        List<String> events = config.getStringList("enabled-events");
        enabledEvents.clear();
        for (String event : events) {
            var eventType = getEnum(EventType.class, event);
            if (eventType.isEmpty()) {
                logger.warn("Found an invalid %s '%s' at `%s`.".formatted(
                    "event type", event, "enabled-events"
                ));
                continue;
            }
            enabledEvents.add(eventType.get());
        }

        // hand out position
        String rawPosition = config.getString("hand-out-position");
        Optional<HandOutPosition> position = getEnum(HandOutPosition.class, rawPosition);
        if (position.isPresent()) {
            this.handOutPosition = position.get();
        }
        else {
            logger.warn("Found an invalid %s '%s' at `%s`.".formatted(
                "hand out position", rawPosition, "hand-out-position"
            ));
        }

        // notify on update
        this.notifyOnUpdate = config.getBoolean("notify-on-update", true);
    }
}
