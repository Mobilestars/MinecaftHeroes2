package de.scholle.minecraftheroes;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class LivesStorage {

    private final CombatPlugin plugin;
    private File file;
    private FileConfiguration config;

    public LivesStorage(CombatPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "lives.yml");
        if (!file.exists()) {
            plugin.saveResource("lives.yml", false);
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void setLives(UUID uuid, int lives) {
        config.set(uuid.toString(), lives);
        save();
    }

    public int getLives(UUID uuid) {
        return config.getInt(uuid.toString(), 3);
    }

    public void remove(UUID uuid) {
        config.set(uuid.toString(), null);
        save();
    }

    public HashMap<UUID, Integer> loadLives() {
        HashMap<UUID, Integer> map = new HashMap<>();
        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            int lives = config.getInt(key);
            map.put(uuid, lives);
        }
        return map;
    }

    private void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
