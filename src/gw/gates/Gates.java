package gw.gates;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.HappyVillagerParticle;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import gw.Core;
import jdk.nashorn.internal.ir.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Gates{
    private static Config config;
    private Core plugin;

    private Double time = 0.0;
    private Map<String, Integer> throttleMap;

    public Gates(Config c, Core parent) {
        plugin = parent;
        config = c;
    }
    public void run(Player p) {
        if (config.getSections("portals").size() > 0) {
            config.getSections("portals").forEach((s, o) -> {
                ConfigSection c = (ConfigSection) o;
                Level l = p.getLevel();
                time+=0.02;
                if (!l.getName().equals(c.getString("world"))) return;
                //spawnSparkles(c, l, s);
                if(
                        p.x >= c.getInt("x") &&
                        p.x <= c.getInt("x") + (c.getInt("width")) &&
                        p.z >= c.getInt("z") &&
                        p.z <= c.getInt("z") + (c.getInt("depth")) &&
                        p.y >= c.getDouble("y") &&
                        p.y <= c.getDouble("y") + (c.getDouble("height"))
                        ){
                            if (config.getBoolean("resetPosition")) {
                                p.teleport(p.getLevel().getSafeSpawn());
                            }
                            Server.getInstance().dispatchCommand(p, c.getString("command"));
                        }
            });
        }
    }



    public void spawnSparkles(ConfigSection c, Level l, String gate){
       /*  for(int x=c.getInt("x"); x<=c.getInt("x")+(c.getInt("width")); x++){
            for(int z=c.getInt("z"); z<=c.getInt("z")+(c.getInt("depth")); z++){
                    l.addParticle(
                            new HappyVillagerParticle(new Vector3(x + 0.5, c.getDouble("y") + 0.1, z + 0.5))
                    );

            }
        }*/

        Double x = (c.getInt("x")+c.getInt("width")*0.5)+((Math.sin(time)*c.getInt("width")*0.5));
        Double z = (c.getInt("z")+c.getInt("depth")*0.5)+((Math.cos(time)*c.getInt("depth")*0.5));
        Vector3 p = new Vector3(x, c.getInt("y"), z);
        l.addParticle(
                new HappyVillagerParticle(p)
        );
    }



}