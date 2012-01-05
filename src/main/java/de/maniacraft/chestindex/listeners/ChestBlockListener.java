package de.maniacraft.chestindex.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

import org.bukkit.event.block.BlockPlaceEvent;



import de.maniacraft.chestindex.Chestindex;


public class ChestBlockListener extends BlockListener {

	private final Chestindex plugin;
    public ChestBlockListener(Chestindex instance) {
        plugin = instance;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
    }
    
    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();

        if (block.getType() == Material.CHEST) {
            BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};

            for (BlockFace blockFace : faces) {
                Block face = block.getRelative(blockFace);
                if (face.getType() == Material.CHEST) {
                	// Double Chest
                }
            }
            plugin.DB.addChest(player, block.getX(), block.getY(), block.getZ());
        }
    }

}
