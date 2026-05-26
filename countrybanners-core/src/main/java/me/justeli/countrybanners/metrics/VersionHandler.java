package me.justeli.countrybanners.metrics;

import com.google.gson.JsonParser;
import me.justeli.countrybanners.Core;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Optional;

/**
 * @author Eli
 * @since February 04, 2022 (creation)
 */
public final class VersionHandler {
    private final Core core;
    public VersionHandler(Core core) {
        this.core = core;
        queueCheck();
    }

    private static final String LINE = "--------------------------------------------------------------------";

    public void queueCheck() {
        core.getThread().submit(() -> findLatestVersion(core.getMainConfig().isNotifyOnUpdate()));
    }

    public void findLatestVersion(boolean printToConsole) {
        Optional<ReleaseVersion> version = findLatestVersion(Core.REPOSITORY);
        if (version.isEmpty()) {
            return;
        }

        this.latestVersion = version.get();
        if (!printToConsole) {
            return;
        }

        String pluginVersion = core.getAttributes().getVersion();
        if (!pluginVersion.equals(latestVersion.tag()) && !latestVersion.preRelease()) {
            core.getLogger().warning(LINE);
            core.getLogger().warning(" Detected an outdated version of CountryBanners (%s is installed).".formatted(pluginVersion));
            core.getLogger().warning(" The latest version is %s, released on %s.".formatted(latestVersion.tag(), latestVersion.date()));
            core.getLogger().warning(" Download: %s".formatted(core.getAttributes().getUrl()));
            core.getLogger().warning(LINE);
        }
    }

    private ReleaseVersion latestVersion;
    public Optional<ReleaseVersion> getLatestVersion() {
        return Optional.ofNullable(latestVersion);
    }

    public static Optional<ReleaseVersion> findLatestVersion(String repository) {
        try {
            URL url = URI.create("https://api.github.com/repos/" + repository + "/releases/latest").toURL();
            URLConnection request = url.openConnection();

            request.setReadTimeout(1000);
            request.setConnectTimeout(1000);
            request.connect();

            try (var reader = new InputStreamReader((InputStream) request.getContent())) {
                var root = JsonParser.parseReader(reader);
                var jsonObject = root.getAsJsonObject();
                return Optional.of(new ReleaseVersion(
                    jsonObject.get("tag_name").getAsString(),
                    jsonObject.get("prerelease").getAsBoolean(),
                    jsonObject.get("name").getAsString(),
                    jsonObject.get("published_at").getAsString()
                ));
            }
        }
        catch (Exception ignored) {
            return Optional.empty();
        }
    }
}
