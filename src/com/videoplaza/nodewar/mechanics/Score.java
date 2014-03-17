package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.PlayerInfo;

public class Score {

   private final int nodes;
   private final int strength;
   private final PlayerInfo playerInfo;

   public int getNodes() {
      return nodes;
   }

   public int getStrength() {
      return strength;
   }

   public PlayerInfo getPlayerInfo() {
      return playerInfo;
   }

   public Score(int nodes, int strength, PlayerInfo playerInfo){
      this.nodes = nodes;
      this.strength = strength;
      this.playerInfo = playerInfo;
   }

   public String toString(){
      return playerInfo.getName() + ", nodes: " + nodes + ", strength: " + strength;
   }

}
