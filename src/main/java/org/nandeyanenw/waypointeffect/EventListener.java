package org.nandeyanenw.waypointeffect;

import org.bukkit.event.Listener;


public class EventListener implements Listener {
    private final EffectManager effectManager;

    public EventListener(EffectManager effectManager) {
        this.effectManager = effectManager;
    }
}


