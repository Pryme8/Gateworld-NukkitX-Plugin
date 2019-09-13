package gw.items;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMapInfoRequestEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemMap;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.NukkitRunnable;
import gw.Core;
import gw.engine.Engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

//Thanks t -> https://github.com/PetteriM1/Maps

public class MapMod extends PluginBase implements Listener {
    private Core plugin;
    private Engine engine;

    public MapMod(Core _plugin, Engine _engine) {
        this.plugin = _plugin;
        this.engine = _engine;
    }

    @EventHandler
    public void mapInfo(PlayerMapInfoRequestEvent e) {
        Player p = e.getPlayer();
        ItemMap map = (ItemMap) e.getMap();
        BufferedImage image = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        try {
            Graphics2D graphics = image.createGraphics();
            for (int x = 0; x != 128; x++) {
                for (int y = 0; y != 128; y++) {
                    graphics.setColor(new Color(p.getLevel().getMapColorAt(p.getFloorX() - 64 + x, p.getFloorZ() - 64 + y).getRGB()));
                    graphics.fillRect(x, y, x, y);
                }
            }
        } catch(Exception ex) {}
        map.setImage(image);
    }



    @EventHandler
    public boolean onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Item i = e.getItem();

        if (i.getId() == Item.EMPTY_MAP) {
            if(i.hasMeta()){
                plugin.getLogger().info("Map Meta:"+i.getDamage());
                plugin.getLogger().info("Map Meta:"+i.getId());
            }
            if (!p.isCreative()) {
                new NukkitRunnable() {
                    public void run() {
                        p.getInventory().removeItem(i);
                        p.getInventory().addItem(new ItemMap());
                    }
                }.runTaskLater(null, 1);
            } else p.getInventory().addItem(new ItemMap());
        }
        return true;
    }
}