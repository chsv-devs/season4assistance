package com.jungwuk.season4assistance.listeners;

import com.sucy.skill.api.event.PlayerLevelUpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SkillApiListener implements Listener {
    @EventHandler
    public void onPlayerLevelUp(PlayerLevelUpEvent ev) {
        int level = ev.getLevel();
        String playerName = ev.getPlayerData().getPlayerName();
        Player player = Bukkit.getPlayer(playerName);

        if (player != null){
            if (level == 2) {
                player.sendMessage("  §l§b축하드립니다! §7이제 스텟을 올려보세요!");
            }
        }

        Bukkit.broadcastMessage("  §l§b" + playerName + "§7님이 §b" + level + "§7레벨 달성!");
    }
}
