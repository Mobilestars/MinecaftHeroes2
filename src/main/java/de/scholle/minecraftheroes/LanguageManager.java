package de.scholle.minecraftheroes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager {

    private final JavaPlugin plugin;
    private Map<String, String> messages = new HashMap<>();

    public LanguageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadLanguage("de_de"); // Standard: Deutsch
    }

    public void loadLanguage(String langCode) {
        File langFolder = new File(plugin.getDataFolder(), "lang");
        if (!langFolder.exists()) langFolder.mkdirs();

        File langFile = new File(langFolder, langCode + ".json");

        if (!langFile.exists()) {
            try (InputStream in = plugin.getResource("lang/" + langCode + ".json")) {
                if (in != null) {
                    try (OutputStream out = new FileOutputStream(langFile)) {
                        in.transferTo(out);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (Reader reader = new InputStreamReader(new FileInputStream(langFile), StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            this.messages = new Gson().fromJson(reader, type);
        } catch (Exception e) {
            plugin.getLogger().warning("Konnte Sprachdatei nicht laden: " + e.getMessage());
        }
    }

    public String getMessage(String key) {
        return get(key, null);
    }

    public String getMessage(String key, Map<String, String> placeholders) {
        return get(key, placeholders);
    }

    public String get(String key, Map<String, String> placeholders) {
        String msg = messages.getOrDefault(key, key);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return msg;
    }

    public String get(String key) {
        return get(key, null);
    }
}
