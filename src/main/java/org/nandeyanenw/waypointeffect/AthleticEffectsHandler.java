package org.nandeyanenw.waypointeffect;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
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
        playerStartTimes.put(player.getUniqueId(), System.currentTimeMillis());
        applyEnabledEffects(player);
    }

    private void applyEnabledEffects(Player player) {
        if (effectManager.isEffectEnabled("knockback")) {
            effectManager.activateKnockbackEffect(player,false);
        }
        if (effectManager.isEffectEnabled("immobilize")) {
            effectManager.activateImmobilizeEffect(player,false);
        }
        if (effectManager.isEffectEnabled("explosion")) {
            effectManager.activateExplosionEffect(player,false);
        }
        if (effectManager.isEffectEnabled("nausea")) {
            effectManager.activateNauseaEffect(player,false);
        }
    }

    public long getElapsedTime(Player player) {
        return System.currentTimeMillis() - playerStartTimes.getOrDefault(player.getUniqueId(), 0L);
    }

    public boolean hasStartedAthletic(Player player) {
        return playerStartTimes.containsKey(player.getUniqueId());
    }

    public void checkAndActivateEffects(Player player) {
        if (!hasStartedAthletic(player)) return;
        if (!isEligibleForEffects(player)) return;
        activateRandomEffect(player);
    }

    private boolean isEligibleForEffects(Player player) {
        long startTime = playerStartTimes.get(player.getUniqueId());
        return System.currentTimeMillis() - startTime >= 300000 &&
                player.getGameMode() == GameMode.ADVENTURE &&
                !isPlayerFinishedAthletic(player) &&
                !isPlayerAffectedByOtherEffects(player);
    }

    private void activateRandomEffect(Player player) {
        Random rand = new Random();
        int effectIndex = rand.nextInt(4);

        switch (effectIndex) {
            case 0: effectManager.activateKnockbackEffect(player,false); break;
            case 1: effectManager.activateImmobilizeEffect(player,false); break;
            case 2: effectManager.activateExplosionEffect(player,false); break;
            case 3: effectManager.activateNauseaEffect(player,false); break;
        }
    }

    private boolean isPlayerFinishedAthletic(Player player) {
        Location playerLocation = player.getLocation();
        Location goalMinLocation = new Location(player.getWorld(), 10, 20, 999);
        Location goalMaxLocation = new Location(player.getWorld(), -10, 20, 1013);
        return isLocationInside(playerLocation, goalMinLocation, goalMaxLocation);
    }

    private boolean isLocationInside(Location location, Location min, Location max) {
        return location.getX() >= min.getX() && location.getX() <= max.getX() &&
                location.getY() >= min.getY() && location.getY() <= max.getY() &&
                location.getZ() >= min.getZ() && location.getZ() <= max.getZ();
    }

    private boolean isPlayerAffectedByOtherEffects(Player player) {
        return player.getActivePotionEffects().stream()
                .anyMatch(effect -> effect.getType().equals(PotionEffectType.CONFUSION));
    }
}
