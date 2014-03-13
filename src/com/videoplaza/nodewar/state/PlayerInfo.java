package com.videoplaza.nodewar.state;

import com.videoplaza.nodewar.mechanics.Player;

import java.util.UUID;

public class PlayerInfo {

   private String id = UUID.randomUUID().toString();
   private String name;
   private Player playerImplementation;

   public PlayerInfo(String name, Player playerImplementation) {
      this.name = name;
      this.playerImplementation = playerImplementation;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Player getPlayerImplementation() {
      return playerImplementation;
   }

   public void setPlayerImplementation(Player playerImplementation) {
      this.playerImplementation = playerImplementation;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String toString() {
      return name;
   }

}
