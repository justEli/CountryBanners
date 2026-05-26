package me.justeli.countrybanners.config;

import me.justeli.countrybanners.Core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * @author Eli
 * @since May 05, 2026
 */
public final class SectionLogger {
    private final Core core;
    public SectionLogger(Core core) {
        this.core = core;
    }

    private final AtomicInteger warnings = new AtomicInteger(0);

    public int getWarnings() {
        return warnings.get();
    }

    public void clearWarnings() {
        warnings.set(0);
    }

    public Named create(String name) {
        return new Named(name);
    }

    public class Named {
        private final String name;
        public Named(String name) {
            this.name = name;
        }

        public void warn(String message) {
            int warning = warnings.incrementAndGet();
            core.getLogger().log(
                Level.WARNING, "[%s] #%,d: %s".formatted(name, warning, message)
            );
        }

        public void info(String message) {
            core.getLogger().log(
                Level.INFO, "[%s]: %s".formatted(name, message)
            );
        }
    }
}
