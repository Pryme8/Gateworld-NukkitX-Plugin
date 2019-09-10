package gw;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.*;
import gw.player.PlayerContainer;

import java.sql.SQLException;

public class EventListener implements Listener {
    private Core plugin;

    public EventListener(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void blockBreak(BlockBreakEvent e){
        PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(e.getPlayer().getName()));
        if(pc.checkZoneNegate()){
            e.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void blockPlace(BlockPlaceEvent e){
        PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(e.getPlayer().getName()));
        plugin.getLogger().info("Checking Block Place");
        if(pc.checkZoneNegate()){
            plugin.getLogger().info("Negate");
            e.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerJoin(PlayerJoinEvent e) throws SQLException {
        Player p = e.getPlayer();
        plugin.getLogger().info("Player "+ p.getName()+":"+p.getUniqueId()+" Joined!");
            if(!e.getPlayer().playedBefore){
                plugin.getLogger().info("Player Has not Played before");
                (this.plugin.getEngine().addPlayer( p )).setZone("StartZone");
            }else{
                plugin.getLogger().info("Player Has Played before");
                this.plugin.getEngine().addPlayer( p );
            }
        plugin.getLogger().info("playerJoin Complete.");
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerQuit(PlayerQuitEvent e){
        this.plugin.getEngine().removePlayer(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerRespawn(PlayerRespawnEvent e) {
      /* PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(e.getPlayer().getName()));
       if(pc != null){
           if(e.isFirstSpawn()){
               pc.setZone("StartZone");
           }else{
               e.getPlayer().getSpawn();
               pc.spawn();
           }
       }else{
           e.getPlayer().kill();
       }*/
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void pvpDamage(EntityDamageByEntityEvent e){
        PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(e.getEntity().getName()));

    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerDeath(PlayerDeathEvent e){
        if(e.getEntity().getLastDamageCause().getEventName().equals("cn.nukkit.event.entity.EntityDamageByEntityEvent")){
            EntityDamageEvent lastDamage = e.getEntity().getLastDamageCause();
            PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(lastDamage.getEntity().getName()));
            pc.getScore().getUser_pvp_data().addMurderedCount();
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerMove(PlayerMoveEvent e){
        double dist = (e.getFrom().subtract(e.getTo()).length());
        PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByPlayer(this.plugin.getServer().getPlayer(e.getPlayer().getName()));
        pc.getScore().getUser_basic_data().addTotalDistance(dist);
    }
}
