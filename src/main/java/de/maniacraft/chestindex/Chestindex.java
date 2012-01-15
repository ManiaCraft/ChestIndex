package de.maniacraft.chestindex;

import java.util.Hashtable;
//import java.util.LinkedList;
import java.util.List;
//import java.util.Stack;
import java.util.Vector;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import de.maniacraft.chestindex.MySQL;
import de.maniacraft.chestindex.listeners.ChestBlockListener;
import de.maniacraft.chestindex.listeners.ChestPlayerListener;

import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
//import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Chestindex extends JavaPlugin {

	static final Logger log = Logger.getLogger("Minecraft");
	private final ChestPlayerListener playerListener = new ChestPlayerListener(this);
	private final ChestBlockListener blockListener = new ChestBlockListener(this);
	public static final String prefix = "[ChestIndex]";
	public static final String version = "1.0";
	public static boolean LWC;
	public MySQL DB = new MySQL();
	private Hashtable<String, Vector<IndexChest>> data = new Hashtable<String, Vector<IndexChest>>();

	public void onDisable() {
		System.out.println(prefix + " Version " + version + " disabled!");
	}

	public void onEnable() {
		Config.load();
		DB.Connect();
		PluginManager pm = getServer().getPluginManager();
		LWC = Config.getBoolean("LWC");
		// Register Events
		pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		System.out.println(prefix + " Version " + version + " enabled!");
	}

	public void sendConsole(String text) {
		System.out.println(text);
	}

	public void sendPlayer(String text, Player player) {
		player.sendMessage(text);
	}

	public boolean isNumeric(String s) {
		return s.matches("\\d+");
	}

	public ItemStack[] getChestContent(Block block) {
		/*
		 * BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}; for (BlockFace blockFace : faces) { Block face = block.getRelative(blockFace); if (face.getType() == Material.CHEST) { // Double Chest Chest chest2 = (Chest)face.getState(); inventory =
		 * chest2.getInventory().getContents(); } }
		 */
		Chest chest = (Chest) block.getState();
		ItemStack[] inventory = chest.getInventory().getContents();
		return inventory;
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		Player player = (Player) sender;

		if (commandName.compareToIgnoreCase("chestindex") == 0) {
			if (args.length == 0) {
				sender.sendMessage(prefix + ChatColor.WHITE + " /ci search <Block> - Search for <Block> in your Chests.");
				sender.sendMessage(prefix + ChatColor.WHITE + " /ci teleport <ID> - Telport to Chest <ID>");
			} else if (args.length > 1) {
				if (args[0].equalsIgnoreCase("search") || args[0].equalsIgnoreCase("s") && Material.getMaterial(args[1]) != null) {
					Vector<IndexChest> chestVec = new Vector<IndexChest>();
					data.put(sender.getName(), chestVec);

					List<Chest> chests = DB.getChests(sender.getName());
					for (Chest chest : chests) {
						try {
							if (!chest.getInventory().contains(Material.valueOf(args[1])))
								continue;
							ItemStack[] inventory = chest.getInventory().getContents();
							IndexChest found = new IndexChest(chest.getWorld().getName(), chest.getX(), chest.getY(), chest.getZ(), 0, args[1]);
							chestVec.add(found);
							for (ItemStack x : inventory) {
								if (x == null)
									continue;
								if (x.getType().equals(Material.valueOf(args[1]))) {
									// sendConsole(x.getType() + " - " + x.getAmount());
									// IndexChest[] found = new IndexChest[];
									// arrays.put($user, new IndexChest(chest.getX(),chest.getY(), chest.getZ(), x.getAmount()));
									// sender.sendMessage(0 + ": " + args[1] + " " + found.amount + " stk.");
									found.amount += x.getAmount();
								}
							}
						} catch (Exception e) {
							System.out.println(prefix + " Error on handling Chestindex Command search.");
						}
					}
					// Output List
					for (int y = 0; y < chestVec.size(); y++) {
						IndexChest chest = chestVec.get(y);
						sender.sendMessage(y + ": " + chest.item + " " + chest.amount + " stk.");
					}
				} else if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp") && isNumeric(args[1])) {
					try {
						Vector<IndexChest> chestVec = data.get(sender.getName());
						IndexChest chest = chestVec.get(Integer.parseInt(args[1]));
						sender.sendMessage("TELEPORT to Chest " + args[1] + ": " + chest.item + " " + chest.amount + " stk.");
						World world = Bukkit.getWorld(chest.world);
						Location to = new Location(world, chest.x, chest.y, chest.z);
						player.teleport(to);
					} catch (Exception e) {
						System.out.println("Error on handling Chestindex Command tp.");
					}
				}
			}

			return true;
		}
		return false;
	}
}
