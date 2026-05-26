package me.justeli.countrybanners.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * @author Eli
 * @since May 26, 2026
 */
public final class GeoIpClient {
    private final HttpClient http;
    private final String authHeader;

    private static final Duration TIMEOUT = Duration.ofMillis(500);

    public GeoIpClient(int accountId, String licenseKey) {
        this.http = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT)
            .build();

        String credentials = accountId + ":" + licenseKey;
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }

    public CountryResponse getCountry(InetAddress address) throws IllegalStateException {
        return getCountry(address.getHostAddress());
    }

    public CountryResponse getCountry(String ip) throws IllegalStateException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://geolite.info/geoip/v2.1/country/" + ip))
            .header("Authorization", authHeader)
            .timeout(TIMEOUT)
            .GET()
            .build();

        try {
            HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                JsonElement error = JsonParser.parseString(response.body()).getAsJsonObject().get("error");
                if (error == null) {
                    throw new IllegalStateException("Unknown error from service");
                }
                throw new IllegalStateException(error.getAsString());
            }

            JsonObject country = JsonParser.parseString(response.body())
                .getAsJsonObject()
                .getAsJsonObject("country");

            return new CountryResponse(
                country.get("iso_code").getAsString(),
                country.getAsJsonObject("names").get("en").getAsString()
            );
        }
        catch (IOException | InterruptedException exception) {
            throw new IllegalStateException(exception.getMessage());
        }
    }

    public void close() {
        http.close();
    }
}
