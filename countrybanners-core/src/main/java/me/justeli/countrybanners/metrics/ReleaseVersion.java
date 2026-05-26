package me.justeli.countrybanners.metrics;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * @author Eli
 * @since April 15, 2026
 */
public record ReleaseVersion(String tag, boolean preRelease, String name, String date) {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM d, uuuu");

    public ReleaseVersion(String tag, boolean preRelease, String name, String date) {
        this.tag = tag;
        this.preRelease = preRelease;
        this.name = name;
        this.date = Instant.parse(date).atZone(ZoneOffset.UTC).format(FORMATTER);
    }

    @Override
    public boolean equals(Object o) {
        return tag != null && o instanceof ReleaseVersion version && tag.equals(version.tag);
    }
}
