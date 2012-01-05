package de.maniacraft.chestindex;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import de.maniacraft.chestindex.ChestPlayerListener;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
public class Chestindex extends JavaPlugin {
	
	static final Logger log = Logger.getLogger("Minecraft");
	private final ChestPlayerListener playerListener = new ChestPlayerListener(this);
    public void onDisable() {
        // TODO: Place any custom disable code here.
        System.out.println(this + " is now disabled!");
    }

    public void onEnable() {
        // TODO: Place any custom enable code here, such as registering events
        //playerFile = folder.getAbsolutePath() + File.separator + "players.txt";                   
           PluginManager pm = getServer().getPluginManager();
           // pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Normal, this);
           pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Priority.Normal, this);
           // log.log(Level.INFO, folder.getAbsolutePath() + File.separator + "players.txt");
        System.out.println(this + " is now enabled!");
    }
    
    public void sendConsole(String text) {
    		System.out.println(text);
    		
    }
    public void sendPlayer(String text, Player player) {
		player.sendMessage(text);
		
}
}
