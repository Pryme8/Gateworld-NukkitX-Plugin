package gw.database;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import gw.Core;
import gw.player.PlayerContainer;

import ru.nukkit.dblib.DbLib;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database{
    private Core plugin;
    private String host;
    private String port;
    private String dbName;
    private String userName;
    private String password;
    private Connection connection;

    public Database(Core parent){
        plugin = parent;
        plugin.getLogger().info("Loading Database Config...");
        Config config = plugin.getConfig();
        host = String.valueOf(config.get("mysql-host", "localhost"));
        port = String.valueOf(config.get("mysql-port", "3306"));
        dbName = String.valueOf(config.get("mysql-dbName", "DBNAME"));
        userName = String.valueOf(config.get("mysql-userName", "USERNAME"));
        password = String.valueOf(config.get("mysql-password", "PASSWORD"));
    }

    public Connection getConnection(){
        plugin.getLogger().info("Getting Connection");
        plugin.getLogger().info(host+"-"+port+"-"+dbName+"-"+userName);
        connection = connectToMySQL( host, port, dbName, userName, password);
        return connection;
    }

    public Connection currentConnection(){
        try {
            if(connection.isClosed()){
               connection = getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public Connection connectToMySQL(String host, String port, String db, String name, String pwd) {
        if (!plugin.enabled) return null;
        plugin.getLogger().info("Connecting...");
        Connection con = DbLib.getMySqlConnection(host, (port == null || port.isEmpty() ? -1 : Integer.parseInt(port)),
                db, name, pwd);
        if (con == null) plugin.enabled = false;
        return con;
    }

    public static List<String> getPlayerData(Player p, Core parent) throws SQLException {
        List<String> list = new ArrayList<String>();
        parent.getLogger().info("Checking for Player on Database!");
        Connection con = parent.db.currentConnection();
        if (con == null) return list;
        String query = "SELECT * FROM `user_data` WHERE `uuid`='"+p.getUniqueId()+"'";
        parent.getLogger().info(query);
        Statement statement = con.prepareStatement(query);
        ResultSet rs = statement.executeQuery(query);
        if (rs == null) return list;
        while (rs.next()) {
            list.add(
                    String.valueOf(rs.getInt("id"))
            );
            list.add(
                    rs.getString("playerName")
            );
            list.add(
                    rs.getString("playerClass")
            );
            list.add(
                    rs.getString("currentZone")
            );
            list.add(
                    rs.getString("spawnPoint")
            );
        }
        return list;
    }

    public static boolean createNewPlayer( PlayerContainer pc, Core parent) throws SQLException {
        parent.getLogger().info("Making Player on Database!");
        Connection con = parent.db.currentConnection();
        Statement statement = con.createStatement();
        statement.addBatch(
                "INSERT INTO `user_data` (`uuid`, `playerName`, `playerClass`, `spawnPoint`)" +
                " VALUES ('"+pc.getPlayer().getUniqueId()+"','"+pc.getName()+"',"+"'none', 'StartZone:0,0,0')"
        );
        statement.executeBatch();
        return true;
    }

    public static boolean scoreBoardsInit( Integer _id, Core parent) throws SQLException {
        parent.getLogger().info("Making Player Scores on Database!");
        Connection con = parent.db.currentConnection();
        Statement statement = con.createStatement();
        statement.addBatch(
            "INSERT INTO `user_pvp_data` (`pid`)" +
                " VALUES ('"+_id+"')"
        );
        statement.addBatch(
            "INSERT INTO `user_basic_data` (`pid`)" +
                " VALUES ('"+_id+"')"
        );
        statement.executeBatch();
        return true;
    }


    public static boolean updatePlayerClass(String playerClass, PlayerContainer pc, Core parent) throws SQLException {
        parent.getLogger().info("Updating Player on Database!");
        Connection con = parent.db.currentConnection();
        String query = "UPDATE `user_data` SET `playerClass`='"+playerClass+"' WHERE `id`='"+pc.getId()+"'";
        parent.getLogger().info(query);
        Statement statement = con.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

    public static boolean updatePlayerZone(String newZone, PlayerContainer pc, Core parent) throws SQLException {
        parent.getLogger().info("Updating Player on Database!");
        Connection con = parent.db.currentConnection();
        String query = "UPDATE `user_data` SET `currentZone`='"+newZone+"' WHERE `id`='"+pc.getId()+"'";
        parent.getLogger().info(query);
        Statement statement = con.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

    public static boolean updatePlayerSpawn(String spawn, PlayerContainer pc, Core parent) throws SQLException {
        parent.getLogger().info("Updating Player on Database!");
        Connection con = parent.db.currentConnection();
        String query = "UPDATE `user_data` SET `spawnPoint`='"+spawn+"' WHERE `id`='"+pc.getId()+"'";
        parent.getLogger().info(query);
        Statement statement = con.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

    public void updateStats(PlayerContainer pc, Core parent){
        try {
            int i = 0;
            StringBuilder query = new StringBuilder();
            Connection con = parent.db.currentConnection();
            Statement statement = con.createStatement();
            loop1: for(Object s : pc.getScore().getData()){
                if(i<1){
                    parent.getLogger().info(s.toString());
                    query = new StringBuilder("UPDATE `" + s.toString() + "` SET ");
                    i=1;
                    continue loop1;
                }else{
                    String _s = s.toString();
                    parent.getLogger().info("_s:"+_s);
                    String[] _ss = _s.substring(1, _s.length()-1).split(",");
                        loop2: for( int j = 0; j<_ss.length; j+=3){
                            query.append("`").append(_ss[j].trim()).append("` = ");
                            if(_ss[j+2].trim().equals("add")){
                                query.append("`"+_ss[j].trim()).append("`+").append(_ss[j + 1].trim());
                            }else{
                                query.append("'"+_ss[j + 1].trim()+"'");
                            }

                            if(j!=_ss.length-3){
                                query.append(",");
                            }else{
                                query.append(" ");
                            }
                        }

                    query.append("WHERE `pid`='").append(pc.getId()).append("'");
                    parent.getLogger().info("Batch Query:"+query.toString());
                    statement.addBatch(query.toString());
                    query.setLength(0);
                    i = 0;
                    continue loop1;
                }
            }
            statement.executeBatch();
            pc.getScore().setDirty(false);
        } catch (IllegalAccessException | SQLException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }



    public static boolean updatePvpDamage(Float dmg, PlayerContainer pc, Core parent) throws SQLException {
        Connection connection = parent.db.getConnection();
        String query = "UPDATE `user_pvp_data` SET `totalDamage`= `totalDamage`+"+dmg+" WHERE `uuid`='"+pc.getPlayer().getUniqueId()+"'";
        parent.getLogger().info(query);
        Statement statement = connection.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

    public static boolean updatePvpDamageTaken(Float dmg, PlayerContainer pc, Core parent) throws SQLException {
        Connection connection = parent.db.getConnection();
        String query = "UPDATE `user_pvp_data` SET `totalDamageTaken`= `totalDamageTaken`+"+dmg+" WHERE `uuid`='"+pc.getPlayer().getUniqueId()+"'";
        parent.getLogger().info(query);
        Statement statement = connection.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

    public static boolean addPvpKill( PlayerContainer pc, Core parent) throws SQLException {
        Connection connection = parent.db.getConnection();
        String query = "UPDATE `user_pvp_data` SET `totalKills`=`totalKills`+1 WHERE `uuid`='"+pc.getPlayer().getUniqueId()+"'";
        parent.getLogger().info(query);
        Statement statement = connection.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

    public static boolean addMurdered( PlayerContainer pc, Core parent) throws SQLException {
        Connection connection = parent.db.getConnection();
        String query = "UPDATE `user_pvp_data` SET `murderedCount`=`murderedCount`+1 WHERE `uuid`='"+pc.getPlayer().getUniqueId()+"'";
        parent.getLogger().info(query);
        Statement statement = connection.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

}
