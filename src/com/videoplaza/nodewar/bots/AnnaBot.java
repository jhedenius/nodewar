package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by nameless on 3/26/14.
 */
public class AnnaBot implements PlayerController {

   @Override
   public Move getNextMove(Game gameState) {

      PlayerInfo playerInfo = gameState.getCurrentPlayer();
      List<Node> ownedNodes = Arrays.asList(GameStateUtils.getPlayerNodes(playerInfo, gameState).toArray(new Node[] { }));
      ArrayList<ArrayList<Node>> clusters = getClusters(ownedNodes);

      Collections.sort(ownedNodes, new Comparator<Node>() {
         @Override
         public int compare(Node o1, Node o2) {
            return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
         }
      });

      Move bestMove = new Move(null, null, "Can't move", MoveType.DONE);
      ArrayList<Node> bestCluster = clusters.get(0);

      for (Node node : bestCluster) {
         Move move = getPassiveMove(node, playerInfo, 0);
         if (move != null) return move;
      }

      return doIt(clusters, playerInfo);
//      for(ArrayList<Node> cluster : clusters) {
//         for (Node node : cluster) {
//            Move move = getPassiveMove(node, playerInfo, -7);
//            if (move != null) return move;
//         }
//      }




   //   return bestMove;
   }

   private Move doIt(ArrayList<ArrayList<Node>> clusters, PlayerInfo player) {
       ArrayList<Node> biggestCluster = clusters.get(0);

      for(int i = 1; i < clusters.size(); i++) {
         for(Node node1 : clusters.get(i)) {
            for(Node node2 : biggestCluster) {
               for(Node adjacent : node1.getAdjacent()) {
                  if(node2.getAdjacent().contains(adjacent) && !adjacent.getOccupier().equals(player)) {
                     return new Move(node1, adjacent, "Attack", MoveType.MOVE);
                  }
               }
            }
         }
      }
      return new Move(null, null, "Can't move", MoveType.DONE);
   }

   private Move getPassiveMove(Node node, PlayerInfo playerInfo, int diff) {
      if (node.getDiceCount() < 2) {
         return null;
      }

      List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[] { }));
      Collections.sort(adjacentNodes, new Comparator<Node>() {
         @Override
         public int compare(Node o1, Node o2) {
            return Integer.compare(o1.getDiceCount(), o2.getDiceCount());
         }
      });

      for (Node adjacent : adjacentNodes) {
         if (adjacent.getOccupier() == null || !adjacent.getOccupier().equals(playerInfo)) {
            if(adjacent.getDiceCount() - diff >= node.getDiceCount())
               continue;
            return new Move(node, adjacent, "Kapow", MoveType.MOVE);
         }
      }
      return null;

   }



   private ArrayList<ArrayList<Node>> getClusters(List<Node> ownedNodes ) {
      ArrayList<ArrayList<Node>> clusters = new ArrayList<ArrayList<Node>>();
      for (Node node1 : ownedNodes) {
         boolean placed = false;
         for(ArrayList<Node> cluster : clusters) {
            if(cluster.isEmpty()) {
               cluster.add(node1);
               placed = true;
               break;
            }

            for(Node node2 : cluster) {
               if(node1.getAdjacent().contains(node2)) {
                  cluster.add(node1);
                  placed = true;
                  break;
               }
            }
         }

         if(!placed) {
            ArrayList<Node> tmp = new ArrayList<Node>();
            tmp.add(node1);
            clusters.add(tmp);
         }
         // Not placed? Create new list and add?
      }


      Collections.sort(clusters, new Comparator<ArrayList<Node>>() {
         @Override
         public int compare(ArrayList<Node> o1, ArrayList<Node> o2) {
            return Integer.compare(o2.size(), o1.size());
         }
      });


      return clusters;
   }


}