package com.jimbo.serverutils.events;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerDeath implements Listener {
    final private String[] deathMessages = {" has suffered a painful death", " lived a good life", " has died", " kicked the bucket", " bit the bullet", " GG'd", " got rekt lmao", " was bad and should feel bad"};

    @EventHandler
    //Run when a player dies.
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.deathMessage(MiniMessage.get().parse("<dark_red><italic>%s <red></italic>%s"
                                                   .formatted(event.getEntity().getName(), deathMessages[new Random().nextInt(deathMessages.length)])));
    }
}
