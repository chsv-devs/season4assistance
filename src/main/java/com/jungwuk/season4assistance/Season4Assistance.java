package com.jungwuk.season4assistance;

import com.jungwuk.season4assistance.listeners.BeautyQuestListener;
import com.jungwuk.season4assistance.listeners.GeneralListener;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.players.PlayerAccount;
import fr.skytasul.quests.players.PlayersManager;
import fr.skytasul.quests.structure.Quest;
import fr.skytasul.quests.structure.QuestBranch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public final class Season4Assistance extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        this.getServer().getPluginManager().registerEvents(new BeautyQuestListener(),this);
    }

    @Override
    public void onDisable() {
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("qs")) {
            Player p = (Player) sender;
            PlayerAccount acc = PlayersManager.getPlayerAccount(p);
            List<Quest> startedQuests = QuestsAPI.getQuestsStarteds(acc);

            if (startedQuests.size() == 0) {
                p.sendMessage("  §4이런! 진행중인 퀘스트가 없습니다.");
                return true;
            }

            for (Quest startedQuest : startedQuests) {
                p.sendMessage("§7========== §c" + startedQuest.getName() + " §7==========");
                p.sendMessage(startedQuest.getBranchesManager().getDescriptionLine(acc, QuestBranch.Source.SCOREBOARD)
                        .replace("{nl}", "\n")
                        + "\n");
            }
        }
        return true;
    }
}
