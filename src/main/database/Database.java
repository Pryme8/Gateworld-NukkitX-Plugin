package main.database;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import main.MainClass;
import ru.nukkit.dblib.DbLib;
import sun.applet.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database{
    private MainClass plugin;
    private String host;
    private String port;
    private String dbName;
    private String userName;
    private String password;

    public Database(MainClass parent){
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

    public static List<String> checkForPlayer(Player p, MainClass parent) throws SQLException {
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
        }
        return list;
    }

    public static boolean createNewPlayer( Player p, MainClass parent) throws SQLException {
        parent.getLogger().info("Making Player on Database!");
        Connection connection = parent.db.getConnection();
        String query = "INSERT INTO `user_data` (`uuid`, `playerName`, `playerClass`) VALUES ('"+p.getUniqueId()+"','"+p.getName()+"',"+"'none')";
        parent.getLogger().info(query);
        Statement statement = connection.prepareStatement(query);
        statement.executeUpdate(query);
        return true;
    }

}
