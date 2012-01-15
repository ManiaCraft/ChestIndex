package de.maniacraft.chestindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;
import de.maniacraft.chestindex.Config;

public class MySQL {
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private PreparedStatement preparedStatement = null;

	public void Connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(Config.getString("Database"));
			if(connect.getWarnings() != null)
	    		System.out.print(connect.getWarnings().getMessage());;
		} catch (Exception e) {
			e.printStackTrace();
			close();
		}
	}

	public List<String> getList() {
		List<String> ret = new LinkedList<String>();
		try {
			if (connect == null || connect.isClosed()) Connect();
			Class.forName("com.mysql.jdbc.Driver");
			statement = connect.createStatement();
			resultSet = statement.executeQuery(Config.getString("Fetch"));
			while (resultSet.next()) {
				ret.add(resultSet.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			close();
		}
		return ret;
	}

	public void addChest(Player player, World world, int x, int y, int z) {
		System.out.println("Inserted Chest entry");
		try {
			preparedStatement = connect.prepareStatement("INSERT INTO ci_chests (player, world, x, y, z) VALUES (?, ?, ?, ?, ?)");
			preparedStatement.setString(1, player.getName());
			preparedStatement.setString(2, world.getName());
			preparedStatement.setInt(3, x);
			preparedStatement.setInt(4, y);
			preparedStatement.setInt(5, z);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			close();
		}
	}

	public boolean isListed(Player player, int x, int y, int z) {
		try {
			preparedStatement = connect.prepareStatement("SELECT COUNT(*) as count FROM ci_chests WHERE  x = ? AND y = ? AND z = ?");
			preparedStatement.setInt(1, x);
			preparedStatement.setInt(2, y);
			preparedStatement.setInt(3, z);
			ResultSet set = preparedStatement.executeQuery();
            if (set.next()) {
              int count = set.getInt("count");
              if(count != 0) 
              return true;
            }
            set.close();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			//close();
		} 
		return false;
	}
	
	public List<Chest> getChests(String player) {
		List<Chest> ret = new LinkedList<Chest>();
		try {
			preparedStatement = connect.prepareStatement("SELECT world, x, y, z, player FROM ci_chests WHERE player = ?");
			preparedStatement.setString(1, player);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				World world = Bukkit.getWorld(resultSet.getString(1));
				System.out.println("+***" + resultSet.getString(1) + " ## # "+ resultSet.getInt(2)+ " ## # "+ resultSet.getInt(3)+ " ## # "+ resultSet.getInt(4));
				Block block = world.getBlockAt(resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4));
				//Block block = (resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4));
				Chest chest = (Chest)block.getState();
				ret.add(chest);
				//ItemStack[] inventory = chest.getInventory().getContents();
			}
			resultSet.close();
			return ret;
		} catch (Exception e) {
			System.out.println("Fehler.");
			System.out.println(e.toString());
			e.printStackTrace();
			//close();
			return null;
		}
	}
	
	private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
