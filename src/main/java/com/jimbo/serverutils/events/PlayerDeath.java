package com.jimbo.serverutils.events;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import com.jimbo.serverutils.Plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class PlayerDeath implements Listener {
    private static String[] deathMessages;

    public PlayerDeath() {
        //get death messages from file
        deathMessages = getDeathMessages();
    }

    @EventHandler
    //Run when a player dies.
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.deathMessage(MiniMessage.get().parse("<dark_red><italic>%s <red></italic> %s"
                                                   .formatted(event.getEntity().getName(), deathMessages[new Random().nextInt(deathMessages.length)])));
    }


    private String[] getDeathMessages() {
        File file = new File(Plugin.access.getDataFolder(), "deathMessages.txt");
        if(!file.exists()) {
            file.getParentFile().mkdirs();
            Plugin.access.saveResource("deathMessage.txt", false);
        }
        List<String> lines = new ArrayList<String>();

        try {
            Scanner fileReader = new Scanner(file);
            while(fileReader.hasNextLine()) {
                lines.add(fileReader.nextLine());
            }
            fileReader.close();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        return (String[])lines.toArray();
    }
}
