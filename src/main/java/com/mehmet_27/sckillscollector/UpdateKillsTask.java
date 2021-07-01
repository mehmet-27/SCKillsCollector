package com.mehmet_27.sckillscollector;

import org.bukkit.scheduler.BukkitRunnable;

public class UpdateKillsTask extends BukkitRunnable {
    SCKillsCollector plugin = SCKillsCollector.getInstance();

    public void start() {
        long interval = (long) plugin.getConfig().getInt("settings.updateAllKillsInterval") * 60 * 20;
        runTaskTimerAsynchronously(plugin, 0, interval);
    }
    @Override
    public void run() {
        SCKillsCollector.getSqlGetter().UpdateAllKills();
    }
}
