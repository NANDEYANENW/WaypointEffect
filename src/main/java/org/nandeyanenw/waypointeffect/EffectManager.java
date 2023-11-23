package org.nandeyanenw.waypointeffect;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.ChatMessageType;
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

    private HashMap<String,Boolean> effectEnabled = new HashMap<>();
    private HashMap<UUID,Long> lastKnockbackEffectTime;
    private HashMap<UUID,Long> lastImmobilizeEffectTime;
    private HashMap<UUID,Long> lastExplosionEffectTime;
    private HashMap<UUID,Long> lastNauseaEffectTime;



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
        this.startPointListener = new StartPointListener();
    }

    public void forceActivateEffect(Player player, String effect) {
        if (!isEffectEnabled(effect.toLowerCase())) return;
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
    public void activateKnockbackEffect(Player player) {
        if (!this.isEffectEnabled("knockback")) return; // 条件を修正

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldown = this.getCooldown("knockback", 300);
        int strength = this.config.getInt("effects.settings.knockback.strength", 10);

        // 初回発動またはクールダウン終了後に再発動
        if (currentTime - this.lastKnockbackEffectTime.getOrDefault(playerId, 0L) < cooldown) return;

        Vector direction = player.getLocation().getDirection().multiply(-strength);
        direction.setY(0); // Y方向の影響を無効化
        player.setVelocity(direction); // プレイヤーにベクトルを適用
        this.lastKnockbackEffectTime.put(playerId, currentTime); // 最後の発動時間を更新
        // クールダウンの終了時刻を計算してメソッドを呼び出す
        long endTime = currentTime + cooldown;
        this.showCooldownInActionBar(player, "ノックバック", endTime);
        // クールダウン終了後にエフェクトを再発動するタスクをスケジュール
        long announceTime = cooldown - 5000; // 5秒前
        if (announceTime > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.YELLOW + "[!] ノックバックまであと5秒です。");
                    }
                }
            }.runTaskLater(this.plugin, announceTime / 50);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                // エフェクトを再発動する前にプレイヤーがオンラインかどうかを確認
                if (!player.isOnline() || !isEffectEnabled("Knockback")) {
                    return;
                }
                // エフェクトを再発動
                activateKnockbackEffect(player);
            }
        }.runTaskLater(this.plugin, cooldown / 50); // クールダウン後に実行
    }


    public void activateImmobilizeEffect(Player player) {
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.settings.immobilize.cooldown", 1800) * 1000;
        int duration = config.getInt("effects.settings.immobilize.duration", 2);

        if (currentTime - this.lastImmobilizeEffectTime.getOrDefault(playerId, 0L) < cooldown) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 255)); // 効果期間は2秒間
        if (!isEffectEnabled("Immobilize")) return;
        this.lastImmobilizeEffectTime.put(playerId, currentTime);
        long endTime = currentTime + cooldown;
        this.showCooldownInActionBar(player, "動けない", endTime);
        long announceTime = cooldown - 5000; // 5秒前
        if (announceTime > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.YELLOW + "[!] 動けなくなるまであと5秒です。");
                    }
                }
            }.runTaskLater(this.plugin, announceTime / 50);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                // エフェクトを再発動する前にプレイヤーがオンラインかどうかを確認
                if (!player.isOnline() || !isEffectEnabled("Immobilize")) {
                    return;
                }
                // エフェクトを再発動
                activateImmobilizeEffect(player);
            }
        }.runTaskLater(this.plugin, cooldown / 50); // クールダウン後に実行
    }


    public void activateExplosionEffect(Player player) {
        if (!isEffectEnabled("explosion")) return;
        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        FileConfiguration config = plugin.getConfig();
        long cooldown = config.getInt("effects.settings.explosion.cooldown", 600) * 1000;

        if (currentTime - this.lastExplosionEffectTime.getOrDefault(playerId, 0L) < cooldown) return;
        player.getWorld().createExplosion(player.getLocation(), 0F, false); // 実際のダメージはなし
        if (!isEffectEnabled("Explosion")) return;
        this.lastExplosionEffectTime.put(playerId, currentTime);
        long endTime = currentTime + cooldown;
        this.showCooldownInActionBar(player, "爆破", endTime);
        long announceTime = cooldown - 5000; // 5秒前
        if (announceTime > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.YELLOW + "[!] 爆破まであと5秒です。");
                    }
                }
            }.runTaskLater(this.plugin, announceTime / 50);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                // エフェクトを再発動する前にプレイヤーがオンラインかどうかを確認
                if (!player.isOnline() || !isEffectEnabled("Explosion")) {
                    return;
                }
                // エフェクトを再発動
                activateExplosionEffect(player);
            }
        }.runTaskLater(this.plugin, cooldown / 50); // クールダウン後に実行
    }




    public void activateNauseaEffect(Player player) {
        if (!isEffectEnabled("nausea")) return;

        UUID playerId = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long cooldown = getCooldown("nausea", 300); // クールダウン時間の取得
        int duration = config.getInt("effects.settings.nausea.duration", 10); // 効果期間の設定

        if (currentTime - this.lastNauseaEffectTime.getOrDefault(playerId, 0L) < cooldown) return;


        // 吐き気エフェクトの発動（強度は固定値で設定）
        player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration * 40, 10)); // 期間をティック単位に変換
        this.lastNauseaEffectTime.put(playerId, currentTime);
        long endTime = currentTime + cooldown;
        this.showCooldownInActionBar(player, "吐き気", endTime);
        long announceTime = cooldown - 5000; // 5秒前
        if (announceTime > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.YELLOW + "[!] 吐き気まであと5秒です。");
                    }
                }
            }.runTaskLater(this.plugin, announceTime / 50);
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                // エフェクトを再発動する前にプレイヤーがオンラインかどうかを確認
                if (!player.isOnline() || !isEffectEnabled("Nausea")) {
                    return;
                }
                // エフェクトを再発動
                activateNauseaEffect(player);
            }
        }.runTaskLater(this.plugin, cooldown / 50); // クールダウン後に実行
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
    public void showCooldownInActionBar(Player player, String effectName, long endTime) {
        final long finalEndTime = endTime; // 実質的に final な変数に代入

        new BukkitRunnable() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                if (currentTime >= finalEndTime) {
                    this.cancel();
                    return;
                }

                int secondsLeft = (int) ((finalEndTime - currentTime) / 1000);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + effectName + " クールダウン: " + secondsLeft + "秒"));
            }
        }.runTaskTimer(this.plugin, 0L, 20L); // 1秒ごとに更新
    }
}


