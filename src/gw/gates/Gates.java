package gw.gates;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import gw.Core;

public class Gates{
    private static Config config;
    private Core plugin;

    public Gates(Config c, Core parent) {
        plugin = parent;
        config = c;
    }
    public void run(Player p) {
        if (config.getSections("portals").size() > 0) {
            config.getSections("portals").forEach((s, o) -> {
                ConfigSection c = (ConfigSection) o;
                if (!p.getLevel().getName().equals(c.getString("world"))) return;
                       /* this.plugin.getLogger().info(s+"-----");
                        this.plugin.getLogger().info("pxx:"+String.valueOf(Math.round(p.x * 2) / 2.0));
                        this.plugin.getLogger().info("pzx:"+String.valueOf(Math.floor(p.z)));
                        this.plugin.getLogger().info("pyx:"+String.valueOf(p.y));
                        this.plugin.getLogger().info("-------");*/
                        if(
                        p.x > c.getInt("x") &&
                        p.x <= c.getInt("x") + (c.getInt("width")-1) &&
                        p.z >= c.getInt("z") &&
                        p.z <= c.getInt("z") + (c.getInt("depth")-1) &&
                        p.y >= c.getDouble("y") &&
                        p.y <= c.getDouble("y") + (c.getDouble("height")-1)
                        ){
                            if (config.getBoolean("resetPosition")) {
                                p.teleport(p.getLevel().getSafeSpawn());
                            }
                            Server.getInstance().dispatchCommand(p, c.getString("command"));
                        }

            });
        }
    }
}