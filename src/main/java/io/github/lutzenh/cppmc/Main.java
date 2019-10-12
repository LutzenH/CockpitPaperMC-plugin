package io.github.lutzenh.cppmc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this plugin is to export data about the server into a bunch of .json files,
 * which can be read using CockpitPaperMC or some other program that works with .json's.
 *
 * @author LutzenH
 * @version 1.0-SNAPSHOT
 * @since 1.0-SNAPSHOT
 */
public class Main extends JavaPlugin {

    private EntityCount entityCount;
    private PlayerCount playerCount;
    private PluginCount pluginCount;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        entityCount = new EntityCount(getDataFolder(), getServer());
        playerCount = new PlayerCount(getDataFolder(), getServer());
        pluginCount = new PluginCount(getDataFolder(), getServer());

        pluginCount.Export();

        int entityCountFrequency = getConfig().getInt("entity_count_sync_frequency");
        int playerCountFrequency = getConfig().getInt("player_info_sync_frequency");

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> entityCount.Export(), 60L, entityCountFrequency);
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> playerCount.Export(), 60L + entityCountFrequency/2, playerCountFrequency);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("entitycount")) {
            if(args.length < 1) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    int count = entityCount.getEntityCount(player.getWorld().getName());
                    sender.sendMessage(ChatColor.GOLD + "" + count + " entities found in " + player.getWorld().getName());
                }
            }
            else if (args.length > 1 && args.length < 3) {
                int count = entityCount.getEntityCount(args[1], args[0]);
                sender.sendMessage(ChatColor.GOLD + "" + count + " " + args[0] + " found in " + args[1]);
            }
            else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    int count = entityCount.getEntityCount(player.getWorld().getName(), args[0]);
                    sender.sendMessage(ChatColor.GOLD + "" + count + " " + args[0] + " found in " + player.getWorld().getName());
                }
            }

            return true;
        }

        switch (cmd.getName().toLowerCase()) {
            case "exportentitycount":
                if (args.length < 1)
                    entityCount.Export();
                else
                    sender.sendMessage(ChatColor.RED + "Too many arguments!");
                break;
            case "exportplugincount":
                if (args.length < 1)
                    pluginCount.Export();
                else
                    sender.sendMessage(ChatColor.RED + "Too many arguments!");
                break;
            case "exportplayercount":
                if (args.length < 1)
                    playerCount.Export();
                else
                    sender.sendMessage(ChatColor.RED + "Too many arguments!");
                break;
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(command.getName().equalsIgnoreCase("entitycount")) {
            if(args.length == 1) {
                ArrayList<String> entityTypes = new ArrayList<>();

                if(!args[0].equalsIgnoreCase("")) {
                    for (EntityType type : EntityType.values()) {
                        if (type != EntityType.UNKNOWN) {
                            String typeName = type.getKey().toString().toLowerCase();

                            if(typeName.contains(args[0].toLowerCase())) {
                                entityTypes.add(type.getKey().toString());
                            }
                        }
                    }
                } else {
                    for (EntityType type : EntityType.values()) {
                        if (type != EntityType.UNKNOWN) {
                            entityTypes.add(type.getKey().toString());
                        }
                    }
                }

                return entityTypes;
            }
            else if (args.length == 2) {
                ArrayList<String> worldList = new ArrayList<>();

                if(!args[1].equalsIgnoreCase("")) {
                    for (World world : getServer().getWorlds()) {
                        String worldName = world.getName();

                        if(worldName.contains(args[1].toLowerCase())) {
                            worldList.add(world.getName());
                        }
                    }
                } else {
                    for (World world : getServer().getWorlds()) {
                        worldList.add(world.getName());
                    }
                }

                return worldList;
            }
        }

        return super.onTabComplete(sender, command, alias, args);
    }
}
