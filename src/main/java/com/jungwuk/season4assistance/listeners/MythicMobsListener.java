package com.jungwuk.season4assistance.listeners;

import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.UUID;

public class MythicMobsListener implements Listener {
    public final BukkitAPIHelper bukkitHelper = new BukkitAPIHelper();
    // Key : MythicMob UUID, value : PlayerName-LastAttackedTimestamp
    public final HashMap<UUID, String> mythicMobAndPlayer = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAttackMythicMob(EntityDamageByEntityEvent ev) {
        if (ev.getDamager() instanceof Player) {
            Player p = (Player) ev.getDamager();
            Entity entity = ev.getEntity();

            if (!bukkitHelper.isMythicMob(entity)) {
                return;
            }

            if (mythicMobAndPlayer.containsKey(entity.getUniqueId())) {
                String v = mythicMobAndPlayer.get(entity.getUniqueId());
                String[] tmp = v.split("-");
                String vName = tmp[0];
                long vTimestamp = Long.parseLong(tmp[1]);

                if (vName.equals(p.getName())) {
                    mythicMobAndPlayer.put(entity.getUniqueId(), p.getName() + "-" + System.currentTimeMillis());
                    return;
                }

                long current = System.currentTimeMillis();
                if (current - vTimestamp < 1000 * 5) {
                    p.sendMessage("  §7이미 누군가가 §4사냥중인 §7몹입니다!");
                    ev.setCancelled(true);
                } else {
                    mythicMobAndPlayer.put(entity.getUniqueId(), p.getName() + "-" + System.currentTimeMillis());
                }
            } else {
                mythicMobAndPlayer.put(entity.getUniqueId(), p.getName() + "-" + System.currentTimeMillis());
            }
        }
    }
}
