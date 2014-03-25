package com.videoplaza.nodewar.state;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
      return name;
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

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      PlayerInfo that = (PlayerInfo) o;

      if (id != that.id) {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode() {
      return id;
   }
}
