package gw.player.score.trackers;

public class user_basic_data{

    public double totalDistance;

    public user_basic_data(){
        totalDistance = 0;
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
    /*--------------------*/
    /*----- SETTERS ------*/
    /*--------------------*/
    public void addTotalDistance(double dist) {
        this.totalDistance += dist;
    }

}