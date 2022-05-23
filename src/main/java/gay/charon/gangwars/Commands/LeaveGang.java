package gay.charon.gangwars.Commands;

import gay.charon.gangwars.GangWars;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class LeaveGang implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getName();
            if (player.hasPermission("gw.leave")) {
                if(GangWars.blueTeam.contains(playerName)){
                    GangWars.blueTeam.remove(playerName);
                    ItemStack Helm = new ItemStack(Material.AIR);
                    ItemStack Chest = new ItemStack(Material.AIR);
                    ItemStack Legs = new ItemStack(Material.AIR);
                    ItemStack Boots = new ItemStack(Material.AIR);

                    player.getInventory().setHelmet(Helm);
                    player.updateInventory();
                    player.getInventory().setChestplate(Chest);
                    player.updateInventory();
                    player.getInventory().setLeggings(Legs);
                    player.updateInventory();
                    player.getInventory().setBoots(Boots);
                    player.updateInventory();
                    player.setPlayerListName("§f"+playerName);
                    player.getInventory().clear();
                    player.updateInventory();
                    player.sendMessage("§8[§cGW§8] §fYou have successfully left blue gang!");
                } else if (GangWars.redTeam.contains(playerName)){
                    GangWars.redTeam.remove(playerName);
                    ItemStack Helm = new ItemStack(Material.AIR);
                    ItemStack Chest = new ItemStack(Material.AIR);
                    ItemStack Legs = new ItemStack(Material.AIR);
                    ItemStack Boots = new ItemStack(Material.AIR);

                    player.getInventory().setHelmet(Helm);
                    player.updateInventory();
                    player.getInventory().setChestplate(Chest);
                    player.updateInventory();
                    player.getInventory().setLeggings(Legs);
                    player.updateInventory();
                    player.getInventory().setBoots(Boots);
                    player.updateInventory();
                    player.getInventory().clear();
                    player.updateInventory();
                    player.setPlayerListName("§f"+playerName);
                    player.sendMessage("§8[§cGW§8] §fYou have successfully left red gang!");
                } else {
                    player.sendMessage("§8[§cGW§8] §fYou are not member of any gang!");
                }
            } else {
                player.sendMessage("§8[§cGW§8] §fYou don't have permission to run this command!");
            }
        } else {
            System.out.println("[GW] You have to run this command as player...");
        }



        return false;
    }
}
