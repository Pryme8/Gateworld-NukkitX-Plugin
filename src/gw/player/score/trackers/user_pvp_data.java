package gw.player.score.trackers;

public class user_pvp_data{
  public double totalDamage;
  public double totalDamageTaken;
  public int totalKills;
  public int murderedCount;

  public user_pvp_data(){
      totalDamage = 0;
      totalDamageTaken = 0;
      totalKills = 0;
      murderedCount = 0;
  }
    /*--------------------*/
    /*----- METHODS ------*/
    /*--------------------*/

    /*--------------------*/
    /*----- GETTERS ------*/
    /*--------------------*/
    public double getTotalDamage() {
        return totalDamage;
    }

    public double getTotalDamageTaken() {
        return totalDamageTaken;
    }

    public int getMurderedCount() {
        return murderedCount;
    }

    public int getTotalKills() {
        return totalKills;
    }
    /*--------------------*/
    /*----- SETTERS ------*/
    /*--------------------*/
    public void addTotalDamage(double dmg) {
        this.totalDamage += dmg;
    }

    public void addTotalDamageTaken(double dmg) {
        this.totalDamageTaken += dmg;
    }

    public void addTotalKills() {
        this.totalKills += 1;
    }

    public void addMurderedCount() {
        this.murderedCount += 1;
    }
}

