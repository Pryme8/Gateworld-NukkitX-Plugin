package gw.engine;
import cn.nukkit.Player;

import gw.Core;
import gw.guild.Guild;
import gw.player.PlayerContainer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Engine {

    private List<PlayerContainer> playersOnline = new ArrayList<PlayerContainer>();
    private List<Guild> guilds = new ArrayList<Guild>();
    private Core core;

    public Engine(Core parent) throws SQLException {
        parent.getLogger().info("Gateworld Engine Loading!");
        core = parent;
        reloadOnlinePlayers();
    }

    public PlayerContainer getPlayerContainerByPlayer(Player p){
        for(PlayerContainer pc : getPlayersOnline()){
           if(pc.getPlayer().getUniqueId().equals(p.getUniqueId())) {
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

    private void reloadOnlinePlayers() throws SQLException {
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
}
