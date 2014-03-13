package com.videoplaza.nodewar.state;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Node {

   private String id = UUID.randomUUID().toString();
   private Set<Node> adjacent = new HashSet<Node>();
   private String name;
   private PlayerInfo occupier;
   private int diceCount;

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Set<Node> getAdjacent() {
      return adjacent;
   }

   public void setAdjacent(Set<Node> adjacent) {
      this.adjacent = adjacent;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public PlayerInfo getOccupier() {
      return occupier;
   }

   public void setOccupier(PlayerInfo occupier) {
      this.occupier = occupier;
   }

   public int getDiceCount() {
      return diceCount;
   }

   public void setDiceCount(int diceCount) {
      this.diceCount = diceCount;
   }

   public String toString() {
      return (occupier == null ? "" : occupier + ":") + diceCount;
   }
}
