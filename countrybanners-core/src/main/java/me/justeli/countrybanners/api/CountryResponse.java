package me.justeli.countrybanners.api;

/**
 * @author Eli
 * @since May 26, 2026
 */
public final class CountryResponse {
    private final String isoCode;
    private final String name;

    public CountryResponse(String isoCode, String name) {
        this.isoCode = isoCode;
        this.name = name;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public String getName() {
        return name;
    }
}
