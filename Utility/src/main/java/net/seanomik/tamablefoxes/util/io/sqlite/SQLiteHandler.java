package net.seanomik.tamablefoxes.util.io.sqlite;

import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteHandler {
	private Connection connection;

	private static SQLiteHandler instance;

	public static SQLiteHandler getInstance() {
		if (instance == null) {
			instance = new SQLiteHandler();
		}

		return instance;
	}

	public synchronized void connect(Plugin plugin) {
		String pluginFolder = plugin.getDataFolder().getAbsolutePath();
		connect(pluginFolder);
	}

	public synchronized void connect(String pluginFolder) {
		try {
			// Reuse existing connection if it's still valid
			if (connection != null && !connection.isClosed()) {
				return;
			}

			String url = "jdbc:sqlite:" + pluginFolder + "/userFoxAmount.db";
			connection = DriverManager.getConnection(url);

			// Enable WAL mode for better concurrency
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("PRAGMA journal_mode=WAL;");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized Connection getConnection() {
		return connection;
	}

	public synchronized void closeConnection() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized void newConnection(String pluginFolder) {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
			connect(pluginFolder);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public synchronized void setConnection(Connection connection) {
		this.connection = connection;
	}
}
