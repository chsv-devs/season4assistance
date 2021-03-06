package com.jungwuk.season4assistance;

import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.players.PlayerAccount;
import fr.skytasul.quests.structure.Quest;
import fr.skytasul.quests.structure.QuestBranch;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;

public class HUtils {
    public static LinkedHashMap<String, Integer> cachedMobLevel = new LinkedHashMap<>();

    // 몹 레벨을 이름에서 가져옵니다.
    public static int parseMobLevel(String entityName) {
        Integer cachedLevel = cachedMobLevel.get(entityName);
        if (cachedLevel != null) {
            return cachedLevel;
        }

        int level;
        try {
            if (entityName.contains("[")) {
                String filtered = ChatColor.stripColor(entityName).split("\\[")[1].split("레벨")[0].trim();
                if (filtered.contains("]")) {
                    level = -1;
                } else {
                    level = Integer.parseInt(filtered);
                }
            } else {
                level = -2;
            }
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, "몹 레벨을 가져오지 못했습니다.", e);
            level = -3;
        }
        cachedMobLevel.put(entityName, level);
        return level;
    }

    // JE 유저인지 확인합니다.
    public static boolean isJePlayer(Player player) {
        return player.getName().startsWith(".") == false;   // 이름이 마침표로 시작하지 않을경우 JE유저로 판단합니다.
    }

    public static String getQuestStatusMsg(PlayerAccount acc) {
        StringBuilder sb = new StringBuilder();
        List<Quest> startedQuests = QuestsAPI.getQuestsStarteds(acc);
        for (Quest startedQuest : startedQuests) {
            sb.append("§7========== §c").append(startedQuest.getName()).append(" §7==========");
            sb.append(startedQuest.getBranchesManager().getDescriptionLine(acc, QuestBranch.Source.SCOREBOARD)
                    .replace("{nl}", "\n"));
            sb.append("\n");
        }
        return sb.toString();
    }
}
