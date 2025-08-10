package de.scholle.minecraftheroes.leafdecay;

import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import java.util.*;

public class BlockBreakEventListener implements Listener {

    private final List<BlockFace> neighbours = new ArrayList<>(List.of(BlockFace.values()));
    private final Set<Block> visited = new HashSet<>();

    public BlockBreakEventListener() {
        neighbours.remove(BlockFace.SELF);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        visited.clear();

        // Wenn ein Log zerstört wird → Prüfen, ob es das letzte in der Nähe war
        if (Tag.LOGS.isTagged(block.getType())) {
            for (BlockFace face : neighbours) {
                Block neighbour = block.getRelative(face);
                if (Tag.LEAVES.isTagged(neighbour.getType()) && !isLeafConnectedToLog(neighbour)) {
                    breakLeaf(neighbour, true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeavesDecay(LeavesDecayEvent event) {
        visited.clear();
        Block leaf = event.getBlock();

        if (!isLeafConnectedToLog(leaf)) {
            event.setCancelled(true); // Normales Verrotten verhindern
            breakLeaf(leaf, true);
        }
    }

    private void breakLeaf(Block block, boolean breakFirstBlock) {
        if (!visited.add(block)) return;

        if (breakFirstBlock) block.breakNaturally();

        for (BlockFace face : neighbours) {
            Block neighbour = block.getRelative(face);
            if (isValidLeaf(neighbour) && !isLeafConnectedToLog(neighbour)) {
                breakLeaf(neighbour, true);
            }
        }
    }

    private boolean isValidLeaf(Block block) {
        if (!(block.getBlockData() instanceof Leaves leafBlock)) return false;
        return !leafBlock.isPersistent(); // Nur natürliche Blätter
    }

    /**
     * Prüft, ob ein Blatt noch in Reichweite eines Logs ist
     */
    private boolean isLeafConnectedToLog(Block leaf) {
        Set<Block> checked = new HashSet<>();
        Queue<Block> toCheck = new LinkedList<>();
        toCheck.add(leaf);

        while (!toCheck.isEmpty()) {
            Block current = toCheck.poll();
            if (!checked.add(current)) continue;

            if (Tag.LOGS.isTagged(current.getType())) {
                return true; // Log gefunden → noch verbunden
            }

            if (current.getBlockData() instanceof Leaves) {
                for (BlockFace face : neighbours) {
                    toCheck.add(current.getRelative(face));
                }
            }
        }
        return false; // Kein Log mehr in Reichweite
    }
}
