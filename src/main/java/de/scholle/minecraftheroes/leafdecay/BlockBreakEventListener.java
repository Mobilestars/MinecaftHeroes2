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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlockBreakEventListener implements Listener {

    private final ArrayList<BlockFace> neighbours = new ArrayList<>(List.of(BlockFace.values()));
    private final Set<Block> visited = new HashSet<>();

    public BlockBreakEventListener() {
        neighbours.remove(BlockFace.SELF);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(@NotNull BlockBreakEvent event) {
        Block block = event.getBlock();

        visited.clear(); // Neues Event, also Reset

        if (Tag.LEAVES.isTagged(block.getType())) {
            breakLeaf(block, isValidLeaf(block), block);
        }

        if (Tag.LOGS.isTagged(block.getType())) {
            breakLeaf(block, false, block);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeavesDecay(LeavesDecayEvent event) {
        visited.clear(); // Neues Event, also Reset
        Block block = event.getBlock();

        if (isValidLeaf(block, block)) {
            event.setCancelled(true); // Normales Verrotten verhindern
            breakLeaf(block, true, block);
        }
    }

    private void breakLeaf(Block block, boolean breakFirstBlock, Block originalBlock) {
        if (!visited.add(block)) return; // Schon verarbeitet → Schleifen verhindern

        if (breakFirstBlock) {
            block.breakNaturally();
        }

        for (BlockFace neighbour : neighbours) {
            Block neighbourBlock = block.getRelative(neighbour);
            if (!isValidLeaf(neighbourBlock, originalBlock)) continue;
            breakLeaf(neighbourBlock, true, originalBlock);
        }
    }

    private boolean isValidLeaf(Block block, Block originalBlock) {
        if (!(block.getBlockData() instanceof Leaves leafBlock)) return false;
        if (leafBlock.isPersistent()) return false; // Spielerplatzierte Blätter ignorieren
        return TaxicabDistanceService.distance(block, originalBlock) <= 35;
    }

    private boolean isValidLeaf(Block block) {
        return isValidLeaf(block, block);
    }
}
