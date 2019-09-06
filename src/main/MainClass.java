package main;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.*;
import cn.nukkit.math.Vector3;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.Player;
import main.database.Database;
import main.gwserver.gwServer;

import java.io.File;

import java.sql.SQLException;
import java.util.LinkedHashMap;

/**Template
 * author: MagicDroidX
 * NukkitExamplePlugin Project
 */
/**gateworld
 * author: Pryme8
 */

public class MainClass extends PluginBase {
    public static boolean enabled;
    public Database db;
    private static main.gwserver.gwServer gwServer;
    private Config config;


    public void onLoad()    {
        this.getLogger().info("Gateworld Plugin - Loaded");
    }

    @Override
    public void onEnable() {
        this.getLogger().info("Gateworld : onEnable()");
        this.getServer().getPluginManager().registerEvents(new EventListener(this), this);
        createConfig();

        //PluginTask
        //this.getServer().getScheduler().scheduleRepeatingTask(new BroadcastPluginTask(this), 200);

        //Save resources
        //this.saveResource("string.txt");

    }

    private void bootServer(){
        //Connect to DB
        enabled = (Server.getInstance().getPluginManager().getPlugin("DbLib") != null);
        if(enabled){
            this.getLogger().info("Connection Loading");
            db = new Database(this);

            db.getConnection();

            this.getLogger().info("Connection Enabled: "+String.valueOf(enabled));
            try {

                gwServer = new gwServer(this);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else{
            this.getLogger().info("DbLib Not Present");
        }
    }

    public main.gwserver.gwServer getGwServer(){
        return gwServer;
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Gateworld - Disabled");
    }


    private void createConfig(){
        //Config reading and writing
        config = new Config(
                new File(this.getDataFolder(), "config.yml"),
                Config.YAML,
                //Default values (not necessary)
                new LinkedHashMap<String, Object>() {
                    {
                        put("gate-world-version", "0.0.1");
                        put("class-reset-warp-end", "49 76 70");
                        put("class-ranger-warp-end", "50 83 65");
                        put("class-warrior-warp-end", "50 83 65");
                        put("class-craftsman-warp-end", "50 83 65");

                        put("mysql-host", "localhost");
                        put("mysql-port", "3306");
                        put("mysql-dbName", "db01");
                        put("mysql-userName", "root");
                        put("mysql-password", "adminpassword");

                    }
                });
        //Now try to get the value, the default value will be given if the key isn't exist!
        this.getLogger().info(String.valueOf(config.get("gate-world-version", "0.0.1")));
        //Don't forget to save it!
        config.save();
        bootServer();
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = this.getServer().getPlayer(sender.getName());
        switch (command.getName()) {
            case "player_choose_class_ranger":
                if(sender.getName() != "CONSOLE"){
                    this.getServer().dispatchCommand( getServer().getConsoleSender(), "tell "+ sender.getName() +" 'You have selected the Ranger class'");
                    String[] sWarp = (String.valueOf(this.getConfig().get("class-ranger-warp-end")).split(" "));
                    Vector3 warpPos = new Vector3(Double.valueOf(sWarp[0]), Double.valueOf(sWarp[1]), Double.valueOf(sWarp[2]));
                    p.teleport(warpPos);
                    Inventory inv = p.getInventory();
                    inv.clearAll();
                    inv.addItem( Item.get(Item.BOW, 0, 1).setCustomName(p.getName()+"'s Starting Bow"));
                    inv.addItem( Item.get(Item.ARROW, 0, 128));
                    inv.addItem( Item.get(Item.LEATHER_CAP, 0, 1).setCustomName(p.getName()+"'s Starting Cap"));
                    inv.addItem( Item.get(Item.LEATHER_TUNIC, 0, 1).setCustomName(p.getName()+"'s Starting Tunic"));
                    inv.addItem( Item.get(Item.LEATHER_PANTS, 0, 1).setCustomName(p.getName()+"'s Starting Pants"));
                    inv.addItem( Item.get(Item.LEATHER_BOOTS, 0, 1).setCustomName(p.getName()+"'s Starting Boots"));
                    inv.addItem( Item.get(Item.STONE_AXE, 0, 1).setCustomName(p.getName()+"'s Starting Axe"));
                    inv.addItem( Item.get(Item.COOKED_SALMON, 0, 16));
                    inv.addItem( Item.get(Item.BEETROOT_SOUP, 0, 16));
                    inv.addItem( Item.get(Item.TORCH , 0, 32));
                    inv.sendContents(p);
                }
            break;
            case "player_choose_class_warrior":
                if(sender.getName() != "CONSOLE"){
                    this.getServer().dispatchCommand( getServer().getConsoleSender(), "tell "+ sender.getName() +" 'You have selected the Warrior class'");
                    String[] sWarp = (String.valueOf(this.getConfig().get("class-ranger-warp-end")).split(" "));
                    Vector3 warpPos = new Vector3(Double.valueOf(sWarp[0]), Double.valueOf(sWarp[1]), Double.valueOf(sWarp[2]));
                    p.teleport(warpPos);
                    Inventory inv = p.getInventory();
                    inv.clearAll();
                    inv.addItem( Item.get(Item.IRON_SWORD, 0, 1).setCustomName(p.getName()+"'s Starting Sword"));
                        inv.addItem( Item.get(Item.CHAIN_HELMET, 0, 1).setCustomName(p.getName()+"'s Starting Helm"));
                    inv.addItem( Item.get(Item.CHAIN_CHESTPLATE, 0, 1).setCustomName(p.getName()+"'s Starting Chestplate"));
                    inv.addItem( Item.get(Item.CHAIN_LEGGINGS, 0, 1).setCustomName(p.getName()+"'s Starting Leggings"));
                    inv.addItem( Item.get(Item.CHAIN_BOOTS, 0, 1).setCustomName(p.getName()+"'s Starting Boots"));
                    inv.addItem( Item.get(Item.STONE_AXE, 0, 1).setCustomName(p.getName()+"'s Starting Axe"));
                    inv.addItem( Item.get(Item.COOKED_BEEF, 0, 16));
                    inv.addItem( Item.get(Item.BAKED_POTATO, 0, 16));
                    inv.addItem( Item.get(Item.TORCH, 0, 32));
                    inv.sendContents(p);
                }
            break;
            case "player_choose_class_craftsman":
                if(sender.getName() != "CONSOLE"){
                    this.getServer().dispatchCommand( getServer().getConsoleSender(), "tell "+ sender.getName() +" 'You have selected the Craftsman class'");
                    String[] sWarp = (String.valueOf(this.getConfig().get("class-craftsman-warp-end")).split(" "));
                    Vector3 warpPos = new Vector3(Double.valueOf(sWarp[0]), Double.valueOf(sWarp[1]), Double.valueOf(sWarp[2]));
                    p.teleport(warpPos);
                    Inventory inv = p.getInventory();
                    inv.clearAll();
                    inv.addItem( Item.get(Item.IRON_AXE, 0, 1).setCustomName(p.getName()+"'s Starting Axe"));
                    inv.addItem( Item.get(Item.IRON_PICKAXE, 0, 1).setCustomName(p.getName()+"'s Starting Pick"));
                    inv.addItem( Item.get(Item.IRON_SHOVEL, 0, 1).setCustomName(p.getName()+"'s Starting Axe"));
                    inv.addItem( Item.get(Item.LEATHER_TUNIC, 0, 1).setCustomName(p.getName()+"'s Starting Tunic"));
                    inv.addItem( Item.get(Item.LEATHER_BOOTS, 0, 1).setCustomName(p.getName()+"'s Starting Boots"));
                    inv.addItem( Item.get(Item.CRAFTING_TABLE, 0, 1).setCustomName(p.getName()+"'s Starting Table"));
                    inv.addItem( Item.get(Item.COOKED_MUTTON, 0, 16));
                    inv.addItem( Item.get(Item.PUMPKIN_PIE, 0, 16));
                    inv.addItem( Item.get(Item.TORCH, 0, 32));
                    inv.sendContents(p);
                }
            break;
            case "player_reset_class":
                if(sender.getName() != "CONSOLE"){
                    this.getServer().dispatchCommand( getServer().getConsoleSender(), "tell "+ sender.getName() +" 'You have reset you're class.'");
                    p.getInventory().clearAll();
                    String[] sWarp = (String.valueOf(this.getConfig().get("class-reset-warp-end")).split(" "));
                    Vector3 warpPos = new Vector3(Double.valueOf(sWarp[0]), Double.valueOf(sWarp[1]), Double.valueOf(sWarp[2]));
                    p.teleport(warpPos);

                    this.getLogger().info(("Class was reset by " + sender.getName()));
                }
            break;

            //case "example":
            //    try {
            //        this.getLogger().info(Utils.readFile(new File(this.getDataFolder(), "string.txt")) + " " + sender.getName());
            //    } catch (IOException e) {
            //        throw new RuntimeException(e);
            //    }
            //    break;
        }
        return true;
    }




}
