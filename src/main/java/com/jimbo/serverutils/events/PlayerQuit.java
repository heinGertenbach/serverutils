package com.jimbo.serverutils.events;

import com.jimbo.serverutils.Plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerQuit implements Listener {
    @EventHandler
    //Run when a player leaves the server
    public void onPlayerLeave(PlayerQuitEvent event) {
        Plugin.trades.keySet().forEach(trade -> { if (trade.contains(event.getPlayer().getName())) Plugin.trades.remove(trade); });

        event.quitMessage(MiniMessage.get().parse("<green>Goodbye <dark_green><italic>" + event.getPlayer().getName()));
    }
}
