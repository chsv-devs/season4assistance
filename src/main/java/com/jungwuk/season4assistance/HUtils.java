package com.jungwuk.season4assistance;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public class HUtils {
    public static int parseMobLevel(String name) {
        try {
            if (name.contains("[")) {
                String filtered = ChatColor.stripColor(name).split("\\[")[1].split("레벨")[0].trim();
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
}
