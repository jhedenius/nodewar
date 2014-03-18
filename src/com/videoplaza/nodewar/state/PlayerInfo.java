package com.videoplaza.nodewar.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.videoplaza.nodewar.json.Game;
import com.videoplaza.nodewar.mechanics.PlayerController;

public class PlayerInfo {

   @JsonIgnore
   public Game game;
   public int id;
   public String name;

   public String implementation;
   public String argument;

   public PlayerInfo() {}

   public PlayerInfo(String name, String implementation, String argument) {
      this.name = name;
      this.implementation = implementation;
      this.argument = argument;
   }

   public Integer getId() {
      return id;
   }

   public PlayerController getPlayerImplementation() {
      return game.controllers.get(id);
   }

   public String getName() {
      return game.players.get(id).name;
   }

   public String toString() {
      return getName();
   }

   public String getImplementation() {
      return implementation;
   }

   public String getArgument() {
      return argument;
   }
}
