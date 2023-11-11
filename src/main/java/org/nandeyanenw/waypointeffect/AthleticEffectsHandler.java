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

    private HashMap<UUID, Long> playerStartTimes;
    private EffectManager effectManager;

    public AthleticEffectsHandler(EffectManager effectManager) {
        this.effectManager = effectManager;
        this.playerStartTimes = new HashMap<>();

    }

    public void startAthletic(Player player) {
        playerStartTimes.put(player.getUniqueId(), System.currentTimeMillis());
        // アスレチックのスタート処理
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
        Location goalMaxLocation = new Location(player.getWorld(), -10, 20,1001); // 範囲の最大座標

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
