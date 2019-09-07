package main.player;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import com.mysql.fabric.xmlrpc.base.Array;
import main.MainClass;

import java.sql.SQLException;
import java.util.List;

public class PlayerContainer {
     private Integer id;
     private String name;
     private String classType;
     private String currentZone;
     private String currentSpawn;
     private Player player;
     private MainClass plugin;
     private String lastPvpDamager;

     public PlayerContainer(List<String> pData, Player p, MainClass parent){
         plugin = parent;
         id = Integer.parseInt(pData.get(0), 10);
         name = pData.get(1);
         classType = pData.get(2);
         currentZone = pData.get(3);
         currentSpawn = pData.get(4);
         player = p;
         lastPvpDamager = "None";
         onCreation();
     }

     public Integer getId(){
         return id;
     }

    public String getName(){
        return name;
    }

    public String getClassType(){
        return classType;
    }

    public Player getPlayer(){
        return player;
    }

    public void onCreation(){
        //Correct Loaded Level?
        Level level = getPlayer().getLevel();
        plugin.getLogger().info("Current Level :"+ level.getName());

        if(!currentZone.equals(player.getLevel().getName())){
            setZone(currentZone);
            spawn();
        }

        if((classType.equals("none") || currentZone.equals("none"))){
            setZone("StartZone");
            setSpawn();
            try {
                plugin.db.updatePlayerClass("none", this, plugin);
                this.classType = "none";
            } catch (SQLException e) {
                e.printStackTrace();
            }

            getPlayer().getInventory().clearAll();

            String[] sWarp = (String.valueOf(plugin.getConfig().get("class-reset-warp-end")).split(" "));
            Vector3 warpPos = new Vector3(Double.valueOf(sWarp[0]), Double.valueOf(sWarp[1]), Double.valueOf(sWarp[2]));
            getPlayer().teleport(warpPos);
            getPlayer().getSpawn();
        }
    }

    public void setZone(String zone){
        Server server = Server.getInstance();
        server.dispatchCommand( server.getConsoleSender(), "tell "+ getName() +" 'Warping!.'");
        currentZone = zone;
        try {
            plugin.db.updatePlayerZone(zone, this, plugin);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        server.dispatchCommand( server.getConsoleSender(), "mw teleport \""+zone+"\" "+getName());
        setSpawn();
    }

    public void setSpawn(){
         String zone = getPlayer().getLevel().getName();
         Vector3 pos = getPlayer().getPosition();
         getPlayer().setSpawn(pos);
        try {
            plugin.db.updatePlayerSpawn(zone+":"+pos.x+","+pos.y+","+pos.z, this, plugin);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void spawn(){
        if(getPlayer().isAlive()){
            getPlayer().getSpawn();
            return;
        }
        Server server = Server.getInstance();
        String[] spawnString = currentSpawn.split(":");
        server.dispatchCommand( server.getConsoleSender(), "mw teleport \""+spawnString[0]+"\" "+getName());
        String[] sWarp = spawnString[1].split(",");
        Vector3 warpPos = new Vector3(Double.valueOf(sWarp[0]), Double.valueOf(sWarp[1]), Double.valueOf(sWarp[2]));
        getPlayer().teleport(warpPos);
        getPlayer().getSpawn();
    }

}
