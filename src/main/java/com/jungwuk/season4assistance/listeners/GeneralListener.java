package com.jungwuk.season4assistance.listeners;

import com.jungwuk.season4assistance.MapImageRenderer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

public class GeneralListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        if (!ev.getPlayer().hasPlayedBefore()) {
            World world = Bukkit.getWorld("튜토리얼");
            Location loc = new Location(world, 783, 123, 73);
            ev.getPlayer().teleport(loc);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent ev) {
        Player player = ev.getPlayer();
        if (ev.getMessage().equals("**GETTESTIMG") && player.isOp()) {
            MapView mapView = Bukkit.createMap(player.getWorld());
            mapView.getRenderers().clear();
            mapView.addRenderer(new MapImageRenderer(true));
            ItemStack mapItem = new ItemStack(Material.MAP);
            mapItem.setDurability(mapView.getId());
            player.getInventory().addItem(mapItem);
        }
    }
}
