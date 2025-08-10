package de.scholle.minecraftheroes.leafdecay;

import org.bukkit.block.Block;

public class TaxicabDistanceService {

    public static int distance(Block b1, Block b2) {
        return Math.abs(b1.getX() - b2.getX()) // ✅ X mit X vergleichen
                + Math.abs(b1.getY() - b2.getY()) // ✅ Y mit Y vergleichen
                + Math.abs(b1.getZ() - b2.getZ()); // ✅ Z mit Z vergleichen
    }
}
