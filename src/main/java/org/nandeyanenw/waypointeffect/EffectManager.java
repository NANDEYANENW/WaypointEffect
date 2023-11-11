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
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.knockback.cooldown", 300) * 1000; // ミリ秒に変換

        if (lastKnockbackEffectTime.containsKey(playerId) && currentTime - lastKnockbackEffectTime.get(playerId) < cooldown) {
            return;
        }

        Vector direction = player.getLocation().getDirection();
        direction.setY(0).normalize().multiply(-10); // 効果のサイズ（強度）は10
        player.setVelocity(direction);

        lastKnockbackEffectTime.put(playerId, currentTime);
    }


    public void activateImmobilizeEffect(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.immobilize.cooldown", 1800) * 1000;

        if (lastImmobilizeEffectTime.containsKey(playerId) && currentTime - lastImmobilizeEffectTime.get(playerId) < cooldown) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 255)); // 効果期間は2秒間

        lastImmobilizeEffectTime.put(playerId, currentTime);
    }



    public void activateExplosionEffect(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.explosion.cooldown", 600) * 1000;

        if (lastExplosionEffectTime.containsKey(playerId) && currentTime - lastExplosionEffectTime.get(playerId) < cooldown) {
            return;
        }

        player.getWorld().createExplosion(player.getLocation(), 0F, false); // 実際のダメージはなし

        lastExplosionEffectTime.put(playerId, currentTime);
    }


    public void activateNauseaEffect(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.nausea.cooldown", 300) * 1000;

        if (lastNauseaEffectTime.containsKey(playerId) && currentTime - lastNauseaEffectTime.get(playerId) < cooldown) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 10)); // 効果期間は2秒間

        lastNauseaEffectTime.put(playerId, currentTime);
    }

}


