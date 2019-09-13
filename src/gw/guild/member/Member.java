package gw.guild.member;

import gw.guild.Guild;
import gw.player.PlayerContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Member {
    private PlayerContainer pc;
    private Integer power;
    private String title;
    public Member( PlayerContainer _pc){
        pc = _pc;
        init();
    }
    /*--------------------*/
    /*----- METHODS ------*/
    /*--------------------*/
    private void init(){
        try {
            HashMap<String, String> d = getData();
            power = Integer.parseInt(d.get("power"));
            title = d.get("title");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> getData() throws SQLException {
        HashMap<String, String> results = new HashMap();
        pc.getParent().getLogger().info("Getting Guild Members!");
        String query = "SELECT * FROM `guild_members` WHERE `userID`="+pc.getId();
        Connection con =  pc.getParent().db.currentConnection();
        con.prepareStatement(query);
        Statement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery(query);
        if (rs != null){
            while( rs.next()){
                results.put("title", rs.getString("title"));
                results.put("power", rs.getString("power"));
             }
        }
        return results;
    }
    /*--------------------*/
    /*----- GETTERS ------*/
    /*--------------------*/
    public Integer getPower(){
        return power;
    }

    public String getTitle(){
        return title;
    }

    public Integer getID(){
        return pc.getId();
    }
    /*--------------------*/
    /*----- SETTERS ------*/
    /*--------------------*/

}
