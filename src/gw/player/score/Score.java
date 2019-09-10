package gw.player.score;

import gw.player.score.trackers.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;

public class Score {
    private long lastUpdate;
    private long updateInterval = 30000;
    private boolean dirty = false;
    private gw.player.score.trackers.user_pvp_data user_pvp_data;
    private gw.player.score.trackers.user_basic_data user_basic_data;

    private ArrayList sendList = new ArrayList();

    public Score(){
        Calendar c = Calendar.getInstance();
        lastUpdate = c.getTimeInMillis();
        user_pvp_data = new user_pvp_data();
        user_basic_data = new user_basic_data();
        sendList.add(user_basic_data);
        sendList.add(user_pvp_data);
    }

    /*--------------------*/
    /*----- METHODS ------*/
    /*--------------------*/

    public ArrayList getData() throws IllegalAccessException{
        ArrayList R = new ArrayList<>();
        ArrayList r = new ArrayList<>();
        for(Object s : sendList) {
            R.add(s.getClass().getSimpleName());
            for (Field f : s.getClass().getDeclaredFields()) {
                r.add(f.getName());
                r.add(f.get(s));
            }
            R.add(r.clone());
            r.clear();
        }
        resetTrackers();
        return R;
    }

    public void resetTrackers(){
        sendList.clear();
        user_pvp_data = new user_pvp_data();
        user_basic_data = new user_basic_data();
        sendList.add(user_basic_data);
        sendList.add(user_pvp_data);
    }

    public boolean checkLastSync(){
        Calendar c = Calendar.getInstance();
        long time = c.getTimeInMillis();
        if(time - getLastUpdate() >= updateInterval && !isDirty()){
            setLastUpdate(time);
            setDirty(true);
            return true;
        }
        return false;
    }

    /*--------------------*/
    /*----- GETTERS ------*/
    /*--------------------*/
    public gw.player.score.trackers.user_pvp_data getUser_pvp_data() {
        return user_pvp_data;
    }

    public gw.player.score.trackers.user_basic_data getUser_basic_data() {
        return user_basic_data;
    }

    private long getLastUpdate() {
        return lastUpdate;
    }

    public long getUpdateInterval() {
        return updateInterval;
    }

    public boolean isDirty() {
        return dirty;
    }

    /*--------------------*/
    /*----- SETTERS ------*/
    /*--------------------*/

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}
