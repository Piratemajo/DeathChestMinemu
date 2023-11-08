package es.minemu.minemu;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

public class Minemu extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Registra el evento de muerte del jugador
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Obtén la ubicación donde el jugador murió
        Location deathLocation = player.getLocation();

        // Poner las coords en el chat de donde ha muerto el jugador
        player.sendMessage(ChatColor.RED +"Has muerto en X: " + deathLocation.getBlockX() + " Y: " + deathLocation.getBlockY() + " Z: " + deathLocation.getBlockZ());


        // Crea un cofre en la ubicación de la muerte
        Block chestBlock = deathLocation.getBlock();
        chestBlock.setType(Material.CHEST);

        // Verifica que el bloque sea un cofre
        if (chestBlock.getState() instanceof Chest) {
            Chest chest = (Chest) chestBlock.getState();

            // No sueltes los objetos del jugador al morir
            event.getDrops().clear();

            // Guarda los objetos del jugador en el cofre
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null) {
                    chest.getInventory().addItem(item);
                }
            }
        }

        // Limpia el inventario del jugador
        player.getInventory().clear();
    }
}
