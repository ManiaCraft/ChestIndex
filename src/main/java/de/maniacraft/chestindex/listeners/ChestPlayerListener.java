package de.maniacraft.chestindex.listeners;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
//import org.bukkit.event.player.PlayerListener;

import de.maniacraft.chestindex.Chestindex;

public class ChestPlayerListener implements Listener {

	private final Chestindex plugin;

	public ChestPlayerListener(Chestindex instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Action action = event.getAction();
		Player player = event.getPlayer();
		if (!Chestindex.LWC) {
			if (action == Action.RIGHT_CLICK_BLOCK) {
				Block block = event.getClickedBlock();
				World world = block.getWorld();
				if (player.getItemInHand().getType() == Material.STICK && block.getType() == Material.CHEST) {
					plugin.DB.addChest(player, world, block.getX(), block.getY(), block.getZ());
				}
			} else if (action == Action.LEFT_CLICK_BLOCK) {
				Block block = event.getClickedBlock();
				World world = block.getWorld();
				if (player.getItemInHand().getType() == Material.STICK && block.getType() == Material.CHEST && plugin.DB.isListed(world, block.getX(), block.getY(), block.getZ(), player.getName())) {
					plugin.DB.removeChest(world, block.getX(), block.getY(), block.getZ());
				}
			}
		} else {
			// TODO Alternative Chests mit LWC..
		}
	}
}