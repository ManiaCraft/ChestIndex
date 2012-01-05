package de.maniacraft.chestindex;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;


public class ChestPlayerListener extends PlayerListener {

	private final Chestindex plugin;
    public ChestPlayerListener(Chestindex instance) {
        plugin = instance;
    }
    
  @Override
	public void onPlayerInteract(PlayerInteractEvent event) {
    Action action = event.getAction();
    Player player = event.getPlayer();
    if (action == Action.RIGHT_CLICK_BLOCK){
        Block block = event.getClickedBlock();
        int itemamount = 1;
        if (block.getType() == Material.CHEST) {
          Chest chest = (Chest)block.getState();
          if (chest.getInventory().contains(Material.valueOf("STONE"), itemamount)){
        	  plugin.sendPlayer(ChatColor.RED+"Eyeyey yiha, da ist ein Stone drin", player);
             ItemStack[] inventory = chest.getInventory().getContents();
                     for (ItemStack x : inventory) {
                       if(x != null) {
                    	   plugin.sendConsole(x.getType() + " - " + x.getAmount());
                       }
                     }
          } else {
            player.sendMessage(ChatColor.RED+"Ouohh, da ist kein Stone drin :(");
          }
        }
    }
  }
}