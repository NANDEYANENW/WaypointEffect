package org.nandeyanenw.waypointeffect;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class WaypointEffect extends JavaPlugin implements CommandExecutor {

    private ConfigManager configManager;
    private EffectManager effectManager;


    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        effectManager = new EffectManager(this);

        this.getCommand("wp").setExecutor(this);
        this.getCommand("effecttoggle").setExecutor(this);
        this.getCommand("forceeffect").setExecutor(this);

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

                Player targetPlayer = this.getServer().getPlayer(args[1]);
                if (targetPlayer == null) {
                    sender.sendMessage("§c指定したプレイヤーが見つかりません。");
                    return true;
                }

                switch (args[0].toLowerCase()) {
                    case "knockback":
                        effectManager.activateKnockbackEffect(targetPlayer);
                        break;
                    case "immobilize":
                        effectManager.activateImmobilizeEffect(targetPlayer);
                        break;
                    case "explosion":
                        effectManager.activateExplosionEffect(targetPlayer);
                        break;
                    case "nausea":
                        effectManager.activateNauseaEffect(targetPlayer);
                        break;
                    default:
                        sender.sendMessage("§c無効なエフェクト名です。");
                        return true;
                }

                sender.sendMessage("§e" + targetPlayer.getName() + " に " + args[0] + " エフェクトを適用しました。");
                return true;
            } else {
                sender.sendMessage("使用法: /forceeffect [effect] [player]");
            }
        }
       return true;
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        if (isPlayerInsideGoal(playerLocation) && player.getGameMode() == GameMode.ADVENTURE) {

            player.setGameMode(GameMode.ADVENTURE);

        }
    }

    private boolean isPlayerInsideGoal(Location location) {

        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        saveConfig();
    }

    public ConfigManager getConfigManager(){
        return configManager;

    }
}
