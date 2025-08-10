package de.scholle.minecraftheroes.leafdecay;

import org.bukkit.plugin.java.JavaPlugin;

public final class FastLeafDecay extends JavaPlugin {

    public static FastLeafDecay plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getPluginManager().registerEvents(new BlockBreakEventListener(), this);
        getLogger().info("FastLeafDecay enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("FastLeafDecay disabled.");
    }
}
