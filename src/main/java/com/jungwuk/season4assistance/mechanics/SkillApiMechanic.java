package com.jungwuk.season4assistance.mechanics;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.skills.Skill;
import com.sucy.skill.api.skills.SkillShot;
import com.sucy.skill.api.skills.TargetSkill;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.io.MythicLineConfig;
import io.lumine.xikage.mythicmobs.skills.ITargetedEntitySkill;
import io.lumine.xikage.mythicmobs.skills.SkillMechanic;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.logging.Level;

public class SkillApiMechanic extends SkillMechanic implements ITargetedEntitySkill {
    private final int level;
    private final boolean isReady;

    private final Skill skill;

    public SkillApiMechanic(MythicLineConfig mlc) {
        super(mlc.getLine(), mlc);
        String skillName = config.getString(new String[]{"name", "n"}, "");
        this.level = config.getInteger(new String[] {"level", "lv", "l"}, 1);

        this.skill = SkillAPI.getSkill(skillName);
        this.isReady = this.skill != null;
    }


    @Override
    public boolean castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if (!isReady) {
            return false;
        }

        LivingEntity caster = (LivingEntity) BukkitAdapter.adapt(skillMetadata.getCaster().getEntity());

        if (skill instanceof SkillShot) {
            try {
                return ((SkillShot) skill).cast(caster, level);
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "SkillShot에서 예외 발생", e);
            }
        } else if (skill instanceof TargetSkill) {
            LivingEntity target = (LivingEntity) BukkitAdapter.adapt(abstractEntity);

            if (target == null) {
                return false;
            }
            try {
                return ((TargetSkill) skill).cast(caster, target, level, !SkillAPI.getSettings().canAttack(caster, target));
            } catch (Exception e) {
                Bukkit.getLogger().log(Level.WARNING, "TargetSkill에서 예외 발생", e);
            }
        }

        return false;
    }
}
