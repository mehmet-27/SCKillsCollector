package com.mehmet_27.sckillscollector;

import net.sacredlabyrinth.phaed.simpleclans.events.CreateClanEvent;
import org.bukkit.event.Listener;

public class CreateClan implements Listener {
    SQLGetter sql = new SQLGetter(SCKillsCollector.getInstance());
    public void createClan(CreateClanEvent event){
        String clanName = event.getClan().getName();
        String clanTag = event.getClan().getTag();
        sql.addClan(clanName, clanTag);
    }
}
