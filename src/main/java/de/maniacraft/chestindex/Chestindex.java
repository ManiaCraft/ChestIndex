package de.maniacraft.chestindex;

import java.util.Hashtable;
import java.util.List;
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
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import com.griefcraft.lwc.*;

public class Chestindex extends JavaPlugin {

	static final Logger log = Logger.getLogger("Minecraft");
	private final ChestPlayerListener playerListener = new ChestPlayerListener(this);
	private final ChestBlockListener blockListener = new ChestBlockListener(this);
	public static final String prefix = "[ChestIndex]";
	public static final String version = "1.0";
	public static boolean LWC;
	public static LWC lwc = null;
	public MySQL DB = new MySQL();
	private Hashtable<String, Vector<IndexChest>> data = new Hashtable<String, Vector<IndexChest>>();

	public void onDisable() {
		sendConsole(prefix + " Version " + version + " disabled!");
	}

	public void onEnable() {
		Config.load();
		PluginManager pm = getServer().getPluginManager();
		Plugin lwcPlugin = getServer().getPluginManager().getPlugin("LWC");
		if (lwcPlugin != null && Config.getBoolean("LWC")) {
			LWC = true;
			lwc = ((LWCPlugin) lwcPlugin).getLWC();
			sendConsole(prefix + " Using LWC Plugin!");
		} else {
			LWC = false;
			if (DB.Connect())
				sendConsole(prefix + " Database connection succesfully etablished!");
			else
				sendConsole(prefix + " Database connection failed!");
		}
		// Register Events
		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener, this);
		sendConsole(prefix + " Version " + version + " enabled!");
	}

	public static void sendConsole(String text) {
		System.out.println(text);
	}

	public void sendPlayer(String text, Player player) {
		player.sendMessage(text);
	}

	public boolean isNumeric(String s) {
		return s.matches("\\d+");
	}

	public ItemStack[] getChestContent(Block block) {
		Chest chest = (Chest) block.getState();
		ItemStack[] inventory = chest.getInventory().getContents();
		return inventory;
	}

	public void sendList(int offset, Vector<IndexChest> chestVec, Player player) {
		try {
			int sum;
			if (chestVec.size() >= (offset + 10))
				sum = 10;
			else
				sum = (chestVec.size() - offset);

			for (int y = offset; y < (sum + offset); y++) {
				IndexChest chest = chestVec.get(y);
				sendPlayer(y + ": " + chest.item + " " + chest.amount + " stk.", player);
			}
			double site = (Math.floor(10 + offset) / 10);
			int sites = (chestVec.size() / 10 + 1);
			sendPlayer("Es wurden " + chestVec.size() + " Ergebnisse gefunden. Zeige Seite " + (int) site + " von " + sites, player);
		} catch (Exception e) {
			sendPlayer("Fehler. Es wurden keine Ergebnisse gefunden.", player);
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		Player player = (Player) sender;
		if (commandName.compareToIgnoreCase("chestindex") == 0) {
			if (args.length == 0) {
				sendPlayer(prefix + ChatColor.WHITE + " /ci search <Block> - Search for <Block> in your Chests.", player);
				sendPlayer(prefix + ChatColor.WHITE + " /ci teleport <ID> - Telport to Chest <ID>", player);
			} else if (args.length > 1) {
				if (args[0].equalsIgnoreCase("search") || args[0].equalsIgnoreCase("s")) {
					String searchfor;
					if (isNumeric(args[1])) {
						searchfor = args[1];
					} else {
						StringBuffer result = new StringBuffer();
						result.append(args[1]);
						if (args.length > 2) {
							for (int i = 2; i < args.length; i++) {
								result.append("_");
								result.append(args[i]);
							}
						}
						searchfor = result.toString().toUpperCase().replace(" ", "_");
					}
					if (Material.getMaterial(searchfor) != null) {
						Vector<IndexChest> chestVec = new Vector<IndexChest>();
						data.put(sender.getName(), chestVec);

						List<Chest> chests = DB.getChests(sender.getName());
						for (Chest chest : chests) {
							try {
								IndexChest found = null;
								boolean doublechest = false;
								World world_chest1 = chest.getWorld();
								Block block_chest1 = world_chest1.getBlockAt(chest.getX(), chest.getY(), chest.getZ());
								BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

								// Double Chest (LWC)
								for (BlockFace blockFace : faces) {
									Block face = block_chest1.getRelative(blockFace);
									if (face.getType() == Material.CHEST) {
										Chest chest2 = (Chest) face.getState();
										if (chest2.getInventory().contains(Material.valueOf(searchfor))) {
											ItemStack[] inventory = chest2.getInventory().getContents();
											found = new IndexChest(chest2.getWorld().getName(), chest2.getX(), chest2.getY(), chest2.getZ(), 0, args[1]);
											chestVec.add(found);
											for (ItemStack x : inventory) {
												if (x == null)
													continue;
												if (x.getType().equals(Material.valueOf(searchfor))) {
													found.amount += x.getAmount();
												}
											}
											doublechest = true;
										}
										break;
									}
								}
								if (!chest.getInventory().contains(Material.valueOf(searchfor)))
									continue;
								ItemStack[] inventory = chest.getInventory().getContents();
								if (!doublechest) {
									found = new IndexChest(chest.getWorld().getName(), chest.getX(), chest.getY(), chest.getZ(), 0, args[1]);
									chestVec.add(found);
								}
								for (ItemStack x : inventory) {
									if (x == null)
										continue;
									if (x.getType().equals(Material.valueOf(searchfor))) {
										found.amount += x.getAmount();
									}
								}
							} catch (Exception e) {
								sendConsole(prefix + " Error on handling Chestindex Command search.");
							}
						}
						/*
						 * for (int y = 0; y < chestVec.size(); y++) { IndexChest chest = chestVec.get(y); sendPlayer(y + ": " + chest.item + " " + chest.amount + " stk.", player); }
						 */

						// Send first page.
						sendList(0, chestVec, player);
					}
				} else if (args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("l") && isNumeric(args[1])) {
					try {
						Vector<IndexChest> chestVec = data.get(sender.getName());
						int page = ((Integer.parseInt(args[1]) * 10) - 10);
						sendList(page, chestVec, player);
					} catch (Exception e) {
						sendConsole("Error on handling Chestindex Command list.");
					}
				} else if (args[0].equalsIgnoreCase("teleport") || args[0].equalsIgnoreCase("tp") && isNumeric(args[1])) {
					try {
						Vector<IndexChest> chestVec = data.get(sender.getName());
						IndexChest chest = chestVec.get(Integer.parseInt(args[1]));
						sendPlayer("TELEPORT to Chest " + args[1] + ": " + chest.item + " " + chest.amount + " stk.", player);
						World world = Bukkit.getWorld(chest.world);
						Location to = new Location(world, chest.x, chest.y, chest.z);
						player.teleport(to);
					} catch (Exception e) {
						sendConsole("Error on handling Chestindex Command tp.");
					}
				}
			}

			return true;
		}
		return false;
	}
}
