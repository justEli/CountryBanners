package me.justeli.countrybanners.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.justeli.countrybanners.Core;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class BannerResolver {
    private final Core core;
    private final Path directory;
    private final SectionLogger.Named logger;

    public BannerResolver(Core core) {
        this.core = core;
        this.directory = core.getDataFolder().toPath().resolve("banners");
        this.logger = core.getSectionLogger().create("banners/");

        try {
            // always create directory
            Files.createDirectories(directory);
        }
        catch (IOException exception) {
            logger.warn("Failed to create banners directory.");
        }

        queueDownload();
        queueParse();
    }

    private final Map<String, ItemStack> banners = new HashMap<>();

    public Optional<ItemStack> getBanner(@NotNull String code) {
        code = code.toUpperCase();
        if (!banners.containsKey(code)) {
            return Optional.empty();
        }

        core.getMetricsHandler().registerBannerHandOut(1);
        return Optional.of(banners.get(code).clone());
    }

    public Set<String> getCountryCodes() {
        return banners.keySet();
    }

    public void queueParse() {
        core.getThread().submit(() -> {
            try {
                parse();
            }
            catch (IOException ignored) {}
        });
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private void parse() throws IOException {
        banners.clear(); // clear currently loaded banners

        // loop all files
        try (var stream = Files.walk(directory)) {
            for (var file : stream.filter(file -> file.getFileName().toString().matches("[A-Z]{2}\\.json")).toList()) {
                String fileName = file.getFileName().toString();
                String code = fileName.substring(0, fileName.length() - 5); // .json is 5 chars

                JsonObject config;
                try {
                    config = GSON.fromJson(Files.readString(file), JsonObject.class);
                }
                catch (IOException exception) {
                    logger.warn("Skipping banner file '%s' because it cannot be read.".formatted(fileName));
                    continue;
                }

                String country = config.get("country").getAsString();
                String itemType = config.get("banner").getAsString();

                Optional<Material> material = core.getItemStackApi().parseMaterial(itemType);
                if (material.isEmpty()) {
                    logger.warn("Skipping banner file '%s' because item type '%s' at `banner` is invalid.".formatted(
                        fileName, itemType
                    ));
                    continue;
                }

                List<Pattern> patterns = new ArrayList<>();
                for (var element : config.getAsJsonArray("patterns")) {
                    var entry = element.getAsJsonObject();

                    var color = entry.get("color");
                    var pattern = entry.get("pattern");
                    if (color == null || pattern == null) {
                        logger.warn("Skipping banner file '%s' because dye color and/or pattern type is not set.".formatted(
                            fileName
                        ));
                        continue;
                    }

                    String rawDyeColor = color.getAsString().toUpperCase();
                    DyeColor dyeColor;
                    try {
                        dyeColor = DyeColor.valueOf(rawDyeColor);
                    }
                    catch (IllegalArgumentException ignored) {
                        logger.warn("Skipping banner file '%s' because dye color '%s' at `patterns` is invalid.".formatted(
                            fileName, rawDyeColor
                        ));
                        continue;
                    }

                    Optional<PatternType> patternType = core.getItemStackApi().parsePatternType(pattern.getAsString());
                    if (patternType.isEmpty()) {
                        logger.warn("Skipping banner file '%s' because pattern type '%s' at `patterns` is invalid.".formatted(
                            fileName, pattern.getAsString()
                        ));
                        continue;
                    }

                    patterns.add(new Pattern(dyeColor, patternType.get()));
                }

                // create banner item
                ItemStack item = new ItemStack(material.get());
                ItemMeta meta = item.getItemMeta();
                if (!(meta instanceof BannerMeta banner)) {
                    logger.warn("Skipping banner file '%s' because item type '%s' at `banner` is not a banner.".formatted(
                        fileName, itemType
                    ));
                    continue;
                }

                for (Pattern pattern : patterns) {
                    banner.addPattern(pattern);
                }

                core.getItemStackApi().setItemName(meta, core.getMainConfig().getBannerName().replace("{country}", country));
                meta.setRarity(core.getMainConfig().getBannerRarity());

                item.setItemMeta(banner);
                if (core.getMainConfig().isBannerPatternsHide()) {
                    core.getItemStackApi().hideBannerPatterns(item);
                }

                banners.put(code, item);
            }

            logger.info("Loaded %,d country banners from the banners folder.".formatted(banners.size()));
        }
    }

    public void queueDownload() {
        core.getThread().submit(this::download);
    }

    private void download() {
        List<String> downloaded = new ArrayList<>();
        try (var client = HttpClient.newHttpClient()) {
            HttpRequest request = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(10))
                .uri(URI.create("https://api.github.com/repos/%s/contents/banners".formatted(Core.REPOSITORY)))
                .header("Accept", "application/vnd.github+json").build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();

            // every file from locale in GitHub
            for (JsonElement element : array) {
                JsonObject json = element.getAsJsonObject();
                String type = json.get("type").getAsString();
                String name = json.get("name").getAsString();

                if (!"file".equals(type) || !name.endsWith(".json")) {
                    continue; // only allow .json in root folder
                }

                Path target = directory.resolve(name);
                if (Files.exists(target)) {
                    continue; // skip when file already exists
                }

                HttpRequest fileRequest = HttpRequest.newBuilder()
                    .timeout(Duration.ofSeconds(10))
                    .uri(URI.create(json.get("download_url").getAsString()))
                    .build();

                HttpResponse<String> fileResponse = client.send(fileRequest, HttpResponse.BodyHandlers.ofString());
                if (fileResponse.statusCode() == 200) {
                    Files.writeString(target, fileResponse.body()); // save language file
                    downloaded.add(name.substring(0, name.length() - 5)); // trim .json
                }
            }
        }
        catch (IOException | InterruptedException ignored) {
            logger.warn("""
                Failed to download one or more banners. Country banners can also manually be downloaded from GitHub: \
                https://github.com/justEli/CountryBanners/tree/main/banners"""
            );
        }

        if (downloaded.isEmpty()) {
            return;
        }

        logger.info("Downloaded %d country banners: %s".formatted(
            downloaded.size(), String.join(", ", downloaded)
        ));
    }

    public boolean registerBanner(ItemStack item, String countryCode, String countryName) {
        if (item.getItemMeta() instanceof BannerMeta meta) {
            String namespacedKey = core.getItemStackApi().toNamespacedKey(item);
            if (namespacedKey == null) {
                return false;
            }

            JsonArray patterns = new JsonArray();
            for (Pattern pattern : meta.getPatterns()) {
                String patternKey = core.getItemStackApi().toNamespacedKey(pattern.getPattern());
                if (patternKey == null) {
                    continue;
                }

                JsonObject entry = new JsonObject();
                entry.addProperty("color", pattern.getColor().name().toLowerCase());
                entry.addProperty("pattern", patternKey);
                patterns.add(entry);
            }

            JsonObject config = new JsonObject();
            config.addProperty("country", countryName);
            config.addProperty("banner", namespacedKey);
            config.add("patterns", patterns);

            try {
                Files.writeString(directory.resolve(countryCode.toUpperCase() + ".json"), GSON.toJson(config));

                // update all banners
                queueParse();
                return true;
            }
            catch (IOException ignored) {}
        }
        return false;
    }
}
