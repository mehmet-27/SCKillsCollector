package com.mehmet_27.sckillscollector;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class SQLGetter {

    private final SCKillsCollector plugin;
    private final Connection connection;

    public SQLGetter(SCKillsCollector plugin) {
        this.plugin = plugin;
        connection = plugin.getMySQL().getConnection();
    }

    public void createTable() {
        try {
            String createTableQuery = "CREATE TABLE IF NOT EXISTS sc_clankills ("
                    + " tag VARCHAR(100),"
                    + " clan VARCHAR(100),"
                    + " kills INT(100),"
                    + " PRIMARY KEY (tag))";
            PreparedStatement ps = connection.prepareStatement(createTableQuery);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadClans() {
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `sc_clans`");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String clan = results.getString("name");
                String tag = results.getString("tag");
                addClan(clan, tag);
            }
            plugin.getLogger().info(Utils.color("&aClans loaded."));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addClan(String clan, String tag) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT IGNORE INTO sc_clankills (`clan`, `tag`, `kills`) VALUES (?,?,?)");
            ps.setString(1, clan);
            ps.setString(2, tag);
            ps.setInt(3, 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadKills() {
        try {
            HashMap<String, Integer> killsMap = new HashMap<>();
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `sc_players`");
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                String clanTag = results.getString("tag");
                int neutralKills = results.getInt("neutral_kills");
                int rivalKills = results.getInt("rival_kills");
                int civilianKills = results.getInt("civilian_kills");
                int totalKills = neutralKills + rivalKills + civilianKills;
                killsMap.compute(clanTag, (k, v) -> {
                    if (v != null) return v + totalKills;
                    return totalKills;
                });
            }
            addKill(killsMap);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addKill(HashMap<String, Integer> killsMap) {
        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE `sc_clankills` SET `kills` = ? WHERE `tag` = ?");
            for (Map.Entry<String, Integer> entry : killsMap.entrySet()) {
                ps.setInt(1, entry.getValue());
                ps.setString(2, entry.getKey());
                if (entry.getKey().length() > 1) {
                    ps.addBatch();
                    Bukkit.getLogger().info("added " + entry.getValue() + " kills to " + entry.getKey() + " clan");
                }
            }
            //add if check
            ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteClan(String clanTag) {
        Bukkit.getScheduler().runTaskAsynchronously(SCKillsCollector.getInstance(), () -> {
            try {
                PreparedStatement ps = connection.prepareStatement("DELETE FROM `sc_clankills` WHERE tag = ?");
                ps.setString(1, clanTag);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void UpdateAllKills() {
        Bukkit.getScheduler().runTaskAsynchronously(SCKillsCollector.getInstance(), () -> {
            loadClans();
            loadKills();
            if (plugin.getConfig().getBoolean("settings.updateAllKillsMessage")) {
                plugin.getLogger().info("All clan kills have been updated.");
            }
        });
    }
}
