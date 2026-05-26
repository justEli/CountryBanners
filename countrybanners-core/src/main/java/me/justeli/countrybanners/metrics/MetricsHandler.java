package me.justeli.countrybanners.metrics;

import me.justeli.countrybanners.Core;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class MetricsHandler {
    public MetricsHandler(Core core) {
        Metrics metrics = new Metrics(core, 31560);

        var config = core.getMainConfig();
        metrics.addCustomChart(new SimplePie("bannerName", config::getBannerName));
        metrics.addCustomChart(new SimplePie("bannerRarity", () -> config.getBannerRarity().name().toLowerCase()));
        metrics.addCustomChart(new SimplePie("bannerPatternsHide", () -> Boolean.toString(config.isBannerPatternsHide())));

        metrics.addCustomChart(new SimplePie("usingMaxmind", () -> Boolean.toString(config.getMaxmindAccountId() > 0)));

        metrics.addCustomChart(new SimplePie("enabledEvents", () -> Integer.toString(config.getEnabledEvents().size())));
        metrics.addCustomChart(new SimplePie("handOutPosition", () -> config.getHandOutPosition().name().toLowerCase()));

        metrics.addCustomChart(new SimplePie("notifyOnUpdate", () -> Boolean.toString(config.isNotifyOnUpdate())));

        metrics.addCustomChart(new SingleLineChart("totalBannersHandedOut", () -> totalBannersHandedOut.getAndSet(0)));
    }

    private final AtomicInteger totalBannersHandedOut = new AtomicInteger(0);

    public void registerBannerHandOut(int amount) {
        totalBannersHandedOut.addAndGet(amount);
    }
}
