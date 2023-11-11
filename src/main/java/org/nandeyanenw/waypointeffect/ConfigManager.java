package org.nandeyanenw.waypointeffect;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final WaypointEffect plugin;
    private boolean isKnockbackEnabled;
    private boolean isImmobilizeEnabled;
    private boolean isExplosionEnabled;
    private boolean isNauseaEnabled;

    public ConfigManager(WaypointEffect plugin) {
        this.plugin = plugin;
    }

    public void loadConfig(){
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        isKnockbackEnabled = config.getBoolean("effects.enable.knockback",true);
        isImmobilizeEnabled = config.getBoolean("effects.enable.immobilize",true);
        isExplosionEnabled = config.getBoolean("effects.enable.explosion",true);
        isNauseaEnabled = config.getBoolean("effect.enable.nausea",true);

    }

    public boolean isKnockbackEnabled() {
        return isKnockbackEnabled;
    }

    public boolean isImmobilizeEnabled() {
        return isImmobilizeEnabled;
    }

    public boolean isExplosionEnabled() {
        return isExplosionEnabled;
    }

    public boolean isNauseaEnabled() {
        return isNauseaEnabled;
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }
}
