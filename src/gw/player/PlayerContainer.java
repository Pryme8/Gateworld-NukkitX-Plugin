package gw.player;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import com.j256.ormlite.stmt.query.In;
import gw.Core;
import gw.geofence.Geofence;
import gw.guild.Guild;
import gw.player.score.Score;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerContainer{
     private Integer id;
     private String name;
     private String classType;
     private String currentZone;
     private String currentSpawn;
     private boolean isReady;
     private Player player;
     private Core parent;
     private Score score;

     private List<Geofence> influecingGeofences = new ArrayList<>();
     private Guild currentGuild;

     public PlayerContainer(Player p, Core _parent){
         parent = _parent;
         isReady = false;
         player = p;
         try {
             init();
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }

    /*--------------------*/
    /*----- METHODS ------*/
    /*--------------------*/
    private void init() throws SQLException {
        score = new Score();
        syncPlayerData();
        checkSpawnLocation();
        updateInfluencingGeofences();
        isReady = true;
    }

    public void syncPlayerData() throws SQLException {
        List<String> pData = parent.db.getPlayerData(player, parent);
        if (pData.size() == 0) {
            boolean playerAdded = parent.db.createNewPlayer(player, parent);
            parent.getLogger().info("Player Added: " + String.valueOf(playerAdded));
            if (playerAdded) {
                pData = parent.db.getPlayerData(player, parent);
            }
        }
        id = Integer.parseInt(pData.get(0));
        name = pData.get(1);
        classType = pData.get(2);
        currentZone = pData.get(3);
        currentSpawn = pData.get(4);
    }

    public void checkSpawnLocation() {
        Level level = player.getLevel();
        if(!currentZone.equals(player.getLevel().getName())) {
            setZone(currentZone);
            spawn();
        }
      if((classType.equals("none") || currentZone.equals("none"))){
            setZone("StartZone");
            setSpawn();
            try {
                parent.db.updatePlayerClass("none", this, parent);
                this.classType = "none";
            } catch (SQLException e) {
                e.printStackTrace();
            }
            getPlayer().getInventory().clearAll();
            String[] sWarp = (String.valueOf(parent.getConfig().get("class-reset-warp-end")).split(" "));
            Vector3 warpPos = new Vector3(Double.valueOf(sWarp[0]), Double.valueOf(sWarp[1]), Double.valueOf(sWarp[2]));
            getPlayer().teleport(warpPos);
            getPlayer().getSpawn();
        }
    }

    public void updateInfluencingGeofences() throws SQLException {
        List<HashMap> igf = Guild.getInfluencingGeofences(this, parent);
        parent.getLogger().info("Building Geofences");
        if(igf.size()>0) {
            for (HashMap<String, String> gfd : igf) {
                parent.getLogger().info(gfd.toString());
                Geofence gf = new Geofence(Integer.parseInt(gfd.get("id")), Integer.parseInt(gfd.get("guild")), gfd.get("meta"), this);
                getInfluencingGeofences().add(gf);
            }
        }
    }

    public boolean checkZoneNegate(){

        parent.getLogger().info("Testing #"+ getInfluencingGeofences().size()+" Geofences");
        for(Geofence gf : getInfluencingGeofences()){
            parent.getLogger().info("Testing Geofence:"+gf.getMeta().toString());
            parent.getLogger().info("HIT:"+String.valueOf(gf.test()));
            if(gf.test()){
                return true;
            }
        }
        return false;
    }

    public void spawn(){
        Server server = Server.getInstance();
        String[] spawnString = currentSpawn.split(":");
        server.dispatchCommand( server.getConsoleSender(), "mw teleport \""+spawnString[0]+"\" "+name);
        String[] sWarp = spawnString[1].split(",");
        Vector3 warpPos = new Vector3(Double.valueOf(sWarp[0]), Double.valueOf(sWarp[1]), Double.valueOf(sWarp[2]));
        player.teleport(warpPos);
        player.getSpawn();
    }

    public boolean checkScoreSync(){
        return score.checkLastSync();
    }

    public void syncScore() {
        parent.db.updateStats(this, parent);
    }

    /*--------------------*/
    /*----- GETTERS ------*/
    /*--------------------*/

     public Integer getId(){
         return id;
     }

    public String getName(){
         return player.getName();
    }

    public String getClassType(){
        return classType;
    }

    public Player getPlayer(){
        return player;
    }

    public String getCurrentSpawn() {
        return currentSpawn;
    }

    public String getCurrentZone() {
        return currentZone;
    }

    public Score getScore() {
        return score;
    }

    public List<Geofence> getInfluencingGeofences() {
        return influecingGeofences;
    }

    public Core getParent() {
        return parent;
    }

    /*--------------------*/
    /*----- SETTERS ------*/
    /*--------------------*/
    private void setName(String n){
        name = n;
    }

    public void setZone(String zone){
        Server server = Server.getInstance();
        server.dispatchCommand( server.getConsoleSender(), "tell "+ getName() +" 'Warping!.'");
        currentZone = zone;
        try {
            parent.db.updatePlayerZone(zone, this, parent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if(!getPlayer().isAlive()){
            getPlayer().getSpawn();
        }
        server.dispatchCommand( server.getConsoleSender(), "mw teleport \""+zone+"\" "+getName());
        setSpawn();
    }

    public void setSpawn(){
         String zone = getPlayer().getLevel().getName();
         Vector3 pos = getPlayer().getPosition();
         getPlayer().setSpawn(pos);
        try {
            parent.db.updatePlayerSpawn(zone+":"+pos.x+","+pos.y+","+pos.z, this, parent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


