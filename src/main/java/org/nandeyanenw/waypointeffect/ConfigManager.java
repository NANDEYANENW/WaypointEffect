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

    public void loadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        isKnockbackEnabled = config.getBoolean("effects.enable.knockback", true);
        isImmobilizeEnabled = config.getBoolean("effects.enable.immobilize", true);
        isExplosionEnabled = config.getBoolean("effects.enable.explosion", true);
        isNauseaEnabled = config.getBoolean("effects.enable.nausea", true);
    }

    public void setEffectEnabled(String effect, boolean enabled) {
        plugin.getConfig().set("effects.enable." + effect, enabled);
        plugin.saveConfig(); // 設定をファイルに保存
    }

    // 各エフェクトの有効性を取得するメソッド
    public boolean isEffectEnabled(String effect) {
        switch (effect) {
            case "knockback":
                return isKnockbackEnabled;
            case "immobilize":
                return isImmobilizeEnabled;
            case "explosion":
                return isExplosionEnabled;
            case "nausea":
                return isNauseaEnabled;
            default:
                return false;
        }
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        loadConfig();
    }
}
