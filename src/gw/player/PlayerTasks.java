package gw.player;

import cn.nukkit.scheduler.PluginTask;
import gw.Core;


public class PlayerTasks extends PluginTask<Core> {
    private Core plugin;

    public PlayerTasks(Core owner) {
        super(owner);
    }
    @Override
    public void onRun(int i) {
        if(super.getOwner().enabled) {
            for (PlayerContainer _pc : super.getOwner().getEngine().getPlayersOnline()) {

                super.getOwner().getGates().run(_pc.getPlayer());

                //CHECK SCORE For Last SYNC to Database.
                if(_pc.checkScoreSync()){
                    _pc.syncScore();
                }
            }
        }
    }
}


