package gw.geofence.shapes;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector2;

import cn.nukkit.math.Vector3;
import gw.geofence.Geofence;
import gw.player.PlayerContainer;
import java.util.HashMap;

public class Circle {
    //Player Position
    public static boolean test(HashMap<String, String> meta, PlayerContainer pc, Geofence gc){
        String[] ps = meta.get("origin").split(",");
        Vector2 p = new Vector2(Double.parseDouble(ps[0]), Double.parseDouble(ps[2]));
        Vector2 pcp = new Vector2(pc.getPlayer().getX(), pc.getPlayer().getZ());
        Double radius = Double.parseDouble( meta.get("radius") );
        if(p.distance(pcp) <= radius){
            return true;
        }
        return false;
    }
    //Physical Position
    public static boolean test(HashMap<String, String> meta, Vector3 _pos, Geofence gc){
        String[] ps = meta.get("origin").split(",");
        Vector2 p = new Vector2(Double.parseDouble(ps[0]), Double.parseDouble(ps[2]));
        Vector2 pcp = new Vector2(_pos.getX(), _pos.getZ());
        Double radius = Double.parseDouble( meta.get("radius") );
        if(p.distance(pcp) <= radius){
            return true;
        }
        return false;
    }
}
