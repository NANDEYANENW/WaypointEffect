package org.nandeyanenw.waypointeffect;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Random;
import java.util.UUID;
import java.util.HashMap;

public class AthleticEffectsHandler {
    private final EffectManager effectManager;
    private final WaypointEffect plugin;
    private HashMap<UUID, Long> playerStartTimes;

    public AthleticEffectsHandler(EffectManager effectManager, WaypointEffect plugin) {
        this.effectManager = effectManager;
        this.plugin = plugin;
        this.playerStartTimes = new HashMap<>();
    }


    public void startAthletic(Player player) {
        // アスレチックのスタート処理
        playerStartTimes.put(player.getUniqueId(), System.currentTimeMillis());

        // 特定の条件に基づいてエフェクトを発動する
        if (effectManager.isEffectEnabled("knockback")) {
            effectManager.activateKnockbackEffect(player);
        }
        if (effectManager.isEffectEnabled("immobilize")) {
            effectManager.activateImmobilizeEffect(player);
        }
        if (effectManager.isEffectEnabled("explosion")) {
            effectManager.activateExplosionEffect(player);
        }
        if (effectManager.isEffectEnabled("nausea")) {
            effectManager.activateNauseaEffect(player);
        }
    }

        // プレイヤーがアスレチックを開始してからの時間を取得
    public long getElapsedTime(Player player) {
        return System.currentTimeMillis() - playerStartTimes.getOrDefault(player.getUniqueId(), 0L);
    }

    // アスレチックを開始したかどうかをチェック
    public boolean hasStartedAthletic(Player player) {
        return playerStartTimes.containsKey(player.getUniqueId());
    }



    public void checkAndActivateEffects(Player player) {
        if (!playerStartTimes.containsKey(player.getUniqueId())) return;

        long startTime = playerStartTimes.get(player.getUniqueId());
        if (System.currentTimeMillis() - startTime < 300000) return; // 5分未満なら何もしない

        if (player.getGameMode() != GameMode.ADVENTURE) return; // アドベンチャーモードでなければ何もしない
        if (isPlayerFinishedAthletic(player)) return; // アスレチックを完了していれば何もしない
        if (isPlayerAffectedByOtherEffects(player)) return; // 他のエフェクトにかかっていれば何もしない

        activateRandomEffect(player);
    }

    private void activateRandomEffect(Player player) {
        Random rand = new Random();
        int effectIndex = rand.nextInt(4); // 0から3の間のランダムな数

        switch (effectIndex) {
            case 0:
                effectManager.activateKnockbackEffect(player);
                break;
            case 1:
                effectManager.activateImmobilizeEffect(player);
                break;
            case 2:
                effectManager.activateExplosionEffect(player);
                break;
            case 3:
                effectManager.activateNauseaEffect(player);
                break;
        }
    }

    private boolean isPlayerFinishedAthletic(Player player) {
        Location playerLocation = player.getLocation();

        // ゴール地点の範囲を定義する
        Location goalMinLocation = new Location(player.getWorld(), 10, 20, 999); // 範囲の最小座標
        Location goalMaxLocation = new Location(player.getWorld(), -10, 20,1013); // 範囲の最大座標

        // プレイヤーの位置がゴール地点の範囲内にあるかをチェック
        return isLocationInside(playerLocation, goalMinLocation, goalMaxLocation);
    }

    private boolean isLocationInside(Location location, Location min, Location max) {
        return location.getX() >= min.getX() && location.getX() <= max.getX() &&
                location.getY() >= min.getY() && location.getY() <= max.getY() &&
                location.getZ() >= min.getZ() && location.getZ() <= max.getZ();
    }


    private boolean isPlayerAffectedByOtherEffects(Player player) {
        // 吐き気エフェクト（CONFUSION）に影響されているかをチェック
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.CONFUSION)) {
                return true; // プレイヤーが吐き気エフェクトを受けている
            }
        }
        return false;
    }

}
