package org.nandeyanenw.waypointeffect;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class EventListener implements Listener {
    private final EffectManager effectManager;

    public EventListener(EffectManager effectManager) {
        this.effectManager = effectManager;
    }

    public void onPlayerMove(PlayerMoveEvent event) {

    }
}
