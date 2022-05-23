package gay.charon.gangwars;

import gay.charon.gangwars.Commands.*;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public final class GangWars extends JavaPlugin implements Listener {

    public static GangWars plugin;
    public static List<String> redTeam = new ArrayList<String>();
    public static List<String> blueTeam = new ArrayList<String>();
    public static int kills;
    public static int count;
    public static int redKills;
    public static int blueKills;
    private BukkitTask task;
    public boolean isStarted;

    @Override
    public void onEnable() {
        System.out.println("[*] Starting GangWars ...");
        System.out.println("[!] Registering commands and events ...");
        plugin = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getCommand("joingang").setExecutor(new JoinGang());
        getCommand("leavegang").setExecutor(new LeaveGang());
        getCommand("setspawn").setExecutor(new SetSpawn());
        getCommand("bow").setExecutor(new GetBow());
        getCommand("ammo").setExecutor(new Ammo());
        System.out.println("[!] Registered commands and events");
        System.out.println("[!] Loading configs ...");
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        System.out.println("[!] Loaded all configs!");
        System.out.println("[*] Started GangWars!");
    }

    @Override
    public void onDisable() {
        System.out.println("[*] Stopping GangWars ...");
        System.out.println("[!] Removing players from gangs...");
        for(Player player : getServer().getOnlinePlayers()){
            String playerName = player.getName();
            if(redTeam.contains(playerName)) {
                redTeam.remove(playerName);
                player.setPlayerListName("§f"+playerName);
            } else if(blueTeam.contains(playerName)) {
                blueTeam.remove(playerName);
                player.setPlayerListName("§f"+playerName);
            }
        }
        System.out.println("[!] Successfully removed players from gang");
        System.out.println("[*] Stopped GangWars!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                Scoreboard scoreboard = manager.getNewScoreboard();

                Objective objective = scoreboard.registerNewObjective("gangwars", "dummy");
                objective.setDisplayName("§c§lGangWars");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                Score spacing = objective.getScore(" ");
                spacing.setScore(10);

                Score username = objective.getScore("§c§lIGN §8» §f" + player.getName());
                username.setScore(9);

                if(redTeam.contains(player.getName())){
                    Score gang = objective.getScore("§c§lGang §8» §cRed");
                    gang.setScore(8);
                } else if(blueTeam.contains(player.getName())){
                    Score gang = objective.getScore("§c§lGang §8» §9Blue");
                    gang.setScore(8);
                } else if(!blueTeam.contains(player.getName()) && !redTeam.contains(player.getName())){
                    Score gang = objective.getScore("§c§lGang §8» §fNone");
                    gang.setScore(8);
                }

                Score timer = objective.getScore("§c§lTimer §8» §f" + count + "s");
                timer.setScore(7);

                Score spacing2 = objective.getScore("   ");
                spacing2.setScore(6);

                Score rk = objective.getScore("§c§lRed Kills §8» §f" + redKills + "");
                rk.setScore(5);

                Score bk = objective.getScore("§c§lBlue Kills §8» §f" + blueKills + "");
                bk.setScore(4);

                Score spacing3 = objective.getScore("    ");
                spacing3.setScore(3);

                Score online = objective.getScore("§c§lOnline §8» §f" + Bukkit.getServer().getOnlinePlayers().size() + "§8/§f" +Bukkit.getServer().getMaxPlayers());
                online.setScore(2);

                Score serverinfo = objective.getScore("§c§lIP §8» §f" + getConfig().getString("Server.Ip"));
                serverinfo.setScore(1);

                player.setScoreboard(scoreboard);
            }
        }, 0L, 20L);
    }

    //On quit remove player from team
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        if(redTeam.contains(playerName)) {
            redTeam.remove(playerName);
            player.setPlayerListName("§f"+playerName);
        } else if(blueTeam.contains(playerName)) {
            blueTeam.remove(playerName);
            player.setPlayerListName("§f"+playerName);
        }
    }


    //No Friendly fire
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player whoWasHit = (Player) e.getEntity();
            Player whoHit = (Player) e.getDamager();

            if(redTeam.contains(whoHit.getName()) && redTeam.contains(whoWasHit.getName())) {
                e.setCancelled(true);
                whoHit.sendMessage("§8[§GW§8] §fYou can't attack your gang members!");
            } else if (blueTeam.contains(whoHit.getName()) && blueTeam.contains(whoWasHit.getName())){
                e.setCancelled(true);
                whoHit.sendMessage("§8[§GW§8] §fYou can't attack your gang members!");
            }
        }
    }

    //Join gang by clicking on sign
    @EventHandler
    public void onPlayerClickSign(PlayerInteractEvent e){
        Player player = e.getPlayer();
        String playerName = player.getName();
        if(e.getClickedBlock().getType() == Material.SIGN || e.getClickedBlock().getType() == Material.SIGN_POST || e.getClickedBlock().getType() == Material.WALL_SIGN && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Sign sign = (Sign) e.getClickedBlock().getState();
            if (sign.getLine(0).equalsIgnoreCase("[JOIN GANG]")) {
                String line = sign.getLine(1);
                if(line.equalsIgnoreCase("red")){
                    if(GangWars.redTeam.contains(playerName) || GangWars.blueTeam.contains(playerName)) {
                        player.sendMessage("§8[§cGW§8] §fYou first have to leave this gang!");
                    } else{
                        redTeam.add(playerName);
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
                        player.setPlayerListName("§cR§f∙§c " + playerName);

                        String arena = player.getWorld().getName();
                        World arenaWorld = Bukkit.getWorld(arena);
                        double x = getConfig().getDouble(arena + ".Red.Spawn.X");
                        double y = getConfig().getDouble(arena + ".Red.Spawn.Y");
                        double z = getConfig().getDouble(arena + ".Red.Spawn.Z");
                        float yaw = (float) getConfig().getDouble(arena + ".Red.Spawn.Pitch");
                        float pitch = (float) getConfig().getDouble(arena + ".Red.Spawn.Pitch");
                        Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                        player.teleport(arenaLocation);

                        player.sendMessage("§8[§cGW§8] §fYou have successfully joined red gang!");
                    }
                } else if (line.equalsIgnoreCase("blue")){
                    if(GangWars.redTeam.contains(playerName) || GangWars.blueTeam.contains(playerName)) {
                        player.sendMessage("§8[§cGW§8] §fYou first have to leave this gang!");
                    } else {
                        blueTeam.add(playerName);
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
                        player.setPlayerListName("§bB§f∙§b " + playerName);

                        String arena = player.getWorld().getName();
                        World arenaWorld = Bukkit.getWorld(arena);
                        double x = getConfig().getDouble(arena + ".Blue.Spawn.X");
                        double y = getConfig().getDouble(arena + ".Blue.Spawn.Y");
                        double z = getConfig().getDouble(arena + ".Blue.Spawn.Z");
                        float yaw = (float) getConfig().getDouble(arena + ".Blue.Spawn.Pitch");
                        float pitch = (float) getConfig().getDouble(arena + ".Blue.Spawn.Pitch");
                        Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                        player.teleport(arenaLocation);

                        player.sendMessage("§8[§cGW§8] §fYou have successfully joined blue gang!");
                    }
                }
            } else if(sign.getLine(0).equalsIgnoreCase("[LEAVE GANG]")) {
                if (GangWars.blueTeam.contains(playerName)) {
                    blueTeam.remove(playerName);
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
                    player.setPlayerListName("§f" + playerName);

                    String arena = player.getWorld().getName();
                    World arenaWorld = Bukkit.getWorld(arena);
                    double x = getConfig().getDouble(arena + ".Spawn.X");
                    double y = getConfig().getDouble(arena + ".Spawn.Y");
                    double z = getConfig().getDouble(arena + ".Spawn.Z");
                    float yaw = (float) getConfig().getDouble(arena + ".Spawn.Pitch");
                    float pitch = (float) getConfig().getDouble(arena + ".Spawn.Pitch");
                    Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                    player.teleport(arenaLocation);

                    player.sendMessage("§8[§cGW§8] §fYou have successfully left blue gang!");
                } else if (GangWars.redTeam.contains(playerName)) {
                    redTeam.remove(playerName);
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
                    player.setPlayerListName("§f" + playerName);

                    String arena = player.getWorld().getName();
                    World arenaWorld = Bukkit.getWorld(arena);
                    double x = getConfig().getDouble(arena + ".Spawn.X");
                    double y = getConfig().getDouble(arena + ".Spawn.Y");
                    double z = getConfig().getDouble(arena + ".Spawn.Z");
                    float yaw = (float) getConfig().getDouble(arena + ".Spawn.Pitch");
                    float pitch = (float) getConfig().getDouble(arena + ".Spawn.Pitch");
                    Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                    player.teleport(arenaLocation);

                    player.sendMessage("§8[§cGW§8] §fYou have successfully left red gang!");
                }
            } else if(sign.getLine(0).equalsIgnoreCase("[BOW]")){
                if(redTeam.contains(playerName) || blueTeam.contains(playerName)) {
                    player.getInventory().addItem(new ItemStack(Material.BOW, 1));
                } else {
                    player.sendMessage("§8[§cGW§8] §fYou have to be in gang!");
                }
            } else if(sign.getLine(0).equalsIgnoreCase("[AMMO]")){
                if (redTeam.contains(playerName) || blueTeam.contains(playerName)) {
                    player.getInventory().addItem(new ItemStack(Material.ARROW, 128));
                } else {
                    player.sendMessage("§8[§cGW§8] §fYou have to be in gang!");
                }
            }
        }
    }

    //Change damage of arrow hit so players are 1 shot
    @EventHandler
    public void changeArrowDamage(EntityDamageByEntityEvent e){
        if(e.getCause() == EntityDamageEvent.DamageCause.PROJECTILE){
            if(e.getDamager() instanceof Arrow){
                Arrow arrow = (Arrow) e.getDamager();
                e.setDamage(99999999);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Player player = event.getEntity();
        if(player instanceof Player) {
            if (redTeam.contains(player.getName())) {
                ItemStack Helm = new ItemStack(Material.LEATHER_HELMET);
                LeatherArmorMeta HelmRed = (LeatherArmorMeta) Helm.getItemMeta();
                HelmRed.setColor(Color.fromRGB(255, 0, 0));
                Helm.setItemMeta(HelmRed);
                ItemStack Chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                LeatherArmorMeta ChestRed = (LeatherArmorMeta) Helm.getItemMeta();
                ChestRed.setColor(Color.fromRGB(255, 0, 0));
                Chest.setItemMeta(HelmRed);
                ItemStack Legs = new ItemStack(Material.LEATHER_LEGGINGS);
                LeatherArmorMeta LegsRed = (LeatherArmorMeta) Helm.getItemMeta();
                LegsRed.setColor(Color.fromRGB(255, 0, 0));
                Legs.setItemMeta(HelmRed);
                ItemStack Boots = new ItemStack(Material.LEATHER_BOOTS);
                LeatherArmorMeta BootsRed = (LeatherArmorMeta) Helm.getItemMeta();
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
                player.setHealth(20);
                player.setFoodLevel(20);
                for (PotionEffect effect : player.getActivePotionEffects())
                    player.removePotionEffect(effect.getType());
                player.setFireTicks(0);
                String arena = player.getWorld().getName();
                World arenaWorld = Bukkit.getWorld(arena);
                double x = getConfig().getDouble(arena + ".Red.Spawn.X");
                double y = getConfig().getDouble(arena + ".Red.Spawn.Y");
                double z = getConfig().getDouble(arena + ".Red.Spawn.Z");
                float yaw = (float) getConfig().getDouble(arena + ".Red.Spawn.Pitch");
                float pitch = (float) getConfig().getDouble(arena + ".Red.Spawn.Pitch");
                Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                player.teleport(arenaLocation);
            } else if (blueTeam.contains(player.getName())) {
                ItemStack Helm = new ItemStack(Material.LEATHER_HELMET);
                LeatherArmorMeta HelmBlue = (LeatherArmorMeta) Helm.getItemMeta();
                HelmBlue.setColor(Color.fromRGB(0, 0, 255));
                Helm.setItemMeta(HelmBlue);
                ItemStack Chest = new ItemStack(Material.LEATHER_CHESTPLATE);
                LeatherArmorMeta ChestBlue = (LeatherArmorMeta) Helm.getItemMeta();
                ChestBlue.setColor(Color.fromRGB(0, 0, 255));
                Chest.setItemMeta(ChestBlue);
                ItemStack Legs = new ItemStack(Material.LEATHER_LEGGINGS);
                LeatherArmorMeta LegsBlue = (LeatherArmorMeta) Helm.getItemMeta();
                LegsBlue.setColor(Color.fromRGB(0, 0, 255));
                Legs.setItemMeta(LegsBlue);
                ItemStack Boots = new ItemStack(Material.LEATHER_BOOTS);
                LeatherArmorMeta BootsBlue = (LeatherArmorMeta) Helm.getItemMeta();
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
                player.setHealth(20);
                player.setFoodLevel(20);
                for (PotionEffect effect : player.getActivePotionEffects())
                    player.removePotionEffect(effect.getType());
                player.setFireTicks(0);
                String arena = player.getWorld().getName();
                World arenaWorld = Bukkit.getWorld(arena);
                double x = getConfig().getDouble(arena + ".Blue.Spawn.X");
                double y = getConfig().getDouble(arena + ".Blue.Spawn.Y");
                double z = getConfig().getDouble(arena + ".Blue.Spawn.Z");
                float yaw = (float) getConfig().getDouble(arena + ".Blue.Spawn.Pitch");
                float pitch = (float) getConfig().getDouble(arena + ".Blue.Spawn.Pitch");
                Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                player.teleport(arenaLocation);
            } else {
                System.out.println("[GW] How the fuck did this retard die ???");
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("gw.setspawn")) {
                if(isStarted == false) {
                    isStarted = true;
                    count = 900;
                    task = new BukkitRunnable() {
                        @Override
                        public void run() {

                            if(redKills == 200 && blueKills<200){
                                Bukkit.getServer().broadcastMessage("§8[§cGW§8] §fRed team won this game!");
                            } else if (blueKills == 200 && redKills<200){
                                Bukkit.getServer().broadcastMessage("§8[§cGW§8] §Blue team won this game!");
                            } else if (blueKills == 200 && redKills == 200){
                                Bukkit.getServer().broadcastMessage("§8[§cGW§8] §It is a tie, both red and blue team scored 200 kills!");
                            }

                            if (count < 1 ) {

                                redKills = 0;
                                blueKills = 0;
                                isStarted = false;

                                Bukkit.getServer().broadcastMessage("§8[§cGW§8] §fGame ended, another game will start soon!");

                                if(redKills>blueKills){
                                    Bukkit.getServer().broadcastMessage("§8[§cGW§8] §fRed team won this game!");
                                } else {
                                    Bukkit.getServer().broadcastMessage("§8[§cGW§8] §Blue team won this game!");
                                }

                                for(String playerName:redTeam){
                                    redTeam.remove(playerName);
                                    Player playerRed = Bukkit.getServer().getPlayer(playerName);
                                    String arena = player.getWorld().getName();
                                    World arenaWorld = Bukkit.getWorld(arena);
                                    double x = getConfig().getDouble(arena + ".Spawn.X");
                                    double y = getConfig().getDouble(arena + ".Spawn.Y");
                                    double z = getConfig().getDouble(arena + ".Spawn.Z");
                                    float yaw = (float) getConfig().getDouble(arena + ".Spawn.Pitch");
                                    float pitch = (float) getConfig().getDouble(arena + ".Spawn.Pitch");
                                    Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                                    playerRed.teleport(arenaLocation);
                                }

                                for(String PlayerName2:blueTeam){
                                    blueTeam.remove(PlayerName2);
                                    Player playerBlue = Bukkit.getServer().getPlayer(PlayerName2);
                                    String arena = player.getWorld().getName();
                                    World arenaWorld = Bukkit.getWorld(arena);
                                    double x = getConfig().getDouble(arena + ".Spawn.X");
                                    double y = getConfig().getDouble(arena + ".Spawn.Y");
                                    double z = getConfig().getDouble(arena + ".Spawn.Z");
                                    float yaw = (float) getConfig().getDouble(arena + ".Spawn.Pitch");
                                    float pitch = (float) getConfig().getDouble(arena + ".Spawn.Pitch");
                                    Location arenaLocation = new Location(arenaWorld, x, y, z, yaw, pitch);
                                    playerBlue.teleport(arenaLocation);
                                }
                                task.cancel();
                                return;
                            } else if(count == 900){
                                Bukkit.getServer().broadcastMessage("§8[§cGW§8] §fGame started, use /joingang <red/blue> to play!");
                            } else if(count == 600){
                                Bukkit.getServer().broadcastMessage("§8[§cGW§8] §fGame will end in 10 minutes, hurry up!");
                            } else if(count == 300){
                                Bukkit.getServer().broadcastMessage("§8[§cGW§8] §fGame will end in 5 minutes, hurry up!");
                            }
                            count--;
                        }
                    }.runTaskTimer(this, 0, 20);
                } else{
                    player.sendMessage("§8[§cGW§8] §fGame is already started, please wait!");
                }
            } else {
                player.sendMessage("§8[§cGW§8] §fYou don't have permission to run this command!");
            }
        } else {
            System.out.println("[!] You have to run this command as player...");
        }
        return false;
    }

    @EventHandler
    public void onKill(PlayerDeathEvent e)
    {
        String killer = e.getEntity().getKiller().getName();
        LivingEntity killed = e.getEntity();

        if(killed instanceof Player) {
            if (redTeam.contains(killer)) {
                redKills++;
            } else if (blueTeam.contains(killer)) {
                blueKills++;
            }
        }
    }

}
