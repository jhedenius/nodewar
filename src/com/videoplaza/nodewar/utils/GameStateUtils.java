package com.videoplaza.nodewar.utils;

import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.mechanics.Reinforcement;
import com.videoplaza.nodewar.mechanics.Score;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class GameStateUtils {

   private static final int MAX_DICES = 8;

   public static Set<Node> getPlayerNodes(PlayerInfo player, Game gameState) {
      Set<Node> ownedNodes = new HashSet<>();
      for (Node node : gameState.getNodes()) {
         if (node.getOccupier() != null && node.getOccupier().equals(player))
            ownedNodes.add(node);
      }
      return ownedNodes;
   }

   public static List<Reinforcement> reinforce(PlayerInfo player, Game gameState, Random rnd) {
      ArrayList<Reinforcement> result = new ArrayList<>();
      int largestConnectedTerritory = getLargestConnectedGraph(player, gameState);

      Set<Node> nodes = getPlayerNodes(player, gameState);
      List<Node> randomPlayerNodes;
      Map<Node, Integer> reinforcements = new HashMap<>();
      for (int i = 0; i < largestConnectedTerritory; i++) {
         randomPlayerNodes = getRandomReinforcementNodes(nodes, reinforcements);
         if (randomPlayerNodes.isEmpty()) {
            System.out.println("No nodes to reinforce for player " + player.getName());
            break;
         }

         Node randomNode = randomPlayerNodes.get(rnd.nextInt(randomPlayerNodes.size()));
         if (reinforcements.get(randomNode) == null) {
            reinforcements.put(randomNode, 0);
         }

         reinforcements.put(randomNode, reinforcements.get(randomNode) + 1);
      }

      System.out.println("Reinforcing " + player.getName() + " with " + largestConnectedTerritory);

      for (Map.Entry<Node, Integer> entry : reinforcements.entrySet()) {
         System.out.println("Reinforcing node " + entry.getKey().getName() + " with " + entry.getValue());
         entry.getKey().setDiceCount(entry.getKey().getDiceCount() + entry.getValue());
         result.add(new Reinforcement(entry.getKey().getId(), entry.getValue()));
      }

      return result;
   }

   private static List<Node> getRandomReinforcementNodes(Set<Node> nodes, Map<Node, Integer> reinforcements) {
      List<Node> randomPlayerNodes = new ArrayList<>();
      randomPlayerNodes.addAll(nodes);

      Set<Node> toRemove = new HashSet<>();
      for (Node node : randomPlayerNodes) {
         int reinforcement = reinforcements.containsKey(node) ? reinforcements.get(node) : 0;
         if (node.getDiceCount() + reinforcement >= MAX_DICES) {
            toRemove.add(node);
         }
      }

      randomPlayerNodes.removeAll(toRemove);
      Collections.shuffle(randomPlayerNodes);
      return randomPlayerNodes;
   }

   private static int getLargestConnectedGraph(PlayerInfo playerInfo, Game gameState) {
      int maxSize = 0;
      for (Node node : getPlayerNodes(playerInfo, gameState)) {
         maxSize = Math.max(getSize(playerInfo, node, new HashSet<Node>()), maxSize);
      }
      return maxSize;
   }

   private static int getSize(PlayerInfo player, Node node, Set<Node> visited) {
      visited.add(node);
      for (Node adjacent : node.getAdjacent()) {
         if (adjacent.getOccupier() != null && adjacent.getOccupier().getId().equals(player.getId()) && !(visited.contains(adjacent))) {
            getSize(player, adjacent, visited);
         }
      }
      return visited.size();
   }

   public static Score getLeader(Game gameState) {

      SortedSet<Score> playerScores = new TreeSet<>(new Comparator<Score>() {
         @Override
         public int compare(Score o1, Score o2) {
            int res = Integer.compare(o2.getNodes(), o1.getNodes());
            return res == 0 ? Integer.compare(o2.getStrength(), o1.getStrength()) : res;
         }
      });

      for (PlayerInfo player : gameState.getPlayers()) {
         playerScores.add(getScore(player, gameState));
      }

      return playerScores.first();
   }

   private static Score getScore(PlayerInfo player, Game gameState) {
      Set<Node> nodes = getPlayerNodes(player, gameState);
      int strength = 0;
      for (Node node : nodes) {
         strength += node.getDiceCount();
      }
      return new Score(nodes.size(), strength, player);
   }
}
