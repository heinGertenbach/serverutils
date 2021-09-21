package com.jimbo.serverutils.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.minimessage.MiniMessage;

public class SpawnCommand implements TabExecutor {
    @Override
    //Run when the commnand is issued
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        Location spawn = player.getWorld().getSpawnLocation().add(0.5f, 0f, 0.5f); spawn.setYaw(-90f);

        player.teleport(spawn);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 1 /*seconds*/ * 20, 10, false, false));
        player.sendMessage(MiniMessage.get().parse("<yellow>You have been teleported to the world spawn."));

        return true;
    }
    
    @Override
    //Run for every tab completion
    public @Nullable List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return new ArrayList<String>();
    }
}
