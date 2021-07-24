package com.jungwuk.season4assistance.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class GeneralListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        if (!ev.getPlayer().hasPlayedBefore()) {
            World world = Bukkit.getWorld("튜토리얼");
            Location loc = new Location(world, 783, 123, 73);
            ev.getPlayer().teleport(loc);
        }
    }
}
