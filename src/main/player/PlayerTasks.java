package main.player;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;
import main.MainClass;

public class PlayerTasks extends PluginTask<MainClass> {
    private MainClass plugin;

    public PlayerTasks(MainClass owner) {
        super(owner);
    }
    @Override
    public void onRun(int i) {
        if(super.getOwner().enabled) {
            for (PlayerContainer _pc : super.getOwner().getGwServer().getPlayersOnline()) {
                super.getOwner().getGates().run(_pc.getPlayer());
            }
        }
    }
}


