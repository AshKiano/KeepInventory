package com.ashkiano.keepinventory;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class KeepInventory extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Register the event listener
        this.getServer().getPluginManager().registerEvents(this, this);

        Metrics metrics = new Metrics(this, 21327);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Check if the player has the "keepinventory.keep" permission
        if (player.hasPermission("keepinventory.keep")) {
            // Prevent inventory from dropping
            event.setKeepInventory(true);
            // Prevent experience loss
            event.setKeepLevel(true);
            // Set experience drop to 0
            event.setDroppedExp(0);
            // Clear items that would have dropped
            event.getDrops().clear();
        }
    }
}
