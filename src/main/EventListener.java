package main;

import cn.nukkit.Player;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
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
    public void playerQuit(PlayerQuitEvent e){
        this.plugin.getGwServer().removePlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerRespawn(PlayerRespawnEvent e) {
        PlayerContainer pc = this.plugin.getGwServer().getPlayerContainerByPlayer(e.getPlayer());
        if(!pc.getPlayer().isAlive()) {
            pc.spawn();
        }
    }
   /* @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void pvpDamage(EntityDamageEvent e) throws SQLException {
        this.plugin.getLogger().info(e.getEventName());
        this.plugin.getLogger().info(e.getEntity().getName());
        this.plugin.getLogger().info("Took Damage From:");
        this.plugin.getLogger().info(e.getCause().name());
        this.plugin.getLogger().info("------------------");

        //PlayerContainer pc = this.plugin.getGwServer().getPlayerContainerByPlayer(e.getPlayer());
    }*/
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void pvpDamage(EntityDamageByEntityEvent e) throws SQLException {
        PlayerContainer pc = this.plugin.getGwServer().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(e.getDamager().getName()));
        this.plugin.db.updatePvpDamage(e.getFinalDamage(), pc, this.plugin);
        PlayerContainer pc0 = this.plugin.getGwServer().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(e.getEntity().getName()));
        this.plugin.db.updatePvpDamageTaken(e.getFinalDamage(), pc0, this.plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerDeath(PlayerDeathEvent e) throws SQLException {
        if(e.getEntity().getLastDamageCause().getEventName().equals("cn.nukkit.event.entity.EntityDamageByEntityEvent")){
            EntityDamageEvent lastDamage = e.getEntity().getLastDamageCause();
            PlayerContainer pc = this.plugin.getGwServer().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(lastDamage.getEntity().getName()));
            this.plugin.db.addMurdered(pc, this.plugin);
            pc = this.plugin.getGwServer().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(pc.getPlayer().getKiller().getName()));
            this.plugin.db.addPvpKill(pc, this.plugin);
        }
    }
}
