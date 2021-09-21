package com.jimbo.serverutils.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.jimbo.serverutils.Plugin;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class TradeCommand implements TabExecutor {
    Map<String, Map<String, Object>> trades = Plugin.trades;

    @Override
    //Run when the command is issued.
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        switch ((args.length > 0) ? args[0] : "") { 
            case "offer": return offerTrade(sender, args);
            case "accept": return acceptTrade(sender, args);
            case "reject": return rejectTrade(sender, args);
            
            default:
                sender.sendMessage(errorMessage()); 
                return true;
        }
    }

    @Override
    //Run for every tab completion.
    public @Nullable List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        Stream<String> items = Arrays.stream(Material.values()).map(item -> item.getKey().asString());
    
        if (args.length == 1) return new ArrayList<String>(Arrays.asList("offer", "accept", "reject"));
        if (args.length == 2) return Plugin.access.getServer().getOnlinePlayers().stream()
                                                  .map(player -> player.getName())
                                                  .filter(name -> name != sender.getName())
                                                  .collect(Collectors.toList());
        if (args[0].equals("offer")) {
            if (args.length == 3 || args.length == 5) return items.filter(item -> item.contains(args[args.length - 1])).collect(Collectors.toList());                                                                 
            if (args.length == 4 || args.length == 6) return new ArrayList<String>(Arrays.asList("[<count>]"));
        }

        return new ArrayList<String>();  
    }

    private boolean offerTrade(CommandSender sender, String[] args) {
        if (args.length < 6) { 
            sender.sendMessage(errorMessage(8)); //Invalid arguments
            return true;
        }

        int offerCount = Integer.parseInt(args[3]);
        int requestCount = Integer.parseInt(args[5]);
        if (offerCount > 64 || offerCount < 0 || requestCount > 64 || requestCount < 0) {
            sender.sendMessage(errorMessage(4)); //Stack size out of bounds
            return true;
        };

        Player player = (Player) sender;
        Player target = Plugin.access.getServer().getPlayer(args[1]);
        if (target == null) { 
            sender.sendMessage(errorMessage(3)); //Player unavaliable
            return true;
        }

        Material offerMaterial   = Material.matchMaterial(args[2]);
        Material requestMaterial = Material.matchMaterial(args[4]);
        if (offerMaterial == null || requestMaterial == null) { 
            sender.sendMessage(errorMessage(5)); //Invalid item
            return true;
        }
        
        ItemStack offer   = new ItemStack(offerMaterial,  offerCount);
        ItemStack request = new ItemStack(requestMaterial, requestCount);

        if (!player.getInventory().containsAtLeast(offer, offerCount)) {
            sender.sendMessage(errorMessage(2)); //Player does not have enough to trade
            return true;
        }
                  
        String offerName   = String.join(" ", 
                             Arrays.stream(offer.getType().getKey().toString().split(":")[1].split("_"))
                                   .map(word -> word.substring(0, 1).toUpperCase().concat(word.substring(1)))
                                   .collect(Collectors.toList())) + "(s)";
        String requestName = String.join(" ", 
                             Arrays.stream(request.getType().getKey().toString().split(":")[1].split("_"))
                                   .map(word -> word.substring(0, 1).toUpperCase().concat(word.substring(1)))
                                   .collect(Collectors.toList())) + "(s)";

        player.sendMessage(tradeMessage(1, target.getName(), Integer.toString(offerCount), offerName, Integer.toString(requestCount), requestName));
        target.sendMessage(tradeMessage(2, player.getName(), Integer.toString(offerCount), offerName, Integer.toString(requestCount), requestName));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.8f);
        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.8f);

        String tradeName = player.getName() + "->" + target.getName();
        Map<String, Object> tradeData = new HashMap<String, Object>();
        tradeData.put("offer", offer);
        tradeData.put("request", request);
        tradeData.put("target", target);
        tradeData.put("sender", player);

        trades.put(tradeName, tradeData);
        return true;
    }

    private boolean acceptTrade(CommandSender sender, String[] args) {
        if (args.length < 2) { 
            sender.sendMessage(errorMessage(7)); //Player target not supplied
            return true;
        }

        Player player = (Player) sender;
        Player target = Plugin.access.getServer().getPlayer(args[1]);
        if (target == null) { 
            sender.sendMessage(errorMessage(3)); //Player unavaliable
            return true;
        }

        String tradeName = target.getName() + "->" + player.getName();
        if (!trades.containsKey(tradeName)) {
            sender.sendMessage(errorMessage(1)); //No pending trade to accept
            return true;
        }

        Map<String, Object> tradeData = trades.get(tradeName);
        ItemStack offer   = (ItemStack) tradeData.get("offer");
        ItemStack request = (ItemStack) tradeData.get("request");

        if (!player.getInventory().containsAtLeast(request, request.getAmount())) {
            sender.sendMessage(errorMessage(2)); //Player does not have enough to trade
            return true;
        }

        if (!player.getInventory().containsAtLeast(offer, offer.getAmount())) {
            sender.sendMessage(errorMessage(9)); //Other player does not have enough to trade
            trades.remove(tradeName);
            return true;
        }
        
        player.sendMessage(tradeMessage(3, target.getName()));
        target.sendMessage(tradeMessage(4, player.getName()));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.2f);

        target.getInventory().removeItem(offer);
        player.getInventory().removeItem(request);
        trades.remove(tradeName);

        if (player.getInventory().firstEmpty() != -1) player.getInventory().addItem(offer);
        else { 
            player.getWorld().dropItem(player.getLocation(), offer);
            player.sendMessage(errorMessage(6)); //Full inventory warning
        }

        if (target.getInventory().firstEmpty() != -1) target.getInventory().addItem(request);
        else {
            target.getWorld().dropItem(target.getLocation(), request);
            target.sendMessage(errorMessage(6)); //Full inventory warning
        }
        
        player.updateInventory();
        target.updateInventory();
        return true;
    }

    private boolean rejectTrade(CommandSender sender, String[] args) {
        if (args.length < 2) { 
            sender.sendMessage(errorMessage(7));
            return true;
        }

        Player player = (Player) sender;
        Player target = Plugin.access.getServer().getPlayer(args[1]);
        if (target == null) { 
            sender.sendMessage(errorMessage(3)); //Player unavaliable
            return true;
        }

        String tradeName = target.getName() + "->" + player.getName();
        if (!trades.containsKey(tradeName)) {
            sender.sendMessage(errorMessage(1)); //No pending trade to accept
            return true;
        }

        player.sendMessage(tradeMessage(5, target.getName()));
        target.sendMessage(tradeMessage(6, player.getName()));
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.2f);
        target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.2f);

        return true;
    }

    private Component tradeMessage(int type, String... args) { 
        switch (type) { 
            case 1: return MiniMessage.get().parse("""
                <green>Sent trade offer to <dark_green><italic>%s</italic>.
                <yellow>Offering <gold><italic>%s %s</italic><yellow>, for <gold><italic>%s %s</italic>."""
            .formatted(args[0], args[1], args[2], args[3], args[4]));

            case 2: return MiniMessage.get().parse("""
                <green>Recieved trade offer from <dark_green><italic>%s</italic>.
                <yellow>Offering <gold><italic>%s %s</italic><yellow>, for <gold><italic>%s %s</italic>."""
            .formatted(args[0], args[1], args[2], args[3], args[4]));
                   
            case 3: return MiniMessage.get().parse("""
                <green>You accepted <dark_green><italic>%s's <green></italic>offer."""
            .formatted(args[0]));

            case 4: return MiniMessage.get().parse("""
                <dark_green><italic>%s <green></italic>accepted your trade offer."""
            .formatted(args[0]));

            case 5: return MiniMessage.get().parse("""
                <red>You rejected <dark_red><italic>%s's <red></italic>offer."""
            .formatted(args[0]));   
            
            case 6: return MiniMessage.get().parse("""
                <dark_red><italic>%s <red></italic>rejected your trade offer."""
            .formatted(args[0]));

            default: return Component.empty();
        }
    }

    private Component errorMessage(int type) {
        Component message;
        switch (type) {
            case 1: message = MiniMessage.get().parse("<red>There is no pending trade from that user."); break;
            case 2: message = MiniMessage.get().parse("<red>You do not have enough of that item to trade."); break;
            case 3: message = MiniMessage.get().parse("<red>That player is not avaliable to trade."); break;
            case 4: message = MiniMessage.get().parse("<red>Item stacks must be between 0 and 64 in size."); break;
            case 5: message = MiniMessage.get().parse("<red>That is not a valid item."); break;
            case 6: message = MiniMessage.get().parse("<orange>Your inventory is full. Your item has been dropped at your location."); break;
            case 7: message = MiniMessage.get().parse("<red>You did not supply a player to accept/reject the trade from."); break;
            case 8: message = MiniMessage.get().parse("<red>That is not a valid offer."); break;
            case 9: message = MiniMessage.get().parse("<red>The other player no longer has the item to trade. <dark_red>Cancelling trade.");

            default: message = Component.empty(); break;
        }         
        
        return message.append(MiniMessage.get().parse("""
            <yellow>\nUsage: 
            <italic><gold><pre>/trade <offer|accept|reject> <player> [offer] [count] [request] [count]"""));
    }

    private Component errorMessage() {
        return MiniMessage.get().parse("""
            <red>That is not a valid trade command.
            <yellow>Use <gold><italic>/trade offer <yellow></italic> to offer a trade,
            or <gold><italic>/trade [accept|reject] <yellow></italic>to respond to a trade offer.""");
    }
}