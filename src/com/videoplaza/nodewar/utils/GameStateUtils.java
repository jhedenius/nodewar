package com.videoplaza.nodewar.utils;

import com.videoplaza.nodewar.mechanics.Score;
import com.videoplaza.nodewar.state.GameState;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class GameStateUtils {

   public static Set<Node> getPlayerNodes(PlayerInfo player, GameState gameState) {
      Set<Node> ownedNodes = new HashSet<>();
      for (Node node : gameState.getNodes().values()) {
         if (node.getOccupier() != null && node.getOccupier().equals(player))
            ownedNodes.add(node);
      }
      return ownedNodes;
   }

   public static void reinforce(PlayerInfo playerInfo, GameState gameState){
      int largestConnectedTerritory = getLargestConnectedGraph(playerInfo, gameState);
      System.out.println("Player " + playerInfo.getName() + " largest connected: " + largestConnectedTerritory);
   }

   private static int getLargestConnectedGraph(PlayerInfo playerInfo, GameState gameState) {
      int maxSize = -1;
      for(Node node:getPlayerNodes(playerInfo, gameState)){
         maxSize = Math.max(getSize(playerInfo, node, new HashSet<Node>()), maxSize);
      }
      return maxSize;
   }

   private static int getSize(PlayerInfo player, Node node, Set<Node> visited) {
      visited.add(node);
      for(Node adjacent:node.getAdjacent()){
         if(adjacent.getOccupier() != null && adjacent.getOccupier().getId().equals(player.getId()) && !(visited.contains(adjacent))){
            getSize(player, adjacent, visited);
         }
      }
      return visited.size();
   }

   public static Score getLeader(GameState gameState) {

      SortedSet<Score> playerScores = new TreeSet<>(new Comparator<Score>() {
         @Override
         public int compare(Score o1, Score o2) {
            int res = Integer.compare(o2.getNodes(), o1.getNodes());
            return res == 0 ? Integer.compare(o2.getStrength(), o1.getStrength()) : res;
         }
      });


      for(PlayerInfo player:gameState.getPlayers()){
         playerScores.add(getScore(player, gameState));
      }

      return playerScores.first();
   }

   private static Score getScore(PlayerInfo player, GameState gameState) {
      Set<Node> nodes = getPlayerNodes(player, gameState);
      int strength = 0;
      for(Node node:nodes){
         strength += node.getDiceCount();
      }
      return new Score(nodes.size(), strength, player);
   }
}
