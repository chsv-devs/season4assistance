package com.jungwuk.season4assistance.season4assistance;

import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.events.DialogSendMessageEvent;
import fr.skytasul.quests.api.events.QuestLaunchEvent;
import fr.skytasul.quests.api.requirements.AbstractRequirement;
import fr.skytasul.quests.options.OptionRequirements;
import fr.skytasul.quests.players.PlayerAccount;
import fr.skytasul.quests.players.PlayersManager;
import fr.skytasul.quests.requirements.QuestRequirement;
import fr.skytasul.quests.structure.NPCStarter;
import fr.skytasul.quests.structure.Quest;
import fr.skytasul.quests.utils.types.Message;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Iterator;
import java.util.List;

public final class Season4Assistance extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(ignoreCancelled = true)
    public void onDialogSendMessage(DialogSendMessageEvent ev) {
        Message msg = ev.getMessage();
        NPC npc = ev.getNPC();
        Player player = ev.getPlayer();
        int wait = msg.getWaitTime();

        if (msg.sender == Message.Sender.PLAYER) {
            player.sendTitle(ChatColor.getByChar('d') + player.getName(),
                    ChatColor.getByChar('7') + msg.text, 5, wait, 5);
        } else {
            player.sendTitle(npc.getName(), msg.text, 5, wait, 5);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onLaunchQuest(QuestLaunchEvent ev) {
        Player p = ev.getPlayer();
        Quest quest = ev.getQuest();
        Location loc = p.getLocation();

        p.sendTitle(ChatColor.YELLOW + "퀘스트 시작", quest.getName(), 10, 60, 10);
        p.playSound(loc, Sound.BLOCK_CHEST_OPEN, 100, 1);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRightClickNpc(NPCRightClickEvent ev){
        handleNpcClickEvent(ev);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeftClickNpc(NPCLeftClickEvent ev) {
        handleNpcClickEvent(ev);
    }

    public void handleNpcClickEvent(NPCClickEvent ev) {
        NPCStarter starter = BeautyQuests.getInstance().getNPCs().get(ev.getNPC());

        if (starter != null) {
            for (Quest quest : starter.getQuests()) {
                PlayerAccount account = PlayersManager.getPlayerAccount(ev.getClicker());
                if (!quest.testRequirements(ev.getClicker(), account, false)) {
                    Iterator<AbstractRequirement> requirements = quest.getOptionValueOrDef(OptionRequirements.class).iterator();

                    final AbstractRequirement ar;
                    if (!requirements.hasNext()) {
                        return;
                    }
                    ar = requirements.next();

                    if (ar instanceof QuestRequirement) {
                        QuestRequirement questRequirement = (QuestRequirement) ar;
                        Quest requiredQuest = BeautyQuests.getInstance().getQuests().get(questRequirement.questId);
                        BeautyQuests.getInstance().getNPCs().forEach((npc, newStarter) -> {
                            for (Quest newQuest : newStarter.getQuests()) {
                                if (newQuest.getID() == requiredQuest.getID()) {
                                    ar.sendReason(ev.getClicker());
                                    ev.getClicker().sendMessage(npc.getName() + "에게 찾아가세요.");
                                    ev.setCancelled(true);
                                }
                            }
                        });
                    }
                }
                if (quest.hasStarted(account)) {
                    ev.getClicker().sendMessage("이미 이 NPC의 퀘스트를 진행 중 입니다 : " + quest.getName() + " :: " + quest.getDescription() +"\n" +
                            ChatColor.RED + "/quest 명령어로 진행중인 퀘스트를 확인하십시오.");
                    ev.setCancelled(true);
                    return;
                }
            }
        }
    }

    @Override
    public void onDisable() {
    }
}
