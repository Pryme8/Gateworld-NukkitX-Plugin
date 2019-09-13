package gw.player;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import gw.Core;
import gw.geofence.Geofence;
import gw.guild.Guild;
import gw.player.score.Score;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
     private Geofence lastGateofInfluence = null;
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
        syncGuildData();
        isReady = true;
    }

    public void syncPlayerData() throws SQLException {
        List<String> pData = parent.db.getPlayerData(player, parent);
        if (pData.size() == 0) {
            boolean playerAdded = parent.db.createNewPlayer(this, parent);
            parent.getLogger().info("Player Added: " + String.valueOf(playerAdded));
            if (playerAdded) {
                pData = parent.db.getPlayerData(player, parent);
                parent.db.scoreBoardsInit(Integer.parseInt(pData.get(0)), parent);
            }
        }

        id = Integer.parseInt(pData.get(0));
        name = pData.get(1);
        classType = pData.get(2);
        currentZone = pData.get(3);
        currentSpawn = pData.get(4);
    }

    public void syncGuildData() throws SQLException {
        getParent().getLogger().info("Getting Guild Membership!");
        String query = "SELECT * FROM `guild_members` WHERE `userID`="+getId();
        parent.getLogger().info(query);
        Connection con =  getParent().db.currentConnection();
        con.prepareStatement(query);
        Statement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery(query);
        if (rs != null ){
            parent.getLogger().info(rs.toString());
            Guild g;
            while(rs.next()){
                g =  getParent().getEngine().getGuildByID(   Integer.parseInt(rs.getString("gid")));
                g.addMember(
                     this
                );
                parent.getLogger().info("Member Added to Guild");
                String gName = "§l§6"+g.getTag()+" §r§f§o"+getPlayer().getName()+" §r";
                getPlayer().setDisplayName(gName);
                getPlayer().setNameTag(gName);
            }
        }
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
        getInfluencingGeofences().clear();
        setLastGateofInfluence(null);
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

    public boolean checkZoneNegate(String type){
            if(getLastGateofInfluence() != null){
                Boolean test = getLastGateofInfluence().test();
                if(test)  return getAreaAccessLevel(type);
            }
            for(Geofence gf : getInfluencingGeofences()){
                if(gf.test()){
                    setLastGateofInfluence(gf);
                    return getAreaAccessLevel(type);
                }
            }
        return false;
    }

    public boolean checkZoneNegate(String type, Vector3 _pos){
        if(getLastGateofInfluence() != null){
            parent.getLogger().info("TESTING Last Gate Of Influence");
            Boolean test = getLastGateofInfluence().test(_pos);
            if(test)  return getAreaAccessLevel(type);
        }
        for(Geofence gf : getInfluencingGeofences()){
            if(gf.test(_pos)){
                setLastGateofInfluence(gf);
                return getAreaAccessLevel(type);
            }
        }
        return false;
    }

    public boolean getAreaAccessLevel(String type){
        Guild owner = parent.getEngine().getGuildByID(getLastGateofInfluence().getGuildID());
        Integer power = 0;
        switch (type){
            case "building" :
                parent.getLogger().info("Checking Building Rights!");
                if(getCurrentGuild() != null) {
                    if (owner.getId() == getCurrentGuild().getId()) {
                        power = getCurrentGuild().getMemberByID(getId()).getPower();
                        break;
                    }
                }
                power = 0;
                break;
        }
        parent.getLogger().info("Power:"+String.valueOf(power));
        switch (type){
            case "building" :
                switch (owner.getType()){
                    case "realmguard":
                    case "normal":
                        return power < 5;
                }

            return true;
        }
        return true;
    }

    public void spawn(){
        Server server = Server.getInstance();
        String[] spawnString = currentSpawn.split(":");
        server.dispatchCommand( server.getConsoleSender(), "mw teleport \""+spawnString[0]+"\" "+name);
        String[] sWarp = spawnString[1].split(",");
        Vector3 warpPos = new Vector3(Double.valueOf(sWarp[0]), Double.valueOf(sWarp[1]), Double.valueOf(sWarp[2]));
        try {
            updateInfluencingGeofences();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public Geofence getLastGateofInfluence() {
        return lastGateofInfluence;
    }

    public Core getParent() {
        return parent;
    }

    public Guild getCurrentGuild() {
        return currentGuild;
    }

    /*--------------------*/
    /*----- SETTERS ------*/
    /*--------------------*/
    private void setName(String n){
        name = n;
    }

    public void setCurrentGuild(Guild currentGuild) {
        this.currentGuild = currentGuild;
    }

    public void setLastGateofInfluence(Geofence lastGateofInfluence) {
        this.lastGateofInfluence = lastGateofInfluence;
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
        try {
            updateInfluencingGeofences();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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


