package de.maniacraft.chestindex.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import de.maniacraft.chestindex.Chestindex;

public class ChestBlockListener implements Listener {

	private final Chestindex plugin;

	public ChestBlockListener(Chestindex instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!Chestindex.LWC) {
			Block block = event.getBlock();
			Block block2 = null;
			World world = block.getWorld();
			if (block.getType() == Material.CHEST) {
				BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

				// Double Chest
				for (BlockFace blockFace : faces) {
					Block face = block.getRelative(blockFace);
					if (face.getType() == Material.CHEST) {
						block2 = face;
						break;
					}
				}
				// Remove or edit Chestentry
				if (plugin.DB.isListed(world, block.getX(), block.getY(), block.getZ())) {
					// Doppelchest, Chest die in der Datenbank ist wird geloescht -> Trage zweite Chest stattdessen ein.
					if (block2 != null && !plugin.DB.isListed(world, block2.getX(), block2.getY(), block2.getZ())) {
						plugin.DB.editChest(world, block.getX(), block.getY(), block.getZ(), block2.getX(), block2.getY(), block2.getZ());
					}
					// Eizelne Kiste die in der Datenbank ist wird geloescht -> Loesche aus Datenbank.
					else {
						plugin.DB.removeChest(world, block.getX(), block.getY(), block.getZ());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!Chestindex.LWC) {

			Player player = event.getPlayer();
			Block block = event.getBlockPlaced();
			Block block2 = null;
			World world = block.getWorld();
			if (block.getType() == Material.CHEST) {
				BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

				// Double Chest
				for (BlockFace blockFace : faces) {
					Block face = block.getRelative(blockFace);
					if (face.getType() == Material.CHEST) {
						block2 = face;
						break;
					}
				}
				// Entweder Doppelchest, aber Chest2 ist nicht gelistet, oder Einzelchest -> trage ein
				if (block2 == null || !plugin.DB.isListed(world, block2.getX(), block2.getY(), block2.getZ())) {
					plugin.DB.addChest(player, world, block.getX(), block.getY(), block.getZ());
				}
			}
		}
	}

}
