package me.justeli.countrybanners.spigot.implement;

import me.justeli.countrybanners.api.ItemStackApi;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class ItemStackImplement implements ItemStackApi {
    @Override
    public void hideBannerPatterns(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return;
        }

        meta.addItemFlags(ItemFlag.HIDE_BANNER_PATTERNS);
        item.setItemMeta(meta);
    }

    @Override
    public void setItemName(ItemMeta meta, String name) {
        meta.setItemName(name);
    }

    @Override
    public Optional<PatternType> parsePatternType(@Nullable String value) {
        if (value == null) {
            return Optional.empty();
        }

        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(Registry.BANNER_PATTERN.get(key));
    }

    @Override
    public Optional<Material> parseMaterial(@Nullable String value) {
        if (value == null) {
            return Optional.empty();
        }

        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(Registry.MATERIAL.get(key));
    }

    @Override
    public String toNamespacedKey(PatternType type) {
        NamespacedKey key = type.getKeyOrNull();
        if (key == null) {
            return null;
        }

        return key.toString();
    }

    @Override
    public String toNamespacedKey(ItemStack item) {
        NamespacedKey key = item.getType().getKeyOrNull();
        if (key == null) {
            return null;
        }

        return key.toString();
    }
}
