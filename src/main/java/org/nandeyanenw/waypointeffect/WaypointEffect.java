package org.nandeyanenw.waypointeffect;


import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class WaypointEffect extends JavaPlugin implements CommandExecutor,Listener {

    private Location goalMinLocation;
    private Location goalMaxLocation;

    private StartPointListener startPointListener;

    private ConfigManager configManager;
    private EffectManager effectManager;


    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("撮影プラグインを起動しました。初期設定を行います。");
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        effectManager = new EffectManager(this);

        this.getCommand("wp").setExecutor(this);
        this.getCommand("effecttoggle").setExecutor(this);
        this.getCommand("forceeffect").setExecutor(this);
        startPointListener = new StartPointListener();
        // ゴールエリアの座標範囲を設定
        goalMinLocation = new Location(getServer().getWorld("world"), -10, 20, 999);
        goalMaxLocation = new Location(getServer().getWorld("world"), 10, 20, 1001);
        getServer().getPluginManager().registerEvents(this,this);

    }
    public StartPointListener getStartPointListener() {
        return startPointListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーからのみ実行できます。");
            return true;
        }

        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("wp")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (!player.hasPermission("wp.reload")) {
                    player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
                    return true;
                }
                configManager.loadConfig();
                sender.sendMessage("設定を再読み込みしました。");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("effecttoggle")) {
            if (args.length == 2) {
                if (!sender.hasPermission("wp.effecttoggle")) {
                    sender.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
                    return true;
                }
                String effect = args[0];
                boolean enabled = args[1].equalsIgnoreCase("enable");
                configManager.setEffectEnabled(effect, enabled);
                sender.sendMessage(effect + " エフェクトを " + (enabled ? "有効化" : "無効化") + "しました。");
                return true;
            } else {
                sender.sendMessage("使用法: /effecttoggle [effect] [enable|disable]");
            }

        }
        if (cmd.getName().equalsIgnoreCase("forceeffect")) {
            if (args.length == 2) {
                if (!sender.hasPermission("wp.fe")) {
                    sender.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
                    return true;
                }

                String effect = args[0].toLowerCase();
                if (args[1].equalsIgnoreCase("all")) {
                    // 全プレイヤーにエフェクトを適用
                    for (Player targetPlayer : this.getServer().getOnlinePlayers()){
                        applyEffectToPlayer(targetPlayer, effect);
                    }
                    sender.sendMessage("§e全プレイヤーに " + effect + " エフェクトを適用しました。");
                } else {
                    // 特定のプレイヤーにエフェクトを適用
                    Player targetPlayer = this.getServer().getPlayer(args[1]);
                    if (targetPlayer == null) {
                        sender.sendMessage("§c指定したプレイヤーが見つかりません。");
                        return true;
                    }
                    applyEffectToPlayer(targetPlayer, effect);
                    sender.sendMessage("§e" + targetPlayer.getName() + " に " + effect + " エフェクトを適用しました。");
                }
                return true;
            } else {
                sender.sendMessage("使用法: /forceeffect [effect] [player|all]");
            }
        }
        return false;
    }

    private void applyEffectToPlayer(Player player, String effect) {
        switch (effect) {
            case "knockback":
                effectManager.activateKnockbackEffect(player,false);
                break;
            case "immobilize":
                effectManager.activateImmobilizeEffect(player,false);
                break;
            case "explosion":
                effectManager.activateExplosionEffect(player,false);
                break;
            case "nausea":
                effectManager.activateNauseaEffect(player,false);
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        // ゴール地点にプレイヤーが到達したかチェック
        if (isPlayerInsideGoal(playerLocation) && player.getGameMode() == GameMode.ADVENTURE) {
            player.setGameMode(GameMode.SPECTATOR); // スペクテイターモードに切り替え
        }
    }

    private boolean isPlayerInsideGoal(Location location) {
        return location.getX() >= goalMinLocation.getX() && location.getX() <= goalMaxLocation.getX()
                && location.getY() >= goalMinLocation.getY() && location.getY() <= goalMaxLocation.getY()
                && location.getZ() >= goalMinLocation.getZ() && location.getZ() <= goalMaxLocation.getZ();
    }
    public ConfigManager getConfigManager(){
        return configManager;

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("撮影プラグインを終了しました。設定を保存します。");
        saveConfig();
    }


}
