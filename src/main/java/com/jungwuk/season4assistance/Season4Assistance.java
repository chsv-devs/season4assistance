package com.jungwuk.season4assistance;

import com.jungwuk.season4assistance.listeners.BeautyQuestListener;
import com.jungwuk.season4assistance.listeners.GeneralListener;
import com.jungwuk.season4assistance.listeners.MythicMobsListener;
import com.jungwuk.season4assistance.listeners.SkillApiListener;
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
        this.getServer().getPluginManager().registerEvents(new SkillApiListener(), this);
        this.getServer().getPluginManager().registerEvents(new MythicMobsListener(), this);
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
            String msg = HUtils.getQuestStatusMsg(acc);

            if (msg.length() == 0) {
                p.sendMessage("  §4이런! 진행중인 퀘스트가 없습니다.");
                return true;
            }

            p.sendMessage(msg);
        }
        return true;
    }
}
