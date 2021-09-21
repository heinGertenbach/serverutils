package com.jimbo.serverutils;

import java.util.HashMap;
import java.util.Map;

import com.jimbo.serverutils.commands.HomeCommand;
import com.jimbo.serverutils.commands.SpawnCommand;
import com.jimbo.serverutils.commands.StartCommand;
import com.jimbo.serverutils.commands.TradeCommand;
import com.jimbo.serverutils.events.PlayerDeath;
import com.jimbo.serverutils.events.PlayerJoin;
import com.jimbo.serverutils.events.PlayerQuit;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    //Static access to plugin data:
    public static Plugin access;
    { access = this; }

    //Trade storage:
    public static Map<String, Map<String, Object>> trades = new HashMap<String, Map<String, Object>>();

    //Commonly used JavaPlugin Objects:
    PluginManager manager = getServer().getPluginManager();
    ConsoleCommandSender console = getServer().getConsoleSender();

    @Override
    //Run when the plugin is enabled. 
    public void onEnable() {
        //Event Listeners:
        manager.registerEvents(new PlayerJoin(), this);
        manager.registerEvents(new PlayerDeath(), this);
        manager.registerEvents(new PlayerQuit(), this);

        //Command Executors:
        getCommand("trade").setExecutor(new TradeCommand());
        getCommand("start").setExecutor(new StartCommand());
        getCommand("spawn").setExecutor(new SpawnCommand());
        getCommand("home").setExecutor(new HomeCommand());

        console.sendMessage("[serverutils] Plugin is enabled.");
    }

    @Override
    //Run when the plugin is disabled.
    public void onDisable() {
        console.sendMessage("[serverutils] Plugin is disabled.");
    }
}
