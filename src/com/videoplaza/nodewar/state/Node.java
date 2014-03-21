package com.videoplaza.nodewar.state;

import java.util.HashSet;
import java.util.Set;

public class Node {

   private final Game game;


   private final int regionId;
   private final Region region;

   public Node(Game game, int regionId) {
      this.game = game;
      this.regionId = regionId;
      this.region = game.map.regions.get(regionId);
   }

   public Integer getId() {
      return regionId;
   }

   public Set<Node> getAdjacent() {
      HashSet<Node> nodes = new HashSet<>();
      Region region = game.map.regions.get(regionId);
      for (Integer neighbour : region.neighbours) {
         nodes.add(new Node(game, neighbour));
      }
      return nodes;
   }

   public String getName() {
      return region.name;
   }

   public PlayerInfo getOccupier() {
      return game.players.get(game.occupants.get(regionId).player);
   }

   public int getDiceCount() {
      return game.occupants.get(regionId).strength;
   }

   public String toString() {
      return (getOccupier() == null ? "" : getOccupier() + ":") + getDiceCount();
   }

   public void setOccupier(PlayerInfo occupier) {
      game.occupants.get(regionId).player = occupier.getId();
   }

   public void setDiceCount(int diceCount) {
      game.occupants.get(regionId).strength = diceCount;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }
      if (o == null || getClass() != o.getClass()) {
         return false;
      }

      Node node = (Node) o;

      if (regionId != node.regionId) {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode() {
      return regionId;
   }

}
