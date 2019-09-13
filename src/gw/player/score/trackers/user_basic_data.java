package gw.player.score.trackers;

import cn.nukkit.level.Position;
import cn.nukkit.utils.Hash;
import javafx.geometry.Pos;

import java.util.HashMap;

public class user_basic_data{

    public double totalDistance;
    public String lastKnownPosition;
    public String lastKnownDirection;

    private HashMap map = new HashMap<String, String>();

    public user_basic_data(){
        totalDistance = 0;
        lastKnownPosition = "0:0:0";
        lastKnownDirection = "0:0:0";
        map.put("totalDistance", "add");
        map.put("lastKnownPosition", "set");
        map.put("lastKnownDirection", "set");

    }
    /*--------------------*/
    /*----- METHODS ------*/
    /*--------------------*/

    /*--------------------*/
    /*----- GETTERS ------*/
    /*--------------------*/
    public double getTotalDistance() {
        return totalDistance;
    }
    public String getLastKnownPosition(){return lastKnownPosition;}
    public String getLastKnownDirection() {return lastKnownDirection;}

    public HashMap getMap() {
        return map;
    }
    /*--------------------*/
    /*----- SETTERS ------*/
    /*--------------------*/
    public void addTotalDistance(double dist) {
        this.totalDistance += dist;
    }
    public void setLastKnownPosition(String p){
        lastKnownPosition = p;
    }
    public void setLastKnownDirection(String p){
        lastKnownDirection = p;
    }
}