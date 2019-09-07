package main.gwserver;
import cn.nukkit.Player;
import cn.nukkit.Server;

import main.MainClass;
import main.player.PlayerContainer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class gwServer {
    private List<PlayerContainer> playersOnline = new ArrayList<>();
    private MainClass core;

    public gwServer( MainClass parent) throws SQLException {
        parent.getLogger().info("Gateworld gwServer Loading!");
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

    public void addPlayer(Player p) throws SQLException {
        List<String> pData = core.db.checkForPlayer(p, core);
        if(pData.size()==0) {
            boolean playerAdded = core.db.createNewPlayer(p, core);
            core.getLogger().info("Player Added: " + String.valueOf(playerAdded));
            if (playerAdded) {
                pData = core.db.checkForPlayer(p, core);
            }
        }
        core.getLogger().info("Adding Player to gwServer.");
        PlayerContainer pc = new PlayerContainer(pData, p, core);
        getPlayersOnline().add(pc);
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
            playersOnline.remove(pc);
            core.getLogger().info("Player Removed from gwServer.");
        }
    }

    private void reloadOnlinePlayers() throws SQLException {
        core.getLogger().info("Refreshing gwServer");
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            addPlayer(player);
        }
        core.getLogger().info("Gateworld Server Refreshed!");
        core.getLogger().info(String.valueOf(this.playersOnlineCount())+"- Players Online!");
    }
}
