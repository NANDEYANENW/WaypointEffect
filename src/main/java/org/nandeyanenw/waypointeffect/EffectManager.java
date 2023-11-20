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
    private FileConfiguration config; // configの追加

    private HashMap<String,Boolean> effectEnabled = new HashMap<>();
    private HashMap<UUID,Long> lastKnockbackEffectTime;
    private HashMap<UUID,Long> lastImmobilizeEffectTime;
    private HashMap<UUID,Long> lastExplosionEffectTime;
    private HashMap<UUID,Long> lastNauseaEffectTime;


    public void activateKnockbackEffect(Player player) {
        if (this.isEffectEnabled("knockback")) return;
            UUID playerId = player.getUniqueId();
            long currentTime = System.currentTimeMillis();
            long cooldown = this.getCooldown("knockback", 300);
            int strength = this.config.getInt("effects.settings.knockback.strength", 10);

            if (currentTime - (Long)this.lastKnockbackEffectTime.getOrDefault(playerId, 0L) >= cooldown) return;
                Vector direction = player.getLocation().getDirection().multiply(-strength);
                direction.setY(0);
                player.setVelocity(direction);
                lastKnockbackEffectTime.put(playerId, currentTime);
    }


    public EffectManager(WaypointEffect plugin){
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
    }

    public void forceActivateEffect(Player player, String effect) {
        switch (effect.toLowerCase()) {
            case "knockback":
                activateKnockbackEffect(player);
                break;
            case "immobilize":
                activateImmobilizeEffect(player);
                break;
            case "explosion":
                activateExplosionEffect(player);
                break;
            case "nausea":
                activateNauseaEffect(player);
                break;
            default:
                // 不明なエフェクトの場合は何もしない
        }
    }


    public void activateImmobilizeEffect(Player player) {
        if (!isEffectEnabled("immobilize")) return;
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
        if (!isEffectEnabled("explosion")) return;
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
        if (!isEffectEnabled("nausea")) return;

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldown = getCooldown("nausea", 300); // クールダウン時間の取得
        int duration = config.getInt("effects.settings.nausea.duration", 10); // 効果期間の設定

        if (currentTime - lastNauseaEffectTime.getOrDefault(playerId, 0L) < cooldown) return;

        // 吐き気エフェクトの発動（強度は固定値で設定）
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration * 40, 10)); // 期間をティック単位に変換

        lastNauseaEffectTime.put(playerId, currentTime);
    }

    private long getCooldown(String effectName, int defaultCooldown) {
        return plugin.getConfig().getInt("effects.settings." + effectName + ".cooldown", defaultCooldown) * 1000L;
   }
    public void setEffectEnabled(String effect, boolean enabled) {
        effectEnabled.put(effect, enabled);
    }

    public boolean isEffectEnabled(String effect) {
        return effectEnabled.getOrDefault(effect, false);
    }

}


