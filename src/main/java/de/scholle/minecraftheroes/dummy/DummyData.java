package de.scholle.minecraftheroes.dummy;

import org.bukkit.entity.ArmorStand;

import java.util.UUID;

public class DummyData {
    private final UUID owner;
    private final ArmorStand dummy;
    private final double initialHealth;

    public DummyData(UUID owner, ArmorStand dummy, double initialHealth) {
        this.owner = owner;
        this.dummy = dummy;
        this.initialHealth = initialHealth;
    }

    public UUID getOwner() {
        return owner;
    }

    public ArmorStand getDummy() {
        return dummy;
    }

    public double getInitialHealth() {
        return initialHealth;
    }
}
