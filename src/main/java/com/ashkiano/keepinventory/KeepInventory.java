package com.ashkiano.keepinventory;

import com.ashkiano.ashlib.PluginStatistics;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class KeepInventory extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        //TODO tuto chybu vypisovat i OP hráčům do chatu
        if (!isAshLibPresent()) {
            getLogger().severe("AshLib plugin is missing! Please download and install AshLib to run KeepInventory. (can be downloaded from: https://www.spigotmc.org/resources/ashlib.118282/ )");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new PluginStatistics(this);
        
        // Register the event listener
        this.getServer().getPluginManager().registerEvents(this, this);

        Metrics metrics = new Metrics(this, 21327);

        this.getLogger().info("Thank you for using the KeepInventory plugin! If you enjoy using this plugin, please consider making a donation to support the development. You can donate at: https://donate.ashkiano.com");

        checkForUpdates();
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

    private void checkForUpdates() {
        try {
            String pluginName = this.getDescription().getName();
            URL url = new URL("https://plugins.ashkiano.com/version_check.php?plugin=" + pluginName);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                JSONObject jsonObject = new JSONObject(jsonResponse);
                if (jsonObject.has("error")) {
                    this.getLogger().warning("Error when checking for updates: " + jsonObject.getString("error"));
                } else {
                    String latestVersion = jsonObject.getString("latest_version");

                    String currentVersion = this.getDescription().getVersion();
                    if (currentVersion.equals(latestVersion)) {
                        this.getLogger().info("This plugin is up to date!");
                    } else {
                        this.getLogger().warning("There is a newer version (" + latestVersion + ") available! Please update!");
                    }
                }
            } else {
                this.getLogger().warning("Failed to check for updates. Response code: " + responseCode);
            }
        } catch (Exception e) {
            this.getLogger().warning("Failed to check for updates. Error: " + e.getMessage());
        }
    }

    private boolean isAshLibPresent() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("AshLib");
        return plugin != null && plugin.isEnabled();
    }
}
