package com.mehmet_27.sckillscollector;

import org.bukkit.scheduler.BukkitRunnable;

public class UpdateKillsTask extends BukkitRunnable {
    SCKillsCollector plugin = SCKillsCollector.getInstance();
    SQLGetter sql = new SQLGetter(plugin);

    public void start() {
        long interval = (long) plugin.getConfig().getInt("settings.updateAllKillsInterval") * 60 * 20;
        runTaskTimerAsynchronously(plugin, interval, interval);
    }
    @Override
    public void run() {
        sql.UpdateAllKills();
    }
}
