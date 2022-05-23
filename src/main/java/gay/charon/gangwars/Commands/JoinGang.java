package gay.charon.gangwars.Commands;

import gay.charon.gangwars.GangWars;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;

public class JoinGang implements CommandExecutor {

    FileConfiguration config = GangWars.plugin.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            String playerName = player.getName();
            if (player.hasPermission("gw.join")) {
                String team = args[0];
                if(team.equalsIgnoreCase("red")){
                    if(GangWars.redTeam.contains(playerName) || GangWars.blueTeam.contains(playerName)){
                        player.sendMessage("§8[§cGW§8] §fYou first have to leave this gang!");
                    } else {
                        GangWars.redTeam.add(playerName);
                        ItemStack Helm = new ItemStack(Material.LEATHER_HELMET);
                        LeatherArmorMeta HelmRed = (LeatherArmorMeta)Helm.getItemMeta();
                        HelmRed.setColor(Color.fromRGB(255, 0, 0));
                        Helm.setItemMeta(HelmRed);
                        ItemStack Chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                        LeatherArmorMeta ChestRed = (LeatherArmorMeta)Helm.getItemMeta();
                        ChestRed.setColor(Color.fromRGB(255, 0, 0));
                        Chest.setItemMeta(HelmRed);
                        ItemStack Legs = new ItemStack(Material.LEATHER_LEGGINGS);
                        LeatherArmorMeta LegsRed = (LeatherArmorMeta)Helm.getItemMeta();
                        LegsRed.setColor(Color.fromRGB(255, 0, 0));
                        Legs.setItemMeta(HelmRed);
                        ItemStack Boots = new ItemStack(Material.LEATHER_BOOTS);
                        LeatherArmorMeta BootsRed = (LeatherArmorMeta)Helm.getItemMeta();
                        BootsRed.setColor(Color.fromRGB(255, 0, 0));
                        Boots.setItemMeta(HelmRed);

                        player.getInventory().setHelmet(Helm);
                        player.updateInventory();
                        player.getInventory().setChestplate(Chest);
                        player.updateInventory();
                        player.getInventory().setLeggings(Legs);
                        player.updateInventory();
                        player.getInventory().setBoots(Boots);
                        player.updateInventory();
                        player.setPlayerListName("§cR§f∙§c "+playerName);

                        String arena = player.getWorld().getName();
                        World arenaWorld = Bukkit.getWorld(arena);
                        double x = config.getDouble(arena + ".Red.Spawn.X");
                        double y = config.getDouble(arena + ".Red.Spawn.Y");
                        double z = config.getDouble(arena + ".Red.Spawn.Z");
                        float yaw = (float) config.getDouble(arena + ".Red.Spawn.Pitch");
                        float pitch = (float) config.getDouble(arena + ".Red.Spawn.Pitch");
                        Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                        player.teleport(arenaLocation);

                        player.sendMessage("§8[§cGW§8] §fYou have successfully joined red gang!");
                    }
                } else if(team.equalsIgnoreCase("blue")){
                    if(GangWars.redTeam.contains(playerName) || GangWars.blueTeam.contains(playerName)){
                        player.sendMessage("§8[§cGW§8] §fYou first have to leave this gang!");
                    }else {
                        GangWars.blueTeam.add(playerName);
                        ItemStack Helm = new ItemStack(Material.LEATHER_HELMET);
                        LeatherArmorMeta HelmBlue = (LeatherArmorMeta)Helm.getItemMeta();
                        HelmBlue.setColor(Color.fromRGB(0, 0, 255));
                        Helm.setItemMeta(HelmBlue);
                        ItemStack Chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                        LeatherArmorMeta ChestBlue = (LeatherArmorMeta)Helm.getItemMeta();
                        ChestBlue.setColor(Color.fromRGB(0, 0, 255));
                        Chest.setItemMeta(ChestBlue);
                        ItemStack Legs = new ItemStack(Material.LEATHER_LEGGINGS);
                        LeatherArmorMeta LegsBlue = (LeatherArmorMeta)Helm.getItemMeta();
                        LegsBlue.setColor(Color.fromRGB(0, 0, 255));
                        Legs.setItemMeta(LegsBlue);
                        ItemStack Boots = new ItemStack(Material.LEATHER_BOOTS);
                        LeatherArmorMeta BootsBlue = (LeatherArmorMeta)Helm.getItemMeta();
                        BootsBlue.setColor(Color.fromRGB(0, 0, 255));
                        Boots.setItemMeta(BootsBlue);

                        player.getInventory().setHelmet(Helm);
                        player.updateInventory();
                        player.getInventory().setChestplate(Chest);
                        player.updateInventory();
                        player.getInventory().setLeggings(Legs);
                        player.updateInventory();
                        player.getInventory().setBoots(Boots);
                        player.updateInventory();
                        player.setPlayerListName("§bB§f∙§b "+playerName);

                        String arena = player.getWorld().getName();
                        World arenaWorld = Bukkit.getWorld(arena);
                        double x = config.getDouble(arena + ".Blue.Spawn.X");
                        double y = config.getDouble(arena + ".Blue.Spawn.Y");
                        double z = config.getDouble(arena + ".Blue.Spawn.Z");
                        float yaw = (float) config.getDouble(arena + ".Blue.Spawn.Pitch");
                        float pitch = (float) config.getDouble(arena + ".Blue.Spawn.Pitch");
                        Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                        player.teleport(arenaLocation);

                        player.sendMessage("§8[§cGW§8] §fYou have successfully joined blue gang!");
                    }
                } else {
                    player.sendMessage("§8[§cGW§8] §fYou have to choose blue or red gang!");
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
