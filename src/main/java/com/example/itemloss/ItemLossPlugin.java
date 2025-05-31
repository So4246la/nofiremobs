package com.example.itemloss;

import org.bukkit.plugin.java.JavaPlugin;

public class ItemLossPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        DeathListener deathListener = new DeathListener();
        getServer().getPluginManager().registerEvents(deathListener, this);
    }
}
