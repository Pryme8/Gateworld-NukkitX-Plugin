package gw.geofence;


import cn.nukkit.math.Vector3;
import gw.Core;
import gw.geofence.shapes.Circle;
import gw.geofence.shapes.EntireZone;
import gw.player.PlayerContainer;
import java.util.HashMap;

public class Geofence{
    private Integer id;
    private PlayerContainer pc;
    private Core parent;
    private String levelName;
    private Integer guildID;
    private HashMap<String, String> meta;

    public Geofence (Integer _id, Integer _guild, String _meta, PlayerContainer _pc){
        id = _id;
        pc = _pc;
        parent = pc.getParent();
        parent.getLogger().info(_meta);
        guildID =_guild;
        meta = parseMeta(_meta);;
    }

    public Geofence (Integer _id, Integer _guild, String _meta, Core _parent){
        id = _id;
        pc = null;
        parent = _parent;
        parent.getLogger().info(_meta);
        guildID =_guild;
        meta = parseMeta(_meta);;
    }

    /*--------------------*/
    /*----- METHODS ------*/
    /*--------------------*/
    private HashMap<String, String> parseMeta(String s){
        HashMap<String, String> hm = new HashMap<>();
        for(String _s : s.split(";")){
            String[] _ks = _s.split(":");
            hm.put(_ks[0], _ks[1]);
        }
        return hm;
    }

    public boolean test(){
        switch(meta.get("type")){
            case "EntireZone":  return EntireZone.test();
            case "Circle": return Circle.test(meta, pc, this);
        }
        return false;
    }
    public boolean test(Vector3 _pos){
        switch(meta.get("type")){
            case "EntireZone":  return EntireZone.test();
            case "Circle": return Circle.test(meta, _pos, this);
        }
        return false;
    }


    public static Integer getAccessLevel(){

        return 0;
    }


    /*--------------------*/
    /*----- GETTERS ------*/
    /*--------------------*/
    public String getlevelName() {
        return levelName;
    }

    public HashMap<String, String> getMeta() {
        return meta;
    }

    public Integer getGuildID() {
        return guildID;
    }

    /*--------------------*/
    /*----- SETTERS ------*/
    /*--------------------*/
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
}
