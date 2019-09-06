package main.player;
import cn.nukkit.Player;

import java.util.List;

public class PlayerContainer {
     private Integer id;
     private String name;
     private String classType;

     private Player nPlayer;

     public PlayerContainer(List<String> pData, Player p){
         id = Integer.parseInt(pData.get(0), 10);
         name = pData.get(1);
         classType = pData.get(1);
         nPlayer = p;
     }

     public Integer getId(){
         return id;
     }

    public String getName(){
        return name;
    }

    public String getClassType(){
        return classType;
    }

    public Player getNplayer(){
        return nPlayer;
    }
}
