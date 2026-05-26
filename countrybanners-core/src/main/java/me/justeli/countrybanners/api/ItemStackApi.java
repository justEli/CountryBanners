package me.justeli.countrybanners.api;

import org.bukkit.Material;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author Eli
 * @since May 25, 2026
 */
public interface ItemStackApi {
    void hideBannerPatterns(ItemStack item);

    void setItemName(ItemMeta meta, String name);

    Optional<PatternType> parsePatternType(@Nullable String value);

    Optional<Material> parseMaterial(@Nullable String value);

    String toNamespacedKey(PatternType type);

    String toNamespacedKey(ItemStack item);
}
