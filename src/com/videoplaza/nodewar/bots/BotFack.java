package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BotFack implements PlayerController {

   private static final int YOLO_TRESHOLD = 5;
   private static boolean YOLO;

   @Override
   public Move getNextMove(Game gameState) {


      PlayerInfo playerInfo = gameState.getCurrentPlayer();

      YOLO = isYolo(gameState, playerInfo);

      List<Node> ownedNodes = Arrays.asList(GameStateUtils.getPlayerNodes(playerInfo, gameState).toArray(new Node[] { }));

      Collections.sort(ownedNodes, new Comparator<Node>() {
         @Override
         public int compare(Node o1, Node o2) {
            return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
         }
      });

      for (Node node : ownedNodes) {
         if (node.getDiceCount() < 2) {
            continue;
         }
         List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[] { }));
         Collections.sort(adjacentNodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
               return Integer.compare(o1.getDiceCount(), o2.getDiceCount());
            }
         });
         double ourSize = getLargestConnectedGraph(playerInfo, gameState);
         List<NodeWeight> weights = new ArrayList<NodeWeight>();
         for (Node adjacent : adjacentNodes) {
            if (adjacent.getOccupier() == null || !adjacent.getOccupier().equals(playerInfo)) {
               NodeWeight weight = new NodeWeight(adjacent);
               weight.weight = 1/adjacent.getDiceCount();
               if(!hasMoreDice(node, adjacent))
                  weight.weight = 0;

               PlayerInfo adjOccupier = adjacent.getOccupier();
               adjacent.setOccupier(playerInfo);
               double sizeIfAdded = getLargestConnectedGraph(playerInfo, gameState, adjacent);
               adjacent.setOccupier(adjOccupier);
               weight.weight *= ((sizeIfAdded - ourSize)+1)*16;

               double theSizeOfThem = 1;
               for(Node adj2adjNode : adjacent.getAdjacent()) {
                  if (adj2adjNode.getOccupier().equals(adjOccupier))
                     theSizeOfThem++;
               }

               double theSizeOfUs = 1;
               for(Node ourNode : node.getAdjacent()) {
                  if (ourNode.getOccupier().equals(playerInfo))
                     theSizeOfUs++;
               }

               double neighbourDifference = (theSizeOfUs / theSizeOfThem)* 8;
               weight.weight *= neighbourDifference;

               if(theSizeOfUs ==1)
                  weight.weight = 10000000;
               weights.add(weight);
            }
         }
         Collections.sort(weights, new Comparator<NodeWeight>() {
            @Override
            public int compare(NodeWeight o1, NodeWeight o2) {
               return -Double.compare(o1.weight, o2.weight);
            }
         });
         if (weights.isEmpty()){
            continue;
         }
         NodeWeight toAttack = weights.get(0);
         if(toAttack.weight > 0.000001)
            return new Move(node, toAttack.node, "Attack", MoveType.MOVE);
      }

      return new Move(null, null, "Can't move", MoveType.DONE);
   }

   private class NodeWeight {
      Node node;
      double weight;
      public NodeWeight (Node n){
         node = n;
      }
   }

   private boolean isYolo(Game gameState, PlayerInfo us) {
      int ourSize = getLargestConnectedGraph(us, gameState);
      for (PlayerInfo player : gameState.getPlayers()){
         int wat = getLargestConnectedGraph(player, gameState);
         if (wat > ourSize*2 && gameState.getCurrentTurn() > YOLO_TRESHOLD)
            return true;
      }
      return false;
   }
   private static int getLargestConnectedGraph(PlayerInfo playerInfo, Game gameState) {
      return getLargestConnectedGraph(playerInfo, gameState, null);
   }

   private static int getLargestConnectedGraph(PlayerInfo playerInfo, Game gameState, Node addNode) {
      int maxSize = 0;
      Set<Node> nodes = GameStateUtils.getPlayerNodes(playerInfo, gameState);
      if(addNode != null)
         nodes.add(addNode);
      for (Node node : GameStateUtils.getPlayerNodes(playerInfo, gameState)) {
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

   private boolean hasMoreDice(Node attacker, Node defender){
      return attacker.getDiceCount() > defender.getDiceCount();
   }

}
