package org.nandeyanenw.waypointeffect;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EffectManager {

    private final WaypointEffect plugin;

    public EffectManager(WaypointEffect plugin){
        this.plugin = plugin;
    }

    public void activateKnockbackEffect(Player player) {
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.knockback.cooldown",300);
        int strength = config.getInt("effects.knockback.strength",10);



    }

    public void activateImmobilizeEffect(Player player) {
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.immobilize.cooldown",1800);
        int strength = config.getInt("effects.immobilize.strength",2);
    }

    public void activeExplosionEffect(Player player) {
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.explosion.cooldown",300);

    }
}
