package org.nandeyanenw.waypointeffect;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WaypointEffect extends JavaPlugin implements CommandExecutor {

    private ConfigManager configManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        this.getCommand("wp").setExecutor(this);

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cこのコマンドはプレイヤーからのみ実行できます。");
            return true;
        }
        Player player = (Player) sender;
        if (cmd.getName().equalsIgnoreCase("wp") && args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")){
                configManager.loadConfig();
                sender.sendMessage("設定を再読み込みしました。");
                return true;
            }
        }

        if (!player.hasPermission("wp.reload")){
            player.sendMessage("§cあなたにはこのコマンドを実行する権限がありません。");
            return true;

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
