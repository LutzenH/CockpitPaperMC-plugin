package io.github.lutzenh.cppmc;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityCount extends AbstractCount {

    private List<World> worlds;
    private Map<String, Map<String, Integer>> entities;

    public EntityCount(File dataPath, Server server) {
        super(dataPath, "entities", server);

        this.worlds = server.getWorlds();

        entities = new HashMap<>();
    }

    private void updateEntityCount() {
        for (World world : worlds) {
            Map<String, Integer> world_entities = new HashMap<>();

            for (Entity entity : world.getEntities()) {

                String name = entity.getType().getKey().toString();
                int count = world_entities.getOrDefault(name, 0);

                world_entities.put(name, count + 1);
            }

            entities.put(world.getName(), world_entities);
        }
    }

    public int getEntityCount(String worldName, String entityName) {
        updateEntityCount();

        if(entities.containsKey(worldName))
            return entities.get(worldName).getOrDefault(entityName, 0);

        return 0;
    }

    public int getEntityCount(String worldName) {
        updateEntityCount();

        if(entities.containsKey(worldName)) {
            int count = 0;

            for (int entityCount : entities.get(worldName).values()) {
                count += entityCount;
            }

            return count;
        }

        return 0;
    }

    public Map<String, Map<String, Integer>> getEntityCountMap() {
        updateEntityCount();

        return entities;
    }

    @Override
    public String asJson() {
        updateEntityCount();

        JSONObject json = new JSONObject(entities);

        return json.toJSONString();
    }
}
