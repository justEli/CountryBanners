package me.justeli.countrybanners.handler;

import me.justeli.countrybanners.Core;
import me.justeli.countrybanners.api.GeoIpClient;

import java.net.InetAddress;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * @author Eli
 * @since May 26, 2026
 */
public final class MaxmindHandler {
    private final Core core;
    private GeoIpClient client;

    public MaxmindHandler(Core core) {
        this.core = core;
        queueLogin();
        core.addShutdownTask(() -> {
            queueClose();
            awaitClose();
        });
    }

    public void queueLogin() {
        core.getThread().submit(() -> {
            var maxmindAccountId = core.getMainConfig().getMaxmindAccountId();
            var maxmindLicenseKey = core.getMainConfig().getMaxmindLicenseKey();

            if (maxmindAccountId <= 0 || maxmindLicenseKey == null || maxmindLicenseKey.isEmpty()) {
                core.getLogger().log(Level.WARNING, """
                    Cannot retrieve country codes because MaxMind has not been configured correctly. Please create a free \
                    MaxMind account at https://www.maxmind.com/en/geolite2/signup, go to 'Manage license keys', and 'Generate \
                    new license key'. Then add the 'Account ID' and 'License Key' in config.yml."""
                );
                return;
            }

            this.client = new GeoIpClient(maxmindAccountId, maxmindLicenseKey);
        });
    }

    public void queueCountryCode(InetAddress address, Consumer<Optional<String>> code) {
        core.getThread().submit(() -> {
            if (client == null) {
                return;
            }

            try {
                code.accept(Optional.ofNullable(client.getCountry(address).getIsoCode()));
            }
            catch (IllegalStateException exception) {
                core.getLogger().log(Level.WARNING,
                    "Failed to retrieve country code for %s: %s".formatted(address.getHostAddress(), exception.getMessage())
                );
                code.accept(Optional.empty());
            }
        });
    }

    public void queueClose() {
        core.getThread().submit(() -> client.close());
    }

    public void awaitClose() {
        try {
            // start closing the thread and wait 1 second at most for tasks to finish and connection to close
            core.getThread().shutdown();
            if (!core.getThread().awaitTermination(1, TimeUnit.SECONDS)) {
                core.getThread().shutdownNow();
            }
        }
        catch (InterruptedException ignored) {
            core.getThread().shutdownNow();
        }
    }
}
