package com.jungwuk.season4assistance;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class HUtils {

    // 몹 레벨을 이름에서 가져옵니다.
    public static int parseMobLevel(String entityName) {
        try {
            if (entityName.contains("[")) {
                String filtered = ChatColor.stripColor(entityName).split("\\[")[1].split("레벨")[0].trim();
                if (filtered.contains("]")) {
                    return 777777;
                }

                return Integer.parseInt(filtered);
            } else {
                return 999999;
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "몹 레벨을 가져오지 못했습니다.", e);
        }
        return -1;
    }

    // JE 유저인지 확인합니다.
    public static boolean isJePlayer(Player player) {
        return player.getName().startsWith(".") == false;   // 이름이 마침표로 시작하지 않을경우 JE유저로 판단합니다.
    }
}
