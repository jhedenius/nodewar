package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class StudsbollBot implements PlayerController {

   private static final int MAX_DEPTH = 2;
   public static final int MIN_ATTACK_OUTPOST = 6;
   private static final double SIZE_MULTIPLIER = 1.5;
   private static final double DICE_MULTIPLIER = 1.2;
   private static final double ADJACENT_ENEMY_MULTIPLIER = 1.0;
   private static final double ENEMY_DICE_MULTIPLIER = 1.0;

   @Override
   public Move getNextMove(Game gameState) {

      PlayerInfo playerInfo = gameState.getCurrentPlayer();

      List<Node> ownedNodes = Arrays.asList(GameStateUtils.getPlayerNodes(playerInfo, gameState).toArray(new Node[] { }));


      Collections.sort(ownedNodes, new Comparator<Node>() {
         @Override
         public int compare(Node o1, Node o2) {
            return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
         }
      });

      Set<Node> bestConnectedGraph = getBestConnectedGraph(playerInfo, gameState);

      for (Node node : ownedNodes) {
         if (node.getDiceCount() < 2) {
            continue;
         }

         if (bestConnectedGraph.contains(node)) {
            List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[] { }));
            Collections.sort(adjacentNodes, new Comparator<Node>() {
               @Override
               public int compare(Node o1, Node o2) {
                  return Integer.compare(o1.getDiceCount(), o2.getDiceCount());
               }
            });

            for (Node adjacent : adjacentNodes) {
               if (adjacent.getOccupier() == null || !adjacent.getOccupier().equals(playerInfo)) {
                  if (adjacent.getDiceCount() < node.getDiceCount() || node.getDiceCount() == 8)
                     return new Move(node, adjacent, "Attack", MoveType.MOVE);
               }
            }
         }
      }

      for (Node node : ownedNodes) {
         if (node.getDiceCount() < 2) {
            continue;
         }

         if (!bestConnectedGraph.contains(node)) {
            List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[] { }));
            Collections.sort(adjacentNodes, new Comparator<Node>() {
               @Override
               public int compare(Node o1, Node o2) {
                  return Integer.compare(o1.getDiceCount(), o2.getDiceCount());
               }
            });

            List<Node> pathToLargestArea = distanceToLargestArea(node, playerInfo, bestConnectedGraph, new HashSet<Node>(), 0);
            if (pathToLargestArea != null) {
               int diceCount = 0;
               for (Node nodePath : pathToLargestArea) {
                  diceCount += nodePath.getDiceCount();
               }
               if (node.getDiceCount() >= diceCount && pathToLargestArea.size() == 1) {
                  return new Move(node, pathToLargestArea.get(0), "Attack", MoveType.MOVE);
               }
               if (node.getDiceCount() > diceCount + pathToLargestArea.size()) {
                  return new Move(node, pathToLargestArea.get(pathToLargestArea.size() - 1), "Attack", MoveType.MOVE);
               }
            }

            if (node.getDiceCount() > MIN_ATTACK_OUTPOST) {

               for (Node adjacent : adjacentNodes) {
                  if (adjacent.getOccupier() == null || !adjacent.getOccupier().equals(playerInfo)) {
                     if (adjacent.getDiceCount() < node.getDiceCount())
                        return new Move(node, adjacent, "Attack", MoveType.MOVE);
                  }
               }
            }
         }


      }

      return new Move(null, null, "Can't move", MoveType.DONE);
   }

   private List<Node> sortNodes(Set<Node> nodes) {
      LinkedList<Node> list = new LinkedList<Node>(nodes);
      Collections.sort(list, new Comparator<Node>() {
         @Override
         public int compare(Node o1, Node o2) {
            return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
         }
      });
      return list;
   }

   private List<Node> distanceToLargestArea(Node node, PlayerInfo playerInfo, Set<Node> largestArea, Set<Node> visited, int depth) {
      if (depth == MAX_DEPTH)
         return null;
      visited.add(node);
      for (Node adjacent : node.getAdjacent()) {
         if (largestArea.contains(adjacent)) {
            List<Node> path = new ArrayList<>();
            path.add(node);
            return path;
         }
         if (adjacent.getOccupier() != null && !adjacent.getOccupier().getId().equals(playerInfo.getId()) && !(visited.contains(adjacent))) {
            List<Node> path = distanceToLargestArea(adjacent, playerInfo, largestArea, visited, depth + 1);
            if (path != null) {
               path.add(node);
               return path;
            }
         }
      }
      return null;
   }

    private List<Node> distanceToOutpost(Node node, PlayerInfo playerInfo, Set<Node> largestArea, Set<Node> visited, int depth) {
    if (depth == MAX_DEPTH)
       return null;
    visited.add(node);
    for (Node adjacent : node.getAdjacent()) {
       if (largestArea.contains(adjacent)) {
          continue;
       }
       if (adjacent.getOccupier() != null && adjacent.getOccupier().getId().equals(playerInfo.getId())) {
         List<Node> path = new ArrayList<>();
         path.add(node);
         return path;
       }
       if (adjacent.getOccupier() != null && !adjacent.getOccupier().getId().equals(playerInfo.getId()) && !(visited.contains(adjacent))) {
          List<Node> path = distanceToOutpost(adjacent, playerInfo, largestArea, visited, depth + 1);
          if (path != null) {
             path.add(node);
             return path;
          }
       }
    }
    return null;
 }

    private Set<Node> getBestConnectedGraph(PlayerInfo playerInfo, Game gameState) {
     Set<Node> biggestConectedArea = new HashSet<>();
       double bestScore = Double.NEGATIVE_INFINITY;
     for (Node node : GameStateUtils.getPlayerNodes(playerInfo, gameState)) {
        Set<Node> tempConnected = getSize(playerInfo, node, new HashSet<Node>());
        double tempScore = getValueOfGraph(tempConnected,playerInfo);
        if(tempScore > bestScore) {
           bestScore = tempScore;
           biggestConectedArea = tempConnected;
        }
     }
     return biggestConectedArea;
  }

   private double getValueOfGraph(Set<Node> nodes, PlayerInfo playerInfo) {
      double value = nodes.size() * SIZE_MULTIPLIER;
      double enemyValue = 0.0;
      Set<Node> enemyNodesChecked = new HashSet<Node>();
      for(Node node: nodes){
         double diceValue = node.getDiceCount() * DICE_MULTIPLIER;
         for(Node adjacent : node.getAdjacent()){
            if(adjacent.getOccupier() != null && !adjacent.getOccupier().getId().equals(playerInfo.getId())){
               diceValue *= ADJACENT_ENEMY_MULTIPLIER;
               break;
            }
         }
         value += diceValue;
         for(Node adjacent : node.getAdjacent()){
            if(adjacent.getOccupier() != null && !adjacent.getOccupier().getId().equals(playerInfo.getId()) && !enemyNodesChecked.contains(adjacent)){
               enemyValue += adjacent.getDiceCount() * ENEMY_DICE_MULTIPLIER;
               enemyNodesChecked.add(adjacent);
            }
         }
      }
      return value-enemyValue;
   }

   private Set<Node> getLargestConnectedGraph(PlayerInfo playerInfo, Game gameState) {
      Set<Node> biggestConectedArea = new HashSet<>();
      for (Node node : GameStateUtils.getPlayerNodes(playerInfo, gameState)) {
         Set<Node> tempConnected = getSize(playerInfo, node, new HashSet<Node>());
         if(tempConnected.size() > biggestConectedArea.size()) {
            biggestConectedArea = tempConnected;
         }
      }
      return biggestConectedArea;
   }

   private Set<Node> getSize(PlayerInfo player, Node node, Set<Node> visited) {
      visited.add(node);
      for (Node adjacent : node.getAdjacent()) {
         if (adjacent.getOccupier() != null && adjacent.getOccupier().getId().equals(player.getId()) && !(visited.contains(adjacent))) {
            getSize(player, adjacent, visited);
         }
      }
      return visited;
   }

}
