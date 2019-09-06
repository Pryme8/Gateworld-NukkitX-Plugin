package main;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.*;
import main.player.PlayerContainer;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;


public class EventListener implements Listener {
    private MainClass plugin;

    public EventListener(MainClass plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        String pName = p.getName();
        plugin.getLogger().info("Player "+pName+":"+uuid+" Joined!");
        if(this.plugin.enabled){
            this.plugin.getGwServer().addPlayer( p );
        }

    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerQuit(PlayerQuitEvent e) throws SQLException {
        this.plugin.getGwServer().removePlayer(e.getPlayer());
    }
}
