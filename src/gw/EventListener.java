package gw;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.scheduler.NukkitRunnable;
import gw.engine.Engine;
import gw.geofence.Geofence;
import gw.player.PlayerContainer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventListener implements Listener {
    private Core plugin;
    private Engine engine;

    public EventListener(Core _plugin, Engine _engine) {
        this.plugin = _plugin;
        this.engine = _engine;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void blockBreak(BlockBreakEvent e){

        PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByName(e.getPlayer().getName());
        plugin.getLogger().info("Checking Block Place");
        if(pc.checkZoneNegate("building", new Vector3(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))){
            e.getPlayer().sendMessage("This is protected by "+engine.getGuildByID(pc.getLastGateofInfluence().getGuildID()).getName());
            e.setCancelled();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void blockPlace(BlockPlaceEvent e){

        PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByName(e.getPlayer().getName());
        plugin.getLogger().info("Checking Block Place");
        if(pc.checkZoneNegate("building", new Vector3(e.getBlock().getX(), e.getBlock().getY(), e.getBlock().getZ()))){
            e.getPlayer().sendMessage("This is protected by "+engine.getGuildByID(pc.getLastGateofInfluence().getGuildID()).getName());
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

    PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByName(e.getPlayer().getName());
       if(pc != null){
           if(e.isFirstSpawn()){
               pc.setZone("StartZone");
           }else{
               e.getPlayer().getSpawn();
               pc.spawn();
           }
       }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void pvpDamage(EntityDamageByEntityEvent e){
        if (e.isCancelled()) return;

        PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByName(e.getEntity().getName());

    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerDeath(PlayerDeathEvent e){

        if(e.getEntity().getLastDamageCause().getEventName().equals("cn.nukkit.event.entity.EntityDamageByEntityEvent")){
            EntityDamageEvent lastDamage = e.getEntity().getLastDamageCause();
            PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByName(lastDamage.getEntity().getName());
            pc.getScore().getUser_pvp_data().addMurderedCount();
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = false) //DON'T FORGET THE ANNOTATION @EventHandler
    public void playerMove(PlayerMoveEvent e){
        double dist = (e.getFrom().subtract(e.getTo()).length());
        PlayerContainer pc = this.plugin.getEngine().getPlayerContainerByName(e.getPlayer().getName());
        pc.getScore().getUser_basic_data().addTotalDistance(dist);
        Vector3 dir = pc.getPlayer().getDirectionVector();
        Vector3 _pos = pc.getPlayer().getPosition();
        pc.getScore().getUser_basic_data().setLastKnownDirection(String.format("%.2f", dir.x)+":"+String.format("%.2f", dir.y)+":"+String.format("%.2f", dir.z));
        pc.getScore().getUser_basic_data().setLastKnownPosition(String.format("%.2f", _pos.x)+":"+String.format("%.2f", _pos.y)+":"+String.format("%.2f", _pos.z));
    }




    //SPLOZIONS
    //https://github.com/Nukkit-coders/Factions-For-Nukkit/blob/master/src/com/massivecraft/factions/listeners/FactionsEntityListener.java
    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityExplode(EntityExplodeEvent e) throws SQLException {
        if (e.isCancelled()) return;
        Position loc = e.getPosition();
        List<Block> splodeBlocks = new ArrayList<>();
        for(Geofence gfi : engine.getZonesOfInfluence(loc.getLevel())){
            for(Block b :e.getBlockList()){
                Vector3 _pos = new Vector3(b.getX(), b.getY(), b.getZ());
                if(!gfi.test(_pos)){
                    splodeBlocks.add(b);
                }
            }
        }

        e.setBlockList(splodeBlocks);


      //  Entity boomer = e.getEntity();
      //  engine.testEventZoneNegate(loc.getLevel(), new Vector3(loc.x, loc.y, loc.z), "splode");




/*

        if (faction.noExplosionsInTerritory()) {
            // faction is peaceful and has explosions set to disabled
            event.setCancelled(true);
            return;
        }

        boolean online = faction.hasPlayersOnline();

        if ((boomer instanceof EntityPrimedTNT ) && ((faction.isNone() && Conf.wildernessBlockTNT
                && !Conf.worldsNoWildernessProtection.contains(loc.getLevel().getName()))
                || (faction.isNormal() && (online ? Conf.territoryBlockTNT : Conf.territoryBlockTNTWhenOffline))
                || (faction.isWarZone() && Conf.warZoneBlockTNT) || (faction.isSafeZone() && Conf.safeZoneBlockTNT))) {
            // TNT which needs prevention
            event.setCancelled(true);
        } else if ((boomer instanceof EntityPrimedTNT ) && Conf.handleExploitTNTWaterlog) {
            // TNT in water/lava doesn't normally destroy any surrounding
            // blocks, which is usually desired behavior, but...
            // this change below provides workaround for waterwalling providing
            // perfect protection,
            // and makes cheap (non-obsidian) TNT cannons require minor
            // maintenance between shots
            Block center = loc.getLevel().getBlock(loc);
            if (isLiquid(center)) {
                // a single surrounding block in all 6 directions is broken if
                // the material is weak enough
                List<Block> targets = new ArrayList<Block>();
                targets.add(center.getLevel().getBlock(center.add(0, 0, 1)));
                targets.add(center.getLevel().getBlock(center.add(0, 0, -1)));
                targets.add(center.getLevel().getBlock(center.add(0, 1, 0)));
                targets.add(center.getLevel().getBlock(center.add(0, -1, 0)));
                targets.add(center.getLevel().getBlock(center.add(1, 0, 0)));
                targets.add(center.getLevel().getBlock(center.add(-1, 0, 0)));
                for (Block target : targets) {
                    int id = target.getId();
                    // ignore air, bedrock, water, lava, obsidian, enchanting
                    // table, etc.... too bad we can't get a blast resistance
                    // value through Bukkit yet
                    if (id != 0 && (id < 7 || id > 11) && id != 49 && id != 90 && id != 116 && id != 119 && id != 120
                            && id != 130)
                        // target.breakNaturally();
                        target.getLevel().setBlockIdAt(target.getFloorX(), target.getFloorY(), target.getFloorZ(),
                                Block.AIR);

                }
            }
        }*/
    }
}
