package de.maniacraft.chestindex;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Config {
	private static Plugin getPlugin() {
	   return Bukkit.getServer().getPluginManager().getPlugin("ChestIndex");
	}
	
    public static void load() {
    	Plugin plugin = getPlugin();
    	plugin.getConfig().addDefault("Database",			"jdbc:mysql://localhost/database?user=root&password=password");
    	plugin.getConfig().options().copyDefaults(true);
    	plugin.saveConfig();
    }

    public static String getString(String key){
        return getPlugin().getConfig().getString(key,"");
    }

    public static Integer getInteger(String key){
    	return getPlugin().getConfig().getInt(key, 0);
    }
}
