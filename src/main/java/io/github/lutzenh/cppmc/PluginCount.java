package io.github.lutzenh.cppmc;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PluginCount extends AbstractCount {

    private PluginManager pluginManager;

    private Map<String, HashMap<String, Object>> plugins;

    public PluginCount(File dataPath, Server server) {
        super(dataPath, "plugins", server);

        this.pluginManager = server.getPluginManager();

        plugins = new HashMap<>();
    }

    public void updatePluginList() {
        plugins.clear();

        for (Plugin plugin: pluginManager.getPlugins()) {
            HashMap<String, Object> pluginInfo = new HashMap<>();

            pluginInfo.put("enabled", plugin.isEnabled());
            pluginInfo.put("full_name", plugin.getDescription().getFullName());
            pluginInfo.put("description", plugin.getDescription().getDescription());
            pluginInfo.put("version", plugin.getDescription().getVersion());
            pluginInfo.put("authors", plugin.getDescription().getAuthors());
            pluginInfo.put("website", plugin.getDescription().getWebsite());
            pluginInfo.put("dependencies", plugin.getDescription().getDepend());

            plugins.put(plugin.getName(), pluginInfo);
        }
    }

    @Override
    public String asJson() {
        updatePluginList();

        JSONObject json = new JSONObject(plugins);

        return json.toJSONString();
    }
}
