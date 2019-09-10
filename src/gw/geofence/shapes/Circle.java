package gw.geofence.shapes;
import cn.nukkit.math.Vector2;

import cn.nukkit.math.Vector3;
import gw.geofence.Geofence;
import gw.player.PlayerContainer;
import java.util.HashMap;

public class Circle {
    public static boolean test(HashMap<String, String> meta, PlayerContainer pc, Geofence gc){
        String[] ps = meta.get("origin").split(",");
        Vector2 p = new Vector2(Double.parseDouble(ps[0]), Double.parseDouble(ps[2]));
        Vector2 pcp = new Vector2(pc.getPlayer().getX(), pc.getPlayer().getZ());
        Double radius = Double.parseDouble( meta.get("radius") );
        pc.getParent().getLogger().info("origin:"+p.toString());
        pc.getParent().getLogger().info("radius:"+radius.toString());
        pc.getParent().getLogger().info("distance:"+p.distance(pcp));
        if(p.distance(pcp) <= radius){
            return true;
        }
        return false;
    }
}
