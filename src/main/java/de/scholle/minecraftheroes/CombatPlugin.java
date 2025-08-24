package de.scholle.minecraftheroes;

import de.scholle.minecraftheroes.commands.DuelCommand;
import de.scholle.minecraftheroes.dummy.DummyListener;
import de.scholle.minecraftheroes.dummy.ListDummysCommand;
import de.scholle.minecraftheroes.leafdecay.BlockBreakEventListener;
import de.scholle.minecraftheroes.links.DiscordCommand;
import de.scholle.minecraftheroes.links.TexturepackCommand;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class CombatPlugin extends JavaPlugin {

    private static CombatPlugin instance;
    public static CombatPlugin plugin;

    private CombatManager combatManager;
    private CobwebManager cobwebManager;
    private EnderPearlManager enderPearlManager;
    private CombatDisplayManager displayManager;
    private LivesStorage livesStorage;
    private DuelCommand duelCommand;

    private final HashMap<UUID, Integer> lives = new HashMap<>();
    private final HashSet<UUID> awaitingRevive = new HashSet<>();

    private boolean villagerTradingEnabled;
    private boolean loseLifeOnLogoutDuringCombat;
    private boolean noNether;
    private boolean noTotems;
    private boolean fireworkPlacementAllowed;
    private boolean noNetherite;

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
        noNether = config.getBoolean("noNether", true);
        noTotems = config.getBoolean("NoTotems", true);
        fireworkPlacementAllowed = config.getBoolean("fireworkPlacementAllowed", false);
        noNetherite = config.getBoolean("noNetherite", true);

        this.livesStorage = new LivesStorage(this);
        this.lives.putAll(livesStorage.loadLives());

        this.combatManager = new CombatManager(this);
        this.cobwebManager = new CobwebManager(this);
        this.enderPearlManager = new EnderPearlManager(this);
        this.displayManager = new CombatDisplayManager(this, this.combatManager);

        this.duelCommand = new DuelCommand(this, this.combatManager);

        Bukkit.getPluginManager().registerEvents(this.combatManager, this);
        Bukkit.getPluginManager().registerEvents(new CobwebListener(this), this); // CobwebListener
        Bukkit.getPluginManager().registerEvents(new FireworkCrossbowBlocker(this), this);
        Bukkit.getPluginManager().registerEvents(new PunchEnchantBlocker(this), this);
        Bukkit.getPluginManager().registerEvents(new VillagerTradeBlocker(this, villagerTradingEnabled), this);
        Bukkit.getPluginManager().registerEvents(new CombatLogoutListener(this), this);
        Bukkit.getPluginManager().registerEvents(new DummyListener(this), this);
        Bukkit.getPluginManager().registerEvents(new NoOpgapEatListener(this), this);
        Bukkit.getPluginManager().registerEvents(new NetherEnterBlocker(this), this);
        Bukkit.getPluginManager().registerEvents(new BlockBreakEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new FireworkUseBlocker(this), this);
        Bukkit.getPluginManager().registerEvents(new NoTotemListener(this), this);
        Bukkit.getPluginManager().registerEvents(new NoNetheriteListener(this), this);
        Bukkit.getPluginManager().registerEvents(new EnderPearlListener(this), this);

        getCommand("lives").setExecutor(new LivesCommand(this));
        getCommand("lives").setTabCompleter(new CombatTabCompleter());
        getCommand("heart").setTabCompleter(new CombatTabCompleter());
        getCommand("listdummys").setExecutor(new ListDummysCommand());
        getCommand("discord").setExecutor(new DiscordCommand(this));
        getCommand("dc").setExecutor(new DiscordCommand(this));
        getCommand("texturepack").setExecutor(new TexturepackCommand(this));
        getCommand("resourcepack").setExecutor(new TexturepackCommand(this));
        getCommand("duel").setExecutor(this.duelCommand);

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

    public CobwebManager getCobwebManager() {
        return this.cobwebManager;
    }

    public EnderPearlManager getEnderPearlManager() {
        return this.enderPearlManager;
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

    public boolean isNoNether() {
        return noNether;
    }

    public boolean isNoTotems() {
        return noTotems;
    }

    public boolean isFireworkPlacementAllowed() {
        return fireworkPlacementAllowed;
    }

    public boolean isNoNetherite() {
        return noNetherite;
    }

    public DuelCommand getDuelCommand() {
        return duelCommand;
    }
}
