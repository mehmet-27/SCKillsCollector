package com.mehmet_27.sckillscollector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    private final SCKillsCollector plugin;
    private Connection connection;

    private final String host;
    private final String port;
    private final String database;
    private final String username;
    private final String password;

    public MySQL(SCKillsCollector plugin) {
        this.plugin = plugin;

        host = plugin.getConfig().getString("mysql.host");
        port = plugin.getConfig().getString("mysql.port");
        database = plugin.getConfig().getString("mysql.database");
        username = plugin.getConfig().getString("mysql.username");
        password = plugin.getConfig().getString("mysql.password");
        connect();
    }

    //MySQL connection
    public void connect() {
        if (connection != null) {
            plugin.getLogger().info(Utils.color("&aDatabase is connected!"));
            return;
        }

        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
        } catch (SQLException ex) {
            plugin.getLogger().severe(Utils.color("&cDatabase not connected! \n") + ex.getMessage());
        }
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}

