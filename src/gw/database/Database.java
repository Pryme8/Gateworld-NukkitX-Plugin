package gw.database;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import gw.Core;
import gw.player.PlayerContainer;


import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
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
        return connectToMySQL( host, port, dbName, userName, password);
    }

    public Connection connectToMySQL(String host, String port, String db, String name, String pwd) {
        if (!plugin.enabled) return null;
        plugin.getLogger().info("Connecting...");
        Connection connection = DbLib.getMySqlConnection(host, (port == null || port.isEmpty() ? -1 : Integer.parseInt(port)),
                db, name, pwd);
        if (connection == null) plugin.enabled = false;
        return connection;
    }

    public static List<String> getPlayerData(Player p, Core parent) throws SQLException {
        List<String> list = new ArrayList<String>();
        parent.getLogger().info("Checking for Player on Database!");
        Connection connection = parent.db.getConnection();
        if (connection == null) return list;
        String query = "SELECT * FROM `user_data` WHERE `uuid`='"+p.getUniqueId()+"'";
        parent.getLogger().info(query);
        Statement statement = connection.prepareStatement(query);
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

    public static boolean createNewPlayer( Player p, Core parent) throws SQLException {
        parent.getLogger().info("Making Player on Database!");
        Connection connection = parent.db.getConnection();
        Statement statement = connection.createStatement();
        statement.addBatch(
                "INSERT INTO `user_data` (`uuid`, `playerName`, `playerClass`, `spawnPoint`)" +
                " VALUES ('"+p.getUniqueId()+"','"+p.getName()+"',"+"'none', 'StartZone:0,0,0')"
        );
        statement.addBatch(
                "INSERT INTO `user_pvp_data` (`uuid`)" +
                        " VALUES ('"+p.getUniqueId()+"')"
        );
        statement.addBatch(
                "INSERT INTO `user_basic_data` (`uuid`)" +
                        " VALUES ('"+p.getUniqueId()+"')"
        );
        statement.executeBatch();
        return true;
    }

    public static boolean updatePlayerClass(String playerClass, PlayerContainer pc, Core parent) throws SQLException {
        parent.getLogger().info("Updating Player on Database!");
        Connection connection = parent.db.getConnection();
        String query = "UPDATE `user_data` SET `playerClass`='"+playerClass+"' WHERE `uuid`='"+pc.getPlayer().getUniqueId()+"'";
        parent.getLogger().info(query);
        Statement statement = connection.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

    public static boolean updatePlayerZone(String newZone, PlayerContainer pc, Core parent) throws SQLException {
        parent.getLogger().info("Updating Player on Database!");
        Connection connection = parent.db.getConnection();
        String query = "UPDATE `user_data` SET `currentZone`='"+newZone+"' WHERE `uuid`='"+pc.getPlayer().getUniqueId()+"'";
        parent.getLogger().info(query);
        Statement statement = connection.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

    public static boolean updatePlayerSpawn(String spawn, PlayerContainer pc, Core parent) throws SQLException {
        parent.getLogger().info("Updating Player on Database!");
        Connection connection = parent.db.getConnection();
        String query = "UPDATE `user_data` SET `spawnPoint`='"+spawn+"' WHERE `uuid`='"+pc.getPlayer().getUniqueId()+"'";
        parent.getLogger().info(query);
        Statement statement = connection.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

    public void updateStats(PlayerContainer pc, Core parent){
        try {
            int i = 0;
            StringBuilder query = new StringBuilder();
            Connection connection = parent.db.getConnection();
            Statement statement = connection.createStatement();
            loop1: for(Object s : pc.getScore().getData()){
                if(i<1){
                    parent.getLogger().info(s.toString());
                    query = new StringBuilder("UPDATE `" + s.toString() + "` SET ");
                    i=1;
                    continue loop1;
                }else{
                    String _s = s.toString();
                    String[] _ss = _s.substring(1, _s.length()-1).split(",");
                        loop2: for( int j = 0; j<_ss.length; j+=2){
                            query.append("`").append(_ss[j].trim()).append("` = `").append(_ss[j].trim()).append("`+").append(_ss[j + 1].trim());
                            if(j!=_ss.length-2){
                                query.append(",");
                            }else{
                                query.append(" ");
                            }
                        }

                    query.append("WHERE `uuid`='").append(pc.getPlayer().getUniqueId().toString()).append("'");
                    //parent.getLogger().info("Batch Query:"+query.toString());
                    statement.addBatch(query.toString());
                    query.setLength(0);
                    i = 0;
                    continue loop1;
                }
            }
            statement.executeBatch();
            pc.getScore().setDirty(false);
        } catch (IllegalAccessException | SQLException e) {
            e.printStackTrace();
        }
    }

    /* THANKS 2 Original Author. */
    /*https://stackoverflow.com/questions/6514876/most-efficient-conversion-of-resultset-to-json*/

        public JSONArray convertRstoJson(ResultSet rs ) throws SQLException {
            JSONArray json = new JSONArray();
            ResultSetMetaData rsmd = rs.getMetaData();
            while(rs.next()) {
                int numColumns = rsmd.getColumnCount();
                JSONObject obj = new JSONObject();

                for (int i=1; i<numColumns+1; i++) {
                    String column_name = rsmd.getColumnName(i);

                    if(rsmd.getColumnType(i)==java.sql.Types.ARRAY){
                        obj.put(column_name, rs.getArray(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.BIGINT){
                        obj.put(column_name, rs.getInt(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.BOOLEAN){
                        obj.put(column_name, rs.getBoolean(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.BLOB){
                        obj.put(column_name, rs.getBlob(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.DOUBLE){
                        obj.put(column_name, rs.getDouble(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.FLOAT){
                        obj.put(column_name, rs.getFloat(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.INTEGER){
                        obj.put(column_name, rs.getInt(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.NVARCHAR){
                        obj.put(column_name, rs.getNString(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.VARCHAR){
                        obj.put(column_name, rs.getString(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.TINYINT){
                        obj.put(column_name, rs.getInt(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.SMALLINT){
                        obj.put(column_name, rs.getInt(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.DATE){
                        obj.put(column_name, rs.getDate(column_name));
                    }
                    else if(rsmd.getColumnType(i)==java.sql.Types.TIMESTAMP){
                        obj.put(column_name, rs.getTimestamp(column_name));
                    }
                    else{
                        obj.put(column_name, rs.getObject(column_name));
                    }
                }
                json.add(obj);
            }

            return json;
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
