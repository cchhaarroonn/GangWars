package gay.charon.gangwars.Commands;

import gay.charon.gangwars.GangWars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {

    FileConfiguration config = GangWars.plugin.getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if(player.hasPermission("gw.setspawn")) {
                if(args[0].equalsIgnoreCase("red")) {
                    String name = player.getWorld().getName();
                    double x = player.getLocation().getX();
                    double y = player.getLocation().getY();
                    double z = player.getLocation().getZ();
                    float yaw = player.getLocation().getYaw();
                    float pitch = player.getLocation().getPitch();
                    config.set(name + ".Red.Spawn.X", x);
                    config.set(name + ".Red.Spawn.Y", y);
                    config.set(name + ".Red.Spawn.Z", z);
                    config.set(name + ".Red.Spawn.Yaw", yaw);
                    config.set(name + ".Red.Spawn.Pitch", pitch);
                    GangWars.plugin.saveConfig();
                    player.sendMessage("§8[§cGW§8] §fSuccessfully set spawn location to your current location");
                } else if(args[0].equalsIgnoreCase("blue")){
                    String name = player.getWorld().getName();
                    double x = player.getLocation().getX();
                    double y = player.getLocation().getY();
                    double z = player.getLocation().getZ();
                    float yaw = player.getLocation().getYaw();
                    float pitch = player.getLocation().getPitch();
                    config.set(name + ".Blue.Spawn.X", x);
                    config.set(name + ".Blue.Spawn.Y", y);
                    config.set(name + ".Blue.Spawn.Z", z);
                    config.set(name + ".Blue.Spawn.Yaw", yaw);
                    config.set(name + ".Blue.Spawn.Pitch", pitch);
                    GangWars.plugin.saveConfig();
                    player.sendMessage("§8[§cGW§8] §fSuccessfully set spawn location to your current location");
                } else if(args[0].equalsIgnoreCase("default")){

                    String name = player.getWorld().getName();
                    double x = player.getLocation().getX();
                    double y = player.getLocation().getY();
                    double z = player.getLocation().getZ();
                    float yaw = player.getLocation().getYaw();
                    float pitch = player.getLocation().getPitch();
                    config.set(name + ".Spawn.X", x);
                    config.set(name + ".Spawn.Y", y);
                    config.set(name + ".Spawn.Z", z);
                    config.set(name + ".Spawn.Yaw", yaw);
                    config.set(name + ".Spawn.Pitch", pitch);
                    GangWars.plugin.saveConfig();
                    player.sendMessage("§8[§cGW§8] §fSuccessfully set spawn location to your current location");
                }
            }else {
                player.sendMessage("§8[§cGW§8] §fYou don't have permission to run this command!");
            }
        } else {
            System.out.println("[!] You have to run this command as player ...");
        }


        return false;
    }
}
