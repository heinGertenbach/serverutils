package com.jimbo.serverutils.events;

import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerJoin implements Listener { 
    @EventHandler
    //Run when a player joins the server.
    public void onPlayerJoin(PlayerJoinEvent event) { 
        event.getPlayer().sendPlayerListHeader(MiniMessage.get().parse("<bold><aqua>-- Jimbo's SMP Server --"));
        event.getPlayer().playerListName(MiniMessage.get().parse("%s%s <red>%s"
                                                          .formatted((event.getPlayer().isOp()) ? 
                                                                        "<yellow>" : "<green>", 
                                                                    event.getPlayer().getName(),
                                                                    event.getPlayer().getStatistic(Statistic.DEATHS))));
        event.joinMessage(MiniMessage.get().parse("""
            <aqua>Welcome to the server <dark_aqua><italic>%s.
            <yellow></italic>New player? Use <gold><italic>/start <yellow></italic>to begin your journey."""
        .formatted(event.getPlayer().getName())));
    }
}