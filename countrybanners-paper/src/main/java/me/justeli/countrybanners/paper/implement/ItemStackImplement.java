package me.justeli.countrybanners.paper.implement;

import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import me.justeli.countrybanners.api.ItemStackApi;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @author Eli
 * @since May 25, 2026
 */
public final class ItemStackImplement implements ItemStackApi {
    private static final TooltipDisplay HIDDEN =
        TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.BANNER_PATTERNS).build();

    @Override
    public void hideBannerPatterns(ItemStack item) {
        item.setData(DataComponentTypes.TOOLTIP_DISPLAY, HIDDEN);
    }

    @Override
    public void setItemName(ItemMeta meta, String name) {
        meta.itemName(Component.text(name));
    }

    private static final Registry<PatternType> REGISTRY =
        RegistryAccess.registryAccess().getRegistry(RegistryKey.BANNER_PATTERN);

    @Override
    public Optional<PatternType> parsePatternType(@Nullable String value) {
        if (value == null) {
            return Optional.empty();
        }

        NamespacedKey key = NamespacedKey.fromString(value);
        if (key == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(REGISTRY.get(key));
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
        NamespacedKey key = REGISTRY.getKey(type);
        if (key == null) {
            return null;
        }

        return key.toString();
    }

    @Override
    public String toNamespacedKey(ItemStack item) {
        return item.getType().getKey().toString();
    }
}
