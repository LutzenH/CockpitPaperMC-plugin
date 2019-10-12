package io.github.lutzenh.cppmc;

import org.bukkit.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractCount {

    private File dataPath;
    private String name;

    protected Server Server;

    public AbstractCount(File dataPath, String name, Server server) {
        this.dataPath = dataPath;
        this.name = name;
        this.Server = server;
    }

    protected abstract String asJson();

    public void Export() {
        try {
            FileWriter writer = new FileWriter(dataPath + File.separator + name + ".json", false);

            String json = asJson();
            writer.write(json);

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
