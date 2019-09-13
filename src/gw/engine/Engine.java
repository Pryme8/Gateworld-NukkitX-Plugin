package gw.engine;
import cn.nukkit.Player;

import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import gw.Core;
import gw.geofence.Geofence;
import gw.guild.Guild;
import gw.player.PlayerContainer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Engine {

    private List<PlayerContainer> playersOnline = new ArrayList<PlayerContainer>();
    private List<Guild> guilds = new ArrayList<Guild>();
    private Core core;

    public Engine(Core parent){
        parent.getLogger().info("Gateworld Engine Loading!");
        core = parent;
    }

    public PlayerContainer getPlayerContainerByPlayer(Player _p){
        for(PlayerContainer pc : getPlayersOnline()){
           if(pc.getPlayer().getUniqueId().equals(_p.getUniqueId())) {
               return pc;
           }
        }
        return null;
    }

    public PlayerContainer getPlayerContainerByName(String _name){
        for(PlayerContainer pc : getPlayersOnline()){
            if(pc.getPlayer().getName().equals(_name)) {
                return pc;
            }
        }
        return null;
    }

    public PlayerContainer getPlayerContainerByID(Integer _id){
        for(PlayerContainer pc : getPlayersOnline()){
            if(pc.getId().equals(_id)) {
                return pc;
            }
        }
        return null;
    }



    public List<PlayerContainer> getPlayersOnline(){
        return playersOnline;
    }
    public Integer playersOnlineCount(){
        return playersOnline.size();
    }

    public PlayerContainer addPlayer(Player p) throws SQLException {
        core.getLogger().info("Adding Player to Engine.");
        PlayerContainer pc = new PlayerContainer(p, core);
        getPlayersOnline().add(pc);
        return pc;
    }

    public void removePlayer(Player p){
        PlayerContainer pc = null;
        for(PlayerContainer _pc : getPlayersOnline()){
            if(_pc.getPlayer().getUniqueId() == p.getUniqueId()){
                pc = _pc;
                break;
            }
        }
        if(pc != null) {
            pc.syncScore();
            playersOnline.remove(pc);
            core.getLogger().info("Player Removed from gwServer.");
        }
    }

    public void reloadOnlinePlayers() throws SQLException {
        core.getLogger().info("Refreshing gwServer");
        for (Player player : cn.nukkit.Server.getInstance().getOnlinePlayers().values()) {
            addPlayer(player);
        }
        core.getLogger().info("Gateworld Server Refreshed!");
        core.getLogger().info(String.valueOf(this.playersOnlineCount())+"- Players Online!");
    }

    public Guild addGuild(Integer gid) throws SQLException {
        core.getLogger().info("Adding Guild to Engine.");
        Guild g = new Guild(gid, core);
        core.getLogger().info(g.toString());
        getGuilds().add(g);
        return g;
    }

    public List<Guild> getGuilds() {
        return guilds;
    }

    public Guild getGuildByID(Integer id) {
        for(Guild g:getGuilds()){
            if(g.getId() == id){
                return g;
            }
        }
        return null;
    }

    public boolean testEventZoneNegate(Level _l, Vector3 _pos, String _type) throws SQLException {
        for(Geofence gf : getZonesOfInfluence(_l)){
            core.getLogger().info("Testing Geofence");
        }
        return false;
    }

    public List<Geofence> getZonesOfInfluence(Level _l) throws SQLException {
        List<HashMap> igf = Guild.getInfluencingGeofences(_l, core);
        core.getLogger().info("Grabbing Geofences");
        List<Geofence> r = new ArrayList<>();
        if(igf.size()>0) {
            for (HashMap<String, String> gfd : igf) {
                core.getLogger().info("gfd_meta:"+gfd.get("meta"));
                Geofence gf = new Geofence(Integer.parseInt(gfd.get("id")), Integer.parseInt(gfd.get("guild")), gfd.get("meta"), core);
                r.add(gf);
            }
        }
        return r;
    }
}
