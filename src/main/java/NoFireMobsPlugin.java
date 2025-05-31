package com.example.nofiremobs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NoFireMobsPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.getIgnitingEntity() != null
                && !(event.getIgnitingEntity() instanceof Player)) {
            // プレイヤー以外のエンティティによる着火を防止
            event.setCancelled(true);
        } else if (event.getCause() == BlockIgniteEvent.IgniteCause.SPREAD
                || event.getCause() == BlockIgniteEvent.IgniteCause.LAVA
                || event.getCause() == BlockIgniteEvent.IgniteCause.LIGHTNING) {
            // 炎の自然な延焼・溶岩・雷による着火を防止
            event.setCancelled(true);
        }
    }
}
