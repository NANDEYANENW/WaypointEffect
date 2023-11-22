package org.nandeyanenw.waypointeffect;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.UUID;

public class StartPointListener implements Listener {
    private final HashMap<UUID, Boolean> playerStartFlags;

    public StartPointListener() {
        this.playerStartFlags = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        // スタート地点の範囲を定義
        if (loc.getBlockX() >= -3 && loc.getBlockX() <= 4 &&
                loc.getBlockY() == 20 &&
                loc.getBlockZ() >= 21 && loc.getBlockZ() <= 22) {

            // スタート地点を通過したことを記録
            playerStartFlags.put(player.getUniqueId(), true);
        }
    }

    public boolean hasPassedStartPoint(UUID playerId) {
        return playerStartFlags.getOrDefault(playerId, false);
    }
}
