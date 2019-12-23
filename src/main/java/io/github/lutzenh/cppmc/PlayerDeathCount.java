package io.github.lutzenh.cppmc;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerDeathCount extends AbstractCount implements Listener {

    private Main main;

    private HashMap<String, Long> PlayerDeathCount;
    private boolean enablePrefix;

    public PlayerDeathCount(File dataPath, Main main, boolean enablePrefix) {
        super(dataPath, "player_deaths", main.getServer());

        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);

        PlayerDeathCount = Import(dataPath + File.separator + "player_deaths.json");
        this.enablePrefix = enablePrefix;
    }

    @Override
    protected String asJson() {
        JSONObject json = new JSONObject(PlayerDeathCount);
        return json.toJSONString();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(PlayerLoginEvent event) {
        Player pl = event.getPlayer();

        if(PlayerDeathCount.containsKey(Objects.requireNonNull(pl.getPlayerProfile().getId()).toString()))
            setPlayerPrefix(pl, PlayerDeathCount.get(Objects.requireNonNull(pl.getPlayerProfile().getId()).toString()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e){
        Entity en1 = e.getEntity();

        if (en1 instanceof Player){
            Player pl = (Player)en1;

            String playerUUID = Objects.requireNonNull(pl.getPlayerProfile().getId()).toString();
            long currentValue = PlayerDeathCount.getOrDefault(playerUUID, (long) 0);

            currentValue++;

            PlayerDeathCount.put(playerUUID, currentValue);
            setPlayerPrefix(pl, currentValue);

            Export();
        }
    }

    private HashMap<String, Long> Import(String path){
        HashMap<String, Long> tempDictionary = new HashMap<>();

        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(new InputStreamReader(new FileInputStream(path)));
            tempDictionary = toMap(json);
            main.getLogger().info("Found death-count information for " + tempDictionary.size() + " players(s).");
        }
        catch (FileNotFoundException e) {
            main.getLogger().info("No death-count information found.");
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return tempDictionary;
    }

    private static HashMap<String, Long> toMap(JSONObject object) {
        HashMap<String, Long> map = new HashMap<>();

        for (Object o : object.keySet()) {
            String key = (String) o;
            Long value = (Long) object.get(key);

            map.put(key, value);
        }

        return map;
    }

    private void setPlayerPrefix(Player player, long deathCount) {
        if(enablePrefix){
            String deathCountString = "";

            if(deathCount > 0 && deathCount < 4) {
                deathCountString += "[";

                for (int i = 0; i < deathCount; i++)
                    deathCountString += "☠";

                deathCountString += "] ";
            } else if (deathCount >= 4) {
                deathCountString = "[" + deathCount + "x☠] ";
            }

            player.setDisplayName(deathCountString + "" + player.getName() + "");
            player.setPlayerListName(deathCountString + "" + player.getName() + "");
        }
    }

    public long getPlayerDeathCount(UUID playerUUID) {
        String UUID = playerUUID.toString();

        if(PlayerDeathCount.containsKey(UUID))
            return PlayerDeathCount.get(UUID);
        else
            return 0;
    }

    public void setPlayerDeathCount(Player player, long amount) {
        PlayerDeathCount.put(Objects.requireNonNull(player.getPlayerProfile().getId()).toString(), amount);
        Export();
        setPlayerPrefix(player, amount);
    }
}
