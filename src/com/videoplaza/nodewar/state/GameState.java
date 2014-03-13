package com.videoplaza.nodewar.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameState {

   private Map<String, Node> nodes = new HashMap<String, Node>();
   private Set<PlayerInfo> players = new HashSet<PlayerInfo>();
   private PlayerInfo currentPlayer;

   public Map<String, Node> getNodes() {
      return nodes;
   }

   public void setNodes(Map<String, Node> nodes) {
      this.nodes = nodes;
   }

   public Set<PlayerInfo> getPlayers() {
      return players;
   }

   public void setPlayers(Set<PlayerInfo> players) {
      this.players = players;
   }

   public PlayerInfo getCurrentPlayer() {
      return currentPlayer;
   }

   public void setCurrentPlayer(PlayerInfo currentPlayer) {
      this.currentPlayer = currentPlayer;
   }

}
