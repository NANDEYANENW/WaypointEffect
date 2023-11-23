package org.nandeyanenw.waypointeffect;


import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class EffectManager {
    private StartPointListener startPointListener;
    private final WaypointEffect plugin;
    private FileConfiguration config; // configの追加

    private HashMap<String, Boolean> effectEnabled = new HashMap<>();
    private HashMap<UUID, Long> lastKnockbackEffectTime;
    private HashMap<UUID, Long> lastImmobilizeEffectTime;
    private HashMap<UUID, Long> lastExplosionEffectTime;
    private HashMap<UUID, Long> lastNauseaEffectTime;


    public EffectManager(WaypointEffect plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig(); // configを初期化
        this.lastKnockbackEffectTime = new HashMap<>(); //ノックバック
        this.lastImmobilizeEffectTime = new HashMap<>(); //動けない
        this.lastExplosionEffectTime = new HashMap<>(); //爆発
        this.lastNauseaEffectTime = new HashMap<>(); //吐き気
        effectEnabled.put("knockback", true);
        effectEnabled.put("immobilize", true);
        effectEnabled.put("explosion", true);
        effectEnabled.put("nausea", true);
        this.startPointListener = new StartPointListener();
    }

    public void forceActivateEffect(Player player, String effect,boolean isForced) {
        if (!isEffectEnabled(effect.toLowerCase())) return;

        switch (effect.toLowerCase()) {
            case "knockback":
                activateKnockbackEffect(player, true);
                break;
            case "immobilize":
                activateImmobilizeEffect(player, true);
                break;
            case "explosion":
                activateExplosionEffect(player, true);
                break;
            case "nausea":
                activateNauseaEffect(player, true);
                break;
            default:
                // 不明なエフェクトの場合は何もしない
        }
    }

    public void activateKnockbackEffect(Player player,boolean isForced) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldown = this.getCooldown("knockback", 300);
        int strength = this.config.getInt("effects.settings.knockback.strength", 10);

        if (!isForced && (currentTime - this.lastKnockbackEffectTime.getOrDefault(playerId, 0L) < cooldown)) {
            return;
        }

        Vector direction = player.getLocation().getDirection().multiply(-strength);
        direction.setY(0);
        player.setVelocity(direction);
        this.lastKnockbackEffectTime.put(playerId, currentTime);

        if (isForced) {
            scheduleNextEffectActivation(player, "knockback", cooldown);
        }
    }



    public void activateImmobilizeEffect(Player player, boolean isForced) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldown = this.getCooldown("immobilize", 1800);
        int duration = this.config.getInt("effects.settings.immobilize.duration", 2);

        if (!isForced && (currentTime - this.lastImmobilizeEffectTime.getOrDefault(playerId, 0L) < cooldown)) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40 * duration, 255));
        this.lastImmobilizeEffectTime.put(playerId, currentTime);

        if (isForced) {
            scheduleNextEffectActivation(player, "immobilize", cooldown);
        }
    }




    public void activateExplosionEffect(Player player, boolean isForced) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldown = this.getCooldown("explosion", 600);

        if (!isForced && (currentTime - this.lastExplosionEffectTime.getOrDefault(playerId, 0L) < cooldown)) {
            return;
        }

        player.getWorld().createExplosion(player.getLocation(), 0F, false);
        this.lastExplosionEffectTime.put(playerId, currentTime);

        if (isForced) {
            scheduleNextEffectActivation(player, "explosion", cooldown);
        }
    }




    public void activateNauseaEffect(Player player, boolean isForced) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldown = this.getCooldown("nausea", 300);
        int duration = this.config.getInt("effects.settings.nausea.duration", 10);

        if (!isForced && (currentTime - this.lastNauseaEffectTime.getOrDefault(playerId, 0L) < cooldown)) {
            return;
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 40 * duration, 10));
        this.lastNauseaEffectTime.put(playerId, currentTime);

        if (isForced) {
            scheduleNextEffectActivation(player, "nausea", cooldown);
        }
    }



    private long getCooldown(String effectName, int defaultCooldown) {
        return plugin.getConfig().getInt("effects.settings." + effectName + ".cooldown", defaultCooldown) * 1000L;
    }

    public void setEffectEnabled(String effect, boolean enabled) {
        effectEnabled.put(effect, enabled);
    }

    public boolean isEffectEnabled(String effect) {
        return effectEnabled.getOrDefault(effect, false); //設定されていないエフェクトはデフォルトで無効になります。
    }

    private void scheduleNextEffectActivation(Player player, String effect, long cooldown) {
        // 以前のロジックを削除し、新たにエフェクトをスケジュールするロジックを追加
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || !isEffectEnabled(effect)) return;

                switch (effect) {
                    case "knockback":
                        activateKnockbackEffect(player, false);
                        break;
                    case "immobilize":
                        activateImmobilizeEffect(player, false);
                        break;
                    case "explosion":
                        activateExplosionEffect(player, false);
                        break;
                    case "nausea":
                        activateNauseaEffect(player, false);
                        break;
                }
            }
        }.runTaskLater(plugin, cooldown / 50);
    }
}