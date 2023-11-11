package org.nandeyanenw.waypointeffect;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class EffectManager {

    private HashMap<String,Boolean> effectEnabled = new HashMap<>();
    private final WaypointEffect plugin;
    private HashMap<UUID,Long> lastKnockbackEffectTime;
    private HashMap<UUID,Long> lastImmobilizeEffectTime;
    private HashMap<UUID,Long> lastExplosionEffectTime;
    private HashMap<UUID,Long> lastNauseaEffectTime;


    public EffectManager(WaypointEffect plugin){
        this.plugin = plugin;
        this.lastKnockbackEffectTime = new HashMap<>(); //ノックバック
        this.lastImmobilizeEffectTime = new HashMap<>(); //動けない
        this.lastExplosionEffectTime = new HashMap<>(); //爆発
        this.lastNauseaEffectTime = new HashMap<>();
        effectEnabled.put("knockback", true);
        effectEnabled.put("immobilize", true);
        effectEnabled.put("explosion", true);
        effectEnabled.put("nausea", true);

    }

    public void activateKnockbackEffect(Player player) {

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.settings.knockback.cooldown", 300) * 1000; // ミリ秒に変換
        int strength = config.getInt("effects.settings.knockbacks.strength",10);

        if (lastKnockbackEffectTime.containsKey(playerId) && currentTime - lastKnockbackEffectTime.get(playerId) < cooldown) {
            return;
        }

        Vector direction = player.getLocation().getDirection();
        direction.setY(0).normalize().multiply(-10); // 効果のサイズ（強度）は10
        player.setVelocity(direction);
        if (!isEffectEnabled("knockback")) return;
        lastKnockbackEffectTime.put(playerId, currentTime);

    }


    public void activateImmobilizeEffect(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.settings.immobilize.cooldown", 1800) * 1000;
        int duration = config.getInt("effects.settings.immobilize.duration", 2);

        if (lastImmobilizeEffectTime.containsKey(playerId) && currentTime - lastImmobilizeEffectTime.get(playerId) < cooldown) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 255)); // 効果期間は2秒間
        if (!isEffectEnabled("Immobilize")) return;
        lastImmobilizeEffectTime.put(playerId, currentTime);
    }



    public void activateExplosionEffect(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.settings.explosion.cooldown", 600) * 1000;

        if (lastExplosionEffectTime.containsKey(playerId) && currentTime - lastExplosionEffectTime.get(playerId) < cooldown) {
            return;
        }

        player.getWorld().createExplosion(player.getLocation(), 0F, false); // 実際のダメージはなし
        if (!isEffectEnabled("Explosion")) return;
        lastExplosionEffectTime.put(playerId, currentTime);
    }


    public void activateNauseaEffect(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.settings.nausea.cooldown", 300) * 1000;
        int strength = config.getInt("effects.settings.nausea.strength", 10);

        if (lastNauseaEffectTime.containsKey(playerId) && currentTime - lastNauseaEffectTime.get(playerId) < cooldown) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40, 10)); // 効果期間は2秒間
        if (!isEffectEnabled("nausea")) return;
        lastNauseaEffectTime.put(playerId, currentTime);
    }
    public void setEffectEnabled(String effect, boolean enabled) {
        effectEnabled.put(effect, enabled);
    }

    public boolean isEffectEnabled(String effect) {
        return effectEnabled.getOrDefault(effect, false);
    }

}


