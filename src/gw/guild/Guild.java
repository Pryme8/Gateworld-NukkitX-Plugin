package gw.guild;
import cn.nukkit.level.Level;
import gw.Core;
import gw.guild.member.Member;
import gw.player.PlayerContainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Guild {
    private Integer id;
    private String name;
    private String tag;
    private String description;
    private Integer leaderID;
    private String type;

    private boolean isReady = false;

    private List<Member> members = new ArrayList();
    private Core parent;

    public Guild(Integer _id, Core _p){
        parent = _p;
        id = _id;

        init();
    }

    /*--------------------*/
    /*----- METHODS ------*/
    /*--------------------*/
    private void init(){
        try {
            HashMap<String,String> gData = getData();
            name = gData.get("name");
            parent.getLogger().info("Getting Guild Name:"+name);
            tag = gData.get("tag");
            description = gData.get("description");
            leaderID = Integer.parseInt(gData.get("leaderID"));
            description = gData.get("description");
            type = gData.get("type");

            setReady(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> getData() throws SQLException {
        HashMap<String, String> results = new HashMap();
        parent.getLogger().info("Getting Guild Data!");
        String query = "SELECT * FROM `guild_data`";
        Connection con = parent.db.currentConnection();
        con.prepareStatement(query);
        Statement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery(query);
        if (rs != null){
            while( rs.next()){
                results.put("id", rs.getString("id"));
                results.put("name", rs.getString("name"));
                results.put("tag", rs.getString("tag"));
                results.put("description", rs.getString("description"));
                results.put("leaderID", rs.getString("leaderID"));
                results.put("type", rs.getString("type"));
            }
        }
        return results;
    }

    private void syncMembers() throws SQLException {
        getMembers().clear();
        parent.getLogger().info("Syncing Members!");
        String query = "SELECT * FROM `guild_members` WHERE `gid`="+getId();
        Connection con = parent.db.currentConnection();
        con.prepareStatement(query);
        Statement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery(query);
        if (rs != null){
            while( rs.next()){
                Integer _id = Integer.parseInt(rs.getString("id"));
                getMembers().add(
                        new Member(parent.getEngine().getPlayerContainerByID(_id))
                );
            }
        }
    }

    public void addMember(PlayerContainer _pc){
        getMembers().add(new Member(_pc));
        _pc.setCurrentGuild(this);
    }

    /*--------------------*/
    /*----- GETTERS ------*/
    /*--------------------*/

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    private List<Member> getMembers() {
        return members;
    }

    private boolean isReady(){
        return isReady;
    }

    public Integer getLeaderID() {
        return leaderID;
    }

    public String getType() {
        return type;
    }

    public Member getMemberByID(Integer _id){
        for(Member m : getMembers()){
            if(m.getID() == _id){
                return m;
            }
        }
        return null;
    }

    /*--------------------*/
    /*----- SETTERS ------*/
    /*--------------------*/
    private void setReady(boolean ready){
        isReady = ready;
    }

    /*--------------------*/
    /*-- STATIC METHODS --*/
    /*--------------------*/

    public static Guild createNewGuild(PlayerContainer pc, String _args, Core parent) throws SQLException {
        int id = -1;
        String[] args = _args.split(":");
        int pcID = -1;
        if(pc!=null){
            pcID = pc.getId();
        }

        if( args.length < 2
        ){
            parent.getLogger().info("Bad Guild Arg String.");
            return null;
        }

        parent.getLogger().info("Checking for Leader on Database!");
        String query = "SELECT * FROM `guild_data` WHERE `leaderID` = "+pcID;
        Connection con = parent.db.currentConnection();
        con.prepareStatement(query);
        Statement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery(query);
        if (rs == null || rs.next()){
            parent.getLogger().info("Already a Leader!");
            return null;
        };

        parent.getLogger().info("Checking for Name or Tag on Database!");
        query = "SELECT * FROM `guild_data` WHERE `name` = '"+args[0]+"' OR `tag` = '"+args[1]+"'";
        con.prepareStatement(query);
        statement = con.prepareStatement(query);
        rs = statement.executeQuery(query);
        if (rs == null || rs.next()){
            parent.getLogger().info("Already Guild by that name or Tag!");
            return null;
        };

        parent.getLogger().info("Making Guild on Database!");
        query = "INSERT INTO `guild_data` (`name`, `tag`, `description`, `leaderID`) VALUES ('"+args[0]+"', '"+args[1]+"', 'A New Guild!', "+pcID+")";
        statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        id = statement.executeUpdate(query);
        if (id!=-1) {
            parent.getLogger().info("rs ID:"+ Integer.toString(id));
            return new Guild(id, parent);
        }else{
            parent.getLogger().info("Error Making Guild on Database!");
            return null;
        }
    }

    public static List<Integer> getAllGuildIDs(Core parent) throws SQLException {
        List<Integer> results = new ArrayList<Integer>();
        parent.getLogger().info("Getting all Guilds!");
        String query = "SELECT id FROM `guild_data`";
        Connection con = parent.db.currentConnection();
        con.prepareStatement(query);
        Statement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery(query);
        if (rs != null){
            while( rs.next()){
                results.add(rs.getInt("id"));
            }
            return results;
        };
        return null;
    }

    public static List<HashMap> getInfluencingGeofences(PlayerContainer pc, Core parent) throws SQLException {
        List<HashMap> results = new ArrayList<>();
        parent.getLogger().info("Getting Influencing Geofences!");
        String query = "SELECT id, gid, geofenceMeta FROM `guild_zones` WHERE `world` = '"+pc.getCurrentZone()+"'";
        parent.getLogger().info(query);
        Connection con = parent.db.currentConnection();
        con.prepareStatement(query);
        Statement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery(query);
        if (rs != null){
            while( rs.next()){
                HashMap<String, String>  r = new HashMap<>();
                r.put("id", String.valueOf(rs.getInt("id")));
                r.put("guild", String.valueOf(rs.getInt("gid")));
                r.put("meta", rs.getString("geofenceMeta"));
                results.add(r);
            }
            return results;
        }
        return null;
    }

    public static List<HashMap> getInfluencingGeofences(Level level, Core parent) throws SQLException {
        List<HashMap> results = new ArrayList<>();
        parent.getLogger().info("Getting Influencing Geofences!");
        String query = "SELECT id, gid, geofenceMeta FROM `guild_zones` WHERE `world` = '"+level.getName()+"'";
        parent.getLogger().info(query);
        Connection con = parent.db.currentConnection();
        con.prepareStatement(query);
        Statement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery(query);
        if (rs != null){
            while( rs.next()){
                HashMap<String, String>  r = new HashMap<>();
                r.put("id", String.valueOf(rs.getInt("id")));
                r.put("guild", String.valueOf(rs.getInt("gid")));
                r.put("meta", rs.getString("geofenceMeta"));
                results.add(r);
            }
            return results;
        }
        return null;
    }
}





