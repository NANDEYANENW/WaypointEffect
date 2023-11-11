package org.nandeyanenw.waypointeffect;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class EffectManager {

    private final WaypointEffect plugin;
    private HashMap<UUID,Long> lastKnockbackEffectTime;
    private HashMap<UUID,Long> lastImmobilizeEffectTime;
    private HashMap<UUID,Long> lastExplosionEffectTime;
    private HashMap<UUID,Long> lastNauseaEffectTime;

    public EffectManager(WaypointEffect plugin){
        this.plugin = plugin;
        this.lastKnockbackEffectTime = new HashMap<>();
        this.lastImmobilizeEffectTime = new HashMap<>();
        this.lastExplosionEffectTime = new HashMap<>();
        this.lastNauseaEffectTime = new HashMap<>();

    }

    public void activateKnockbackEffect(Player player) {
        long currentTime = System.currentTimeMillis();
        UUID playerId = player.getUniqueId();
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.knockback.cooldown",300);
        int strength = config.getInt("effects.knockback.strength",10);

        if (lastKnockbackEffectTime.containsKey(playerId) && currentTime -lastKnockbackEffectTime.get(playerId) <cooldown) {
            return;
        }
        Vector direction = player.getLocation().getDirection();
        direction.setY(0).normalize().multiply(-strength);
        player.setVelocity(direction);

        lastKnockbackEffectTime.put(playerId,currentTime);

    }

    public void activateImmobilizeEffect(Player player) {
        long currentTime = System.currentTimeMillis();
        UUID playerId = player.getUniqueId();
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.immobilize.cooldown",1800);
        int strength = config.getInt("effects.immobilize.strength",2);
        int duration = config.getInt("effects.immobilize.duration",2) * 20;

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,duration,255));

        if (lastImmobilizeEffectTime.containsKey(playerId) && currentTime - lastImmobilizeEffectTime.get(playerId) < cooldown) {
            return;
        }
    }

    public void activeExplosionEffect(Player player) {
        long currentTime = System.currentTimeMillis();
        UUID playerId = player.getUniqueId();
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.explosion.cooldown", 300);
        player.getWorld().createExplosion(player.getLocation(),0F);

        if (lastExplosionEffectTime.containsKey(playerId) && currentTime - lastExplosionEffectTime.get(playerId) < cooldown) {
            return;
        }
    }

    public void activeNauseaEffect(Player player) {
        long currentTime = System.currentTimeMillis();
        UUID playerId = player.getUniqueId();
        FileConfiguration config = plugin.getConfig();
        int cooldown = config.getInt("effects.nausea.cooldown",300);
        int duration = 10 * 20;

        player.addPotionEffect((new PotionEffect(PotionEffectType.CONFUSION,duration,5)));
        if (lastNauseaEffectTime.containsKey(playerId) && currentTime - lastNauseaEffectTime.get(playerId) < cooldown) {
            return;
        }

    }
 }

