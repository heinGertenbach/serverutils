package com.jimbo.serverutils.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class StartCommand implements TabExecutor {
    @Override
    //Run when the command is issued.
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        World playerWorld = player.getWorld();
        Location worldSpawn = playerWorld.getSpawnLocation();
        Random rand = new Random();

        Location destination = new Location(playerWorld,
                                            worldSpawn.getX() + (rand.nextInt(1500) + 1000),  //New X coordinate
                                            120,                                              //New Y coordinate
                                            worldSpawn.getZ() + (rand.nextInt(1500) + 1000)); //New Z coordinate                             
        player.teleport(destination);

        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10 /*seconds*/ * 20, 255, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1 /*seconds*/ * 20, 10, false, false));
        player.sendMessage(MiniMessage.get().parse("""
            <gold><italic>Your adventure begins now.
            <yellow></italic>You have been teleported to a random location away from spawn."""));

        return true;
    } 
    
    @Override
    //Run for every tab completion.
    public @Nullable List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return new ArrayList<String>();
    }
}

