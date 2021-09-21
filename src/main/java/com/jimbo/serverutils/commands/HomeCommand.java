package com.jimbo.serverutils.commands;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kyori.adventure.text.minimessage.MiniMessage;


public class HomeCommand implements TabExecutor {
    @Override
    //Run when the command is issued.
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { 
        Player player = (Player) sender;

        if (args.length > 0 && args[0].equals("set")) {
            player.setBedSpawnLocation(player.getLocation(), true);
            player.sendMessage(MiniMessage.get().parse("<yellow>Your spawn point has been set."));
            return true;
        }

        Location playerSpawn = player.getBedSpawnLocation();
        Location home = (playerSpawn != null) ?
                         playerSpawn :
                         player.getWorld().getSpawnLocation(); home.add(0.5f, 0f, 0.5f);

        player.teleport(home);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1 /*seconds*/ * 20, 10, false, false));
        player.sendMessage(MiniMessage.get().parse("<yellow>You have been teleported to your spawn."));

        return true;
    }

    @Override
    //Run for every tab completion.
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) { 
        if (args.length == 1) return new ArrayList<String>(Arrays.asList("set", "go"));
        return new ArrayList<String>();
    }
}
