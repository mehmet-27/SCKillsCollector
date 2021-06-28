package com.mehmet_27.sckillscollector;

import net.sacredlabyrinth.phaed.simpleclans.events.DisbandClanEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DisbandClan implements Listener {
    @EventHandler
    public void deleteClan(DisbandClanEvent event){
        if (!SCKillsCollector.getInstance().getConfig().getBoolean("settings.clanDisband.deleteIfClanDisbanded")){
            return;
        }
        String clanName = event.getClan().getName();
        String clanTag = event.getClan().getTag();
        SCKillsCollector.getSqlGetter().deleteClan(clanTag);
        if (SCKillsCollector.getInstance().getConfig().getBoolean("settings.clanDisband.consoleMessage")){
            SCKillsCollector.getInstance().getLogger().info("The clan named " + clanName +  " was deleted from the database because it was disbanded.");
        }
    }
}
