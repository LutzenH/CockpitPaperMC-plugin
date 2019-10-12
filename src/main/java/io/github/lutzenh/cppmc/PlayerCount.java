package io.github.lutzenh.cppmc;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerCount extends AbstractCount {

    private Map<String, Map<String, Object>> players;

    public PlayerCount(File dataPath, Server server) {
        super(dataPath, "players", server);

        players = new HashMap<>();
    }

    private void updatePlayerCount() {
        Collection<? extends Player> onlinePlayers = Server.getOnlinePlayers();
        players.clear();

        for (Player player : onlinePlayers) {
            if (player.getPlayerProfile().getId() != null) {
                Map<String, Object> playerInfo = new HashMap<>();

                playerInfo.put("display_name", player.getDisplayName());

                if(player.getAddress() != null)
                    playerInfo.put("ip", player.getAddress().toString());
                else
                    playerInfo.put("ip", null);

                playerInfo.put("gamemode", player.getGameMode().name());
                playerInfo.put("world", player.getWorld().getName());
                playerInfo.put("experience", player.getExp());
                playerInfo.put("food_level", player.getFoodLevel());

                players.put(player.getPlayerProfile().getId().toString(), playerInfo);
            }
        }
    }

    @Override
    protected String asJson() {
        updatePlayerCount();

        JSONObject json = new JSONObject(players);

        return json.toJSONString();
    }
}
