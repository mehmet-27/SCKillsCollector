package com.mehmet_27.sckillscollector;

import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CreateClan implements Listener {
    @EventHandler
    public void createClan(CreateClanEvent event){
        String clanName = event.getClan().getName();
        String clanTag = event.getClan().getTag();
        SCKillsCollector.getSqlGetter().addClan(clanName, clanTag);
    }
}
