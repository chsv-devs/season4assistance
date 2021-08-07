package com.jungwuk.season4assistance.listeners;

import com.jungwuk.season4assistance.HUtils;
import fr.skytasul.quests.BeautyQuests;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.events.DialogSendMessageEvent;
import fr.skytasul.quests.api.events.QuestFinishEvent;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class BeautyQuestListener implements Listener {
    HashMap<Integer, Integer> questBridge = new HashMap<>();

    public BeautyQuestListener() {
        File configFile = new File("plugins/season4assistance/questBridge.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        MemorySection memorySection = (MemorySection) config.get("bridge");

        if (memorySection == null) {
            questBridge.put(-1, -2);
            config.set("bridge", questBridge);
            try {
                config.save(configFile);
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.WARNING, "", e);
            }
        } else {
            for (String key : memorySection.getKeys(false)) {
                questBridge.put(Integer.parseInt(key), memorySection.getInt(key));
            }
        }
    }

    /**
     * 퀘스트의 메세지를 타이틀로 출력해줍니다.
     */
    @EventHandler(ignoreCancelled = true)
    public void onDialogSendMessage(DialogSendMessageEvent ev) {
        Message msg = ev.getMessage();
        NPC npc = ev.getNPC();
        Player player = ev.getPlayer();
        int wait = msg.getWaitTime();

        if (!HUtils.isJePlayer(player)) {
            return;
        }
        if (msg.sender == Message.Sender.PLAYER) {
            player.sendTitle(ChatColor.getByChar('d') + player.getName(),
                    ChatColor.getByChar('7') + msg.text, 5, wait, 5);
        } else {
            player.sendTitle(npc.getName(), msg.text, 5, wait, 5);
        }
    }

    /**
     * 퀘스트 시작을 타이틀로 알려줍니다.
     */
    @EventHandler(ignoreCancelled = true)
    public void onLaunchQuest(QuestLaunchEvent ev) {
        Player p = ev.getPlayer();
        Quest quest = ev.getQuest();
        Location loc = p.getLocation();

        p.sendTitle(ChatColor.YELLOW + "퀘스트 시작", quest.getName(), 10, 60, 10);
        p.playSound(loc, Sound.BLOCK_CHEST_OPEN, 100, 1);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRightClickNpc(NPCRightClickEvent ev){
        handleNpcClickEvent(ev);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeftClickNpc(NPCLeftClickEvent ev) {
        handleNpcClickEvent(ev);
    }

    public void handleNpcClickEvent(NPCClickEvent ev) {
        NPCStarter starter = BeautyQuests.getInstance().getNPCs().get(ev.getNPC());

        if (starter != null) {
            PlayerAccount account = PlayersManager.getPlayerAccount(ev.getClicker());
            if (hasStartedQuest(starter, account)) {
                String msg = HUtils.getQuestStatusMsg(account);
                Quest startedQuest = getStartedQuest(starter, account);
                ev.getClicker().sendMessage("§o이 NPC와 진행중인 퀘스트 : "
                        + startedQuest.getName() + "\n"
                        + ChatColor.RED + "현재 진행중인 퀘스트 정보\n" + msg);
                ev.getClicker().sendTitle("진행 중 : " + startedQuest.getName(), startedQuest.getDescription(), 10, 70, 10);
            } else {
                Quest questToStart = getQuestToStart(starter, ev.getClicker());
                if (questToStart != null) {
                    if (!questToStart.testRequirements(ev.getClicker(), account, false)) {
                        Iterator<AbstractRequirement> requirements = questToStart.getOptionValueOrDef(OptionRequirements.class).iterator();

                        while (requirements.hasNext()) {
                            final AbstractRequirement ar;
                            ar = requirements.next();

                            if (ar instanceof QuestRequirement) {
                                QuestRequirement questRequirement = (QuestRequirement) ar;
                                Quest requiredQuest = QuestsAPI.getQuestFromID(questRequirement.questId);

                                Map.Entry<NPC, NPCStarter> entry = getNpcByQuestId(requiredQuest.getID());
                                if (entry != null) {
                                    NPC npc = entry.getKey();

                                    ar.sendReason(ev.getClicker());
                                    ev.getClicker().sendMessage(requiredQuest.getName() + ChatColor.GRAY
                                            + "를 수행하려면 " + ChatColor.RESET
                                            + npc.getName() + ChatColor.GRAY + "에게 찾아가세요.");
                                    return;
                                }
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean hasStartedQuest(NPCStarter starter, PlayerAccount account) {
        for (Quest quest : starter.getQuests()) {
            if (quest.hasStarted(account)) {
                return true;
            }
        }

        return false;
    }
    public Quest getStartedQuest(NPCStarter starter, PlayerAccount account) {
        for (Quest quest : starter.getQuests()) {
            if (quest.hasStarted(account)) {
                return quest;
            }
        }

        return null;
    }

    public Quest getQuestToStart(NPCStarter starter, Player player) {
        PlayerAccount account = PlayersManager.getPlayerAccount(player);
        for (Quest quest : starter.getQuests()) {
            if (!quest.hasStarted(account) && !quest.hasFinished(account)) {
                return quest;
            }
        }

        return null;
    }

    public Map.Entry<NPC, NPCStarter> getNpcByQuestId(int targetQuestId) {
        Map<NPC, NPCStarter> npcStarterMap = BeautyQuests.getInstance().getNPCs();

        for (Map.Entry<NPC, NPCStarter> entry : npcStarterMap.entrySet()) {
            NPCStarter starter = entry.getValue();
            for (Quest quest : starter.getQuests()) {
                if (quest.getID() == targetQuestId) {
                    return entry;
                }
            }
        }

        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void questBridge(QuestFinishEvent ev) {
        Quest quest = ev.getQuest();
        if (questBridge.containsKey(quest.getID())) {
            Quest newQuest = QuestsAPI.getQuestFromID(questBridge.get(quest.getID()));
            newQuest.clickNPC(ev.getPlayer());
        }
    }
}