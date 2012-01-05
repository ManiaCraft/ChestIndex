package de.maniacraft.chestindex;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import de.maniacraft.chestindex.MySQL;
import de.maniacraft.chestindex.listeners.ChestBlockListener;
import de.maniacraft.chestindex.listeners.ChestPlayerListener;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;

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
}
