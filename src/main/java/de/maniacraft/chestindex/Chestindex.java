package de.maniacraft.chestindex;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import de.maniacraft.chestindex.MySQL;
import de.maniacraft.chestindex.listeners.ChestBlockListener;
import de.maniacraft.chestindex.listeners.ChestPlayerListener;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Chestindex extends JavaPlugin {
	
	static final Logger log = Logger.getLogger("Minecraft");
	private final ChestPlayerListener playerListener = new ChestPlayerListener(this);
	private final ChestBlockListener blockListener = new ChestBlockListener(this);
	public MySQL DB = new MySQL();
	
    public void onDisable() {
        // TODO: Place any custom disable code here.
        System.out.println(this + " is now disabled!");
    }

    public void onEnable() {
		Config.load();
        DB.Connect();                
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
        System.out.println(this + " is now enabled!");
    }
    
    
    public void sendConsole(String text) {
    		System.out.println(text);
    		
    }
    public void sendPlayer(String text, Player player) {
		player.sendMessage(text);
		
    }
    
    public ItemStack[] getChestContent(Block block) {
        /*
        BlockFace[] faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        for (BlockFace blockFace : faces) {
            Block face = block.getRelative(blockFace);
            if (face.getType() == Material.CHEST) {
            	// Double Chest
            	Chest chest2 = (Chest)face.getState();
            	inventory = chest2.getInventory().getContents();
            }
        }
        */
    	Chest chest = (Chest)block.getState();
    	ItemStack[] inventory = chest.getInventory().getContents();
    	return inventory;
    }
    
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String commandName = command.getName().toLowerCase();

        if (commandName.compareToIgnoreCase("chestindex") == 0) {
        	if (args.length < 1) {
        		//Help
            } else if (args.length > 1) {
            	List<Chest> chests = DB.getChests(sender.getName());
            	for(Chest chest : chests) {
            		ItemStack[] inventory = chest.getInventory().getContents();
                    for (ItemStack x : inventory) {
                        if(x != null) {
                        	 if (chest.getInventory().contains(Material.valueOf(args[1]))){
                        		//sendConsole(x.getType() + " - " + x.getAmount());
                        		IndexChest[] found = new IndexChest[0];
                        		found[1] = new IndexChest(chest.getX(),chest.getY(), chest.getZ(), x.getAmount());
                        	 }
                     	   
                        }
                    }
            	}
        }
        return true;
    }
        return false;
}
}
