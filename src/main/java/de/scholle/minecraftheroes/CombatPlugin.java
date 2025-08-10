package de.scholle.minecraftheroes;

import de.scholle.minecraftheroes.leafdecay.BlockBreakEventListener;
import de.scholle.minecraftheroes.dummy.DummyListener;
import de.scholle.minecraftheroes.dummy.ListDummysCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class CombatPlugin extends JavaPlugin {

    private static CombatPlugin instance;
    public static CombatPlugin plugin; // Für FastLeafDecay-Teil

    private CombatManager combatManager;
    private CombatDisplayManager displayManager;
    private LivesStorage livesStorage;

    private final HashMap<UUID, Integer> lives = new HashMap<>();
    private final HashSet<UUID> awaitingRevive = new HashSet<>();

    private boolean villagerTradingEnabled;
    private boolean loseLifeOnLogoutDuringCombat;

    private String messagePrefix;

    @Override
    public void onEnable() {
        instance = this;
        plugin = this;

        saveDefaultConfig();
        loadPrefix();

        FileConfiguration config = getConfig();
        villagerTradingEnabled = config.getBoolean("villagerTradingEnabled", true);
        loseLifeOnLogoutDuringCombat = config.getBoolean("loseLifeOnLogoutDuringCombat", true);
        getLogger().info("[DEBUG] Villager Trading Enabled: " + villagerTradingEnabled);
        getLogger().info("[DEBUG] Lose life on logout during combat: " + loseLifeOnLogoutDuringCombat);

        this.livesStorage = new LivesStorage(this);
        this.combatManager = new CombatManager(this);
        this.displayManager = new CombatDisplayManager(this, this.combatManager);

        // Combat-bezogene Listener
        Bukkit.getPluginManager().registerEvents(this.combatManager, this);
        Bukkit.getPluginManager().registerEvents(new FireworkCrossbowBlocker(this), this);
        Bukkit.getPluginManager().registerEvents(new PunchEnchantBlocker(this), this);
        Bukkit.getPluginManager().registerEvents(new VillagerTradeBlocker(this, villagerTradingEnabled), this);
        Bukkit.getPluginManager().registerEvents(new CombatLogoutListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DummyListener(this), this);

        // FastLeafDecay-Listener
        getServer().getPluginManager().registerEvents(new BlockBreakEventListener(), this);

        // Commands
        getCommand("lives").setExecutor(new LivesCommand(this));
        getCommand("lives").setTabCompleter(new CombatTabCompleter());
        getCommand("heart").setTabCompleter(new CombatTabCompleter());
        getCommand("listdummys").setExecutor(new ListDummysCommand());

        getLogger().info("CombatPlugin enabled.");
    }

    @Override
    public void onDisable() {
        this.lives.forEach((uuid, value) -> this.livesStorage.setLives(uuid, value));
        getLogger().info("CombatPlugin disabled.");
    }

    public static CombatPlugin getInstance() {
        return instance;
    }

    public CombatManager getCombatManager() {
        return this.combatManager;
    }

    public int getLives(UUID uuid) {
        return this.lives.getOrDefault(uuid, 3);
    }

    public void setLives(UUID uuid, int count) {
        this.lives.put(uuid, count);
        this.livesStorage.setLives(uuid, count);
    }

    public void removePlayer(UUID uuid) {
        this.lives.remove(uuid);
        this.livesStorage.remove(uuid);
        awaitingRevive.remove(uuid);
    }

    public boolean hasSavedLives(UUID uuid) {
        return this.livesStorage.loadLives().containsKey(uuid);
    }

    public Long getCombatEnd(UUID uuid) {
        Long timestamp = combatManager.getCombatTimestamps().get(uuid);
        if (timestamp == null) return null;
        return timestamp + 30000;
    }

    public boolean isLoseLifeOnLogoutDuringCombatEnabled() {
        return loseLifeOnLogoutDuringCombat;
    }

    public double getKeepInventoryPercentageCombat() {
        return getConfig().getDouble("keepInventoryPercentageCombat", 0.5);
    }

    public double getKeepInventoryPercentageNatural() {
        return getConfig().getDouble("keepInventoryPercentageNatural", 0.0);
    }

    public void loadPrefix() {
        FileConfiguration config = getConfig();
        String rawPrefix = config.getString("message-prefix", "§6[Helden]");

        if (!rawPrefix.endsWith(" ")) {
            rawPrefix += " ";
        }

        this.messagePrefix = rawPrefix;
    }

    public void sendMessage(Player player, String message) {
        if (player != null) {
            player.sendMessage(messagePrefix + message);
        }
    }
}
