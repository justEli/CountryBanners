package me.justeli.countrybanners;

import me.justeli.countrybanners.api.ItemStackApi;
import me.justeli.countrybanners.api.PluginAttributes;
import me.justeli.countrybanners.config.BannerResolver;
import me.justeli.countrybanners.config.SectionLogger;
import me.justeli.countrybanners.config.MainConfig;
import me.justeli.countrybanners.handler.BannerHandOutHandler;
import me.justeli.countrybanners.handler.MaxmindHandler;
import me.justeli.countrybanners.metrics.MetricsHandler;
import me.justeli.countrybanners.metrics.VersionHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Eli
 * @since May 25, 2026
 */
public abstract class Core extends JavaPlugin {
    @Override
    public void onEnable() {
        loadImplementations();

        this.sectionLogger = new SectionLogger(this);
        this.mainConfig = new MainConfig(this);

        loadCommands();

        this.bannerResolver = new BannerResolver(this);
        this.maxmindHandler = new MaxmindHandler(this);
        this.versionHandler = new VersionHandler(this);

        new BannerHandOutHandler(this);

        this.metricsHandler = new MetricsHandler(this);
    }

    @Override
    public void onDisable() {
        for (Runnable task : shutdownTasks.reversed()) {
            try {
                task.run();
            }
            catch (Exception ignored) {}
        }
    }

    public static final String REPOSITORY = "justEli/CountryBanners";

    // the thread to queue MaxMind login, banner downloads, and banner parsing
    private final ExecutorService thread = Executors.newSingleThreadExecutor();

    public ExecutorService getThread() {
        return thread;
    }

    public void parseEventHandlers(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    protected final List<Runnable> shutdownTasks = new LinkedList<>();

    public void addShutdownTask(Runnable task) {
        shutdownTasks.add(task);
    }

    public abstract void loadImplementations();

    public abstract void loadCommands();

    public abstract ItemStackApi getItemStackApi();

    public abstract PluginAttributes getAttributes();

    // getters

    private SectionLogger sectionLogger;
    public SectionLogger getSectionLogger() {
        return sectionLogger;
    }

    private MainConfig mainConfig;
    public MainConfig getMainConfig() {
        return mainConfig;
    }

    private MaxmindHandler maxmindHandler;
    public MaxmindHandler getMaxmindHandler() {
        return maxmindHandler;
    }

    private MetricsHandler metricsHandler;
    public MetricsHandler getMetricsHandler() {
        return metricsHandler;
    }

    private BannerResolver bannerResolver;
    public BannerResolver getBannerResolver() {
        return bannerResolver;
    }

    private VersionHandler versionHandler;
    public VersionHandler getVersionHandler() {
        return versionHandler;
    }
}
