package com.jungwuk.season4assistance;

import com.jungwuk.season4assistance.listeners.BeautyQuestListener;
import com.jungwuk.season4assistance.listeners.GeneralListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Season4Assistance extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        this.getServer().getPluginManager().registerEvents(new BeautyQuestListener(),this);
    }

    @Override
    public void onDisable() {
    }
}
