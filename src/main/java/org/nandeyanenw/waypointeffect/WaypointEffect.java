package org.nandeyanenw.waypointeffect;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
                if (!player.hasPermission("wp.effecttoggle")) {
                    player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
                    return true;
                }
                String effect = args[0];
                boolean enabled = args[1].equalsIgnoreCase("enable");
                effectManager.setEffectEnabled(effect, enabled);
                sender.sendMessage(effect + " エフェクトを " + (enabled ? "enabled" : "disabled"));
                return true;
            } else {
                sender.sendMessage("使用法: /effecttoggle [effect] [enable|disable]");
            }
        }

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
