package com.jungwuk.season4assistance.listeners;

import com.jungwuk.season4assistance.HUtils;
import fr.skytasul.quests.api.QuestsAPI;
import fr.skytasul.quests.api.mobs.Mob;
import fr.skytasul.quests.api.stages.AbstractStage;
import fr.skytasul.quests.players.PlayerAccount;
import fr.skytasul.quests.players.PlayerQuestDatas;
import fr.skytasul.quests.players.PlayersManager;
import fr.skytasul.quests.stages.StageMobs;
import fr.skytasul.quests.structure.Quest;
import fr.skytasul.quests.structure.QuestBranch;
import fr.skytasul.quests.utils.compatibility.SkillAPI;
import io.lumine.xikage.mythicmobs.api.bukkit.BukkitAPIHelper;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
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

            handlePlayerDamager(ev);
            if (ev.isCancelled()) {
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
        } else if (bukkitHelper.isMythicMob(ev.getDamager())) {
            handleMobDamager(ev);
        }
    }

    public void handlePlayerDamager(EntityDamageByEntityEvent ev) {
        Player p = (Player) ev.getDamager();

        if (bukkitHelper.isMythicMob(ev.getEntity())) {
            ActiveMob mob = bukkitHelper.getMythicMobInstance(ev.getEntity());
            if (!isAttackable(mob, p)) {
                ev.setCancelled(true);
                p.sendTitle("§c이런!", "§o몹과의 레벨차이가 §410§f이상입니다.", 5, 60, 5);
            }
        }
    }

    public void handleMobDamager(EntityDamageByEntityEvent ev) {
        if (!(ev.getEntity() instanceof Player)) return;
        Player p = (Player) ev.getEntity();

        ActiveMob mob = bukkitHelper.getMythicMobInstance(ev.getDamager());
        if (!isAttackable(mob, p)) {
            ev.setCancelled(true);
            mob.voidTargetChange();
        }
    }

    public boolean isAttackable(ActiveMob m, Player p) {
        int mobLvl = HUtils.parseMobLevel(m.getEntity().getName());
        int pLvl = SkillAPI.getLevel(p);
        if (Math.abs(mobLvl - pLvl) <= 10) {
            return true;
        }

        PlayerAccount acc = PlayersManager.getPlayerAccount(p);
        for (Quest questsStarted : QuestsAPI.getQuestsStarteds(acc)) {
            PlayerQuestDatas data = acc.getQuestDatas(questsStarted);
            QuestBranch branch = questsStarted.getBranchesManager().getBranch(data.getBranch());
            AbstractStage stage = branch.getRegularStage(data.getStage());

            if (stage instanceof StageMobs) {
                StageMobs entityStage = (StageMobs) stage;
                for (Map.Entry<Integer, Map.Entry<Mob<?>, Integer>> e : entityStage.getObjects().entrySet()) {
                    Entry<Mob<?>, Integer> entry = e.getValue();
                    int id = e.getKey();
                    if (entry.getKey().getName().equals(m.getEntity().getName())) {
                        Integer amount = entityStage.getPlayerRemainings(acc).get(id);
                        if (amount != null) {
                            String mobName = ChatColor.stripColor(m.getEntity().getName());
                            p.sendMessage("  §7" + mobName + " §4" + amount + "§7마리 남음!");
                            return true;
                        } else {
                            p.sendMessage("  §7이미 이 몹의 §4퀘스트 목표§7를 달성하였습니다!");
                        }
                    }
                }
            }
        }
        return false;
    }
}
