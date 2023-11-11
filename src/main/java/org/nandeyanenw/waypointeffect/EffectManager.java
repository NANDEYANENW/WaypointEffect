package org.nandeyanenw.waypointeffect;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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

        Vector direction = player.getLocation().getDirection();
        direction.setY(0).normalize().multiply(-strength);
        player.setVelocity(direction);

    }

    public void activateImmobilizeEffect(Player player) {
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.immobilize.cooldown",1800);
        int strength = config.getInt("effects.immobilize.strength",2);
        int duration = config.getInt("effects.immobilize.duration",2) * 20;

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,duration,255));
    }

    public void activeExplosionEffect(Player player) {
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.explosion.cooldown", 300);
        player.getWorld().createExplosion(player.getLocation(),0F);
    }

    public void activeNauseaEffect(Player player) {
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.nausea.cooldown",300);
        int duration = 10 * 20;

        player.addPotionEffect((new PotionEffect(PotionEffectType.CONFUSION,duration,5)));

        }
 }

