package com.mehmet_27.sckillscollector;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class SCKillsCollector extends JavaPlugin {
    private static SCKillsCollector instance;

    private MySQL mySQL;
    public SQLGetter sqlGetter;

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        instance = this;
        this.getServer().getPluginManager().registerEvents(new DisbandClan(), this);
        this.getServer().getPluginManager().registerEvents(new CreateClan(), this);
        mySQL = new MySQL(this);
        if (mySQL.getConnection() == null) {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        sqlGetter = new SQLGetter(this);
        sqlGetter.createTable();
        sqlGetter.loadClans();
        sqlGetter.loadKills();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        mySQL.disconnect();

    }
    public MySQL getMySQL() {
        return mySQL;
    }
    public static SCKillsCollector getInstance(){
        return instance;
    }
}