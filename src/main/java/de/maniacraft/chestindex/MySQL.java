package de.maniacraft.chestindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import com.griefcraft.model.Protection;
import de.maniacraft.chestindex.Config;

public class MySQL {
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private PreparedStatement preparedStatement = null;

	public boolean Connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(Config.getString("Database"));
			if (connect.getWarnings() != null)
				Chestindex.sendConsole(connect.getWarnings().getMessage());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			//close();
			return false;
		}
	}

	public void addChest(Player player, World world, int x, int y, int z) {
		try {
			preparedStatement = connect.prepareStatement("INSERT INTO ci_chests (player, world, x, y, z) VALUES (?, ?, ?, ?, ?)");
			preparedStatement.setString(1, player.getName());
			preparedStatement.setString(2, world.getName());
			preparedStatement.setInt(3, x);
			preparedStatement.setInt(4, y);
			preparedStatement.setInt(5, z);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			//close();
		}
	}

	public void removeChest(World world, int x, int y, int z) {
		try {
			preparedStatement = connect.prepareStatement("DELETE FROM ci_chests WHERE world = ? AND x = ? AND y = ? AND z = ?");
			preparedStatement.setString(1, world.getName());
			preparedStatement.setInt(2, x);
			preparedStatement.setInt(3, y);
			preparedStatement.setInt(4, z);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			Chestindex.sendConsole(Chestindex.prefix + " Error on removing Chest from Database.");
			e.printStackTrace();
			//close();
		}
	}
	
	public void editChest(World world, int from_x, int from_y, int from_z, int to_x, int to_y, int to_z) {
		try {
			preparedStatement = connect.prepareStatement("UPDATE ci_chests SET x = ?, y = ?, z = ? WHERE world = ? AND x = ? AND y = ? AND z = ?");
			preparedStatement.setInt(1, to_x);
			preparedStatement.setInt(2, to_y);
			preparedStatement.setInt(3, to_z);
			preparedStatement.setString(4, world.getName());
			preparedStatement.setInt(5, from_x);
			preparedStatement.setInt(6, from_y);
			preparedStatement.setInt(7, from_z);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			Chestindex.sendConsole(Chestindex.prefix + " Error on updating Chest at Database.");
			e.printStackTrace();
			//close();
		}
	}
	
	public boolean isListed(World world, int x, int y, int z, String player) {
		try {
			preparedStatement = connect.prepareStatement("SELECT COUNT(*) as count FROM ci_chests WHERE  x = ? AND y = ? AND z = ? AND world = ? AND player = ?");
			preparedStatement.setInt(1, x);
			preparedStatement.setInt(2, y);
			preparedStatement.setInt(3, z);
			preparedStatement.setString(4, world.getName());
			preparedStatement.setString(5, player);
			ResultSet set = preparedStatement.executeQuery();
			if (set.next()) {
				int count = set.getInt("count");
				if (count != 0)
					return true;
			}
			set.close();
		} catch (Exception e) {
			Chestindex.sendConsole(Chestindex.prefix + " Error on checking Chests in Database.");
			e.printStackTrace();
			//close();
		}
		return false;
	}

	public boolean isListed(World world, int x, int y, int z) {
		try {
			preparedStatement = connect.prepareStatement("SELECT COUNT(*) as count FROM ci_chests WHERE  x = ? AND y = ? AND z = ? AND world = ?");
			preparedStatement.setInt(1, x);
			preparedStatement.setInt(2, y);
			preparedStatement.setInt(3, z);
			preparedStatement.setString(4, world.getName());
			ResultSet set = preparedStatement.executeQuery();
			if (set.next()) {
				int count = set.getInt("count");
				if (count != 0)
					return true;
			}
			set.close();
		} catch (Exception e) {
			Chestindex.sendConsole(Chestindex.prefix + " Error on checking Chests in Database.");
			e.printStackTrace();
			// close();
		}
		return false;
	}

	public List<Chest> getChests(String player) {
		List<Chest> ret = new LinkedList<Chest>();
		if (!Chestindex.LWC) {
			try {
				preparedStatement = connect.prepareStatement("SELECT world, x, y, z, player FROM ci_chests WHERE player = ?");
				preparedStatement.setString(1, player);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					World world = Bukkit.getWorld(resultSet.getString(1));
					Block block = world.getBlockAt(resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4));
					if (block.getType() == Material.CHEST) {
						Chest chest = (Chest) block.getState();
						ret.add(chest);
					} else {
						removeChest(world, resultSet.getInt(2), resultSet.getInt(3), resultSet.getInt(4));
						Chestindex.sendConsole(Chestindex.prefix + " Removing Database Entry for Chest at " + resultSet.getInt(2) + " " + resultSet.getInt(3) + " " + resultSet.getInt(4));
					}
				}
				resultSet.close();
				return ret;
			} catch (Exception e) {
				Chestindex.sendConsole(Chestindex.prefix + " Error on getting Chests from Database.");
				e.printStackTrace();
				// close();
				return null;
			}
		} else {
			List<Protection> protections = Chestindex.lwc.getPhysicalDatabase().loadProtectionsByPlayer(player);
			try {
				for (Protection protection : protections) {
					World world = Bukkit.getWorld(protection.getWorld());
					Block block = world.getBlockAt(protection.getX(), protection.getY(), protection.getZ());
					if (block.getType() == Material.CHEST) {
						/*
						BlockFace[] faces = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };

						// Double Chest (LWC)
						for (BlockFace blockFace : faces) {
							Block face = block.getRelative(blockFace);
							if (face.getType() == Material.CHEST) {
								Chest chest = (Chest) face.getState();
								ret.add(chest);
							}
						}
						*/
						Chest chest = (Chest) block.getState();
						ret.add(chest);
					}
				}
				return ret;
			} catch (Exception e) {
				Chestindex.sendConsole(Chestindex.prefix + " Error on getting Chests from LWC.");
				e.printStackTrace();
				// close();
				return null;
			}
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
