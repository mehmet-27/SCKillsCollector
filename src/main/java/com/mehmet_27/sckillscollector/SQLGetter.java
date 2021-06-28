package com.mehmet_27.sckillscollector;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
                    + " clan VARCHAR(100),"
                    + " tag VARCHAR(100),"
                    + " kills INT(100),"
                    + " PRIMARY KEY (clan))";
            PreparedStatement ps = connection.prepareStatement(createTableQuery);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadClans() {
        Bukkit.getScheduler().runTaskAsynchronously(SCKillsCollector.getInstance(), () -> {
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
        });
    }

    public void addClan(String clan, String tag) {
        Bukkit.getScheduler().runTaskAsynchronously(SCKillsCollector.getInstance(), () -> {
            try {
                PreparedStatement ps = connection.prepareStatement("INSERT IGNORE INTO sc_clankills (`clan`, `tag`) VALUES (?,?)");
                ps.setString(1, clan);
                ps.setString(2, tag);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void loadKills() {
        Bukkit.getScheduler().runTaskAsynchronously(SCKillsCollector.getInstance(), () -> {
            try {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM `sc_players`");
                ResultSet results = ps.executeQuery();
                while (results.next()) {
                    String clan = results.getString("tag");
                    int neutralKills = results.getInt("neutral_kills");
                    int rivalKills = results.getInt("rival_kills");
                    int civilianKills = results.getInt("civilian_kills");
                    int totalKills = neutralKills + rivalKills + civilianKills;
                    addKill(clan, totalKills);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void addKill(String clanTag, int kill) {
        Bukkit.getScheduler().runTaskAsynchronously(SCKillsCollector.getInstance(), () -> {
            try {
                int oldKills = getClanKills(clanTag);
                int newKills = oldKills + kill;
                PreparedStatement ps = connection.prepareStatement("UPDATE `sc_clankills` SET `kills` = ? WHERE `tag` = ?");
                ps.setInt(1, newKills);
                ps.setString(2, clanTag);
                ps.executeUpdate();
                plugin.getLogger().info("kill added " + kill + " " + clanTag + " old kills: " + oldKills);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public int getClanKills(String clanTag) {
        int kills = 0;
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM `sc_clankills` WHERE `tag` = ?");
            ps.setString(1, clanTag);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                kills = results.getInt("kills");
                return kills;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kills;
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
    public void UpdateAllKills(){
        Bukkit.getScheduler().runTaskAsynchronously(SCKillsCollector.getInstance(), () -> {
            try {
                PreparedStatement ps = connection.prepareStatement("UPDATE `sc_clankills` SET `kills` = 0 WHERE `tag`");
                ps.executeUpdate();
            }catch (SQLException e){
                e.printStackTrace();
            }
        });
        loadClans();
        loadKills();
        if(plugin.getConfig().getBoolean("settings.updateAllKillsMessage")){
            Bukkit.getLogger().info("All clan kills have been updated.");
        }
    }
}
