package de.maniacraft.chestindex;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.entity.Player;

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

	public void addChest(Player player, int x, int y, int z) {
		System.out.println("Inserted Chest entry");
		try {
			preparedStatement = connect.prepareStatement("INSERT INTO ci_chests (player, x, y, z) VALUES (?, ?, ?, ?)");
			preparedStatement.setString(1, player.getName());
			preparedStatement.setInt(2, x);
			preparedStatement.setInt(3, y);
			preparedStatement.setInt(4, z);
			preparedStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
			close();
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
