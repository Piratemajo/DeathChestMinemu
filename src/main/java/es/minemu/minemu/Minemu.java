package es.minemu.minemu;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Minemu extends JavaPlugin implements Listener {

    private FileConfiguration dataConfig;
    private File dataFile;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        // Carga o crea el archivo de configuración
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            saveResource("data.yml", false);
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();

        deathLocation.getBlock().setType(Material.CHEST);
        Chest chest = (Chest) deathLocation.getBlock().getState();
        Location chestLocation = chest.getLocation();

        dataConfig.set(chestLocation.toString(), player.getName());

        saveDataConfig();

        event.getDrops().clear();

        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                chest.getInventory().addItem(item);
            }
        }

        player.getInventory().clear();


        player.sendMessage(ChatColor.YELLOW + "Tu inventario ha sido guardado en un cofre en tu ubicación de muerte. " +
                "Coordenadas: X=" + deathLocation.getBlockX() + ", Y=" + deathLocation.getBlockY() + ", Z=" + deathLocation.getBlockZ());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.CHEST) {
            Location chestLocation = event.getBlock().getLocation();


            dataConfig.set(chestLocation.toString(), null);
            saveDataConfig();
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().toString().contains("RIGHT_CLICK") && event.getClickedBlock() != null) {
            if (event.getClickedBlock().getType() == Material.CHEST) {
                Location chestLocation = event.getClickedBlock().getLocation();
                Player player = event.getPlayer();


                if (dataConfig.contains(chestLocation.toString()) && dataConfig.getString(chestLocation.toString()).equals(player.getName())) {
                    // Elimina la información del cofre del archivo YAML
                    dataConfig.set(chestLocation.toString(), null);
                    saveDataConfig();

         
                    Chest chest = (Chest) event.getClickedBlock().getState();
                    Location dropLocation = chestLocation.clone().add(0.5, 1.0, 0.5);
                    for (ItemStack item : chest.getInventory().getContents()) {
                        if (item != null) {
                            event.getClickedBlock().getWorld().dropItemNaturally(dropLocation, item);
                        }
                    }

   
                    event.getClickedBlock().setType(Material.AIR);


                    player.sendMessage("Has recuperado tus objetos del cofre en tu ubicación de muerte. ");
                }
            }
        }
    }

    private void saveDataConfig() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
