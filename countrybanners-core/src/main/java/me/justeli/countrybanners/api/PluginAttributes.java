package me.justeli.countrybanners.api;

/**
 * @author Eli
 * @since April 27, 2026
 */
public interface PluginAttributes {
    /// the current version of this plugin
    String getVersion();

    /// the main url of the project
    String getUrl();

    /// the description of the plugin
    String getDescription();
}
