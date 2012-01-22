package de.maniacraft.chestindex.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.event.block.BlockListener;

import org.bukkit.event.block.BlockPlaceEvent;

import de.maniacraft.chestindex.Chestindex;

public class ChestBlockListener implements Listener {

	private final Chestindex plugin;

	public ChestBlockListener(Chestindex instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!Chestindex.LWC) {
			Block block = event.getBlock();
			World world = block.getWorld();
			if (plugin.DB.isListed(world, block.getX(), block.getY(), block.getZ())) {
				plugin.DB.removeChest(world, block.getX(), block.getY(), block.getZ());
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
			World world = block.getWorld();
			if (block.getType() == Material.CHEST) {
				plugin.DB.addChest(player, world, block.getX(), block.getY(), block.getZ());
			}
		}
	}

}
