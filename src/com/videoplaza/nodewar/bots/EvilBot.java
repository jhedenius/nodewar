package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author blake
 */
public class EvilBot implements PlayerController {
   @Override
   public Move getNextMove(Game gameState) {
      PlayerInfo playerInfo = gameState.getCurrentPlayer();

      Set<Node> ownedNodes = new TreeSet<>((o1, o2) -> Integer.compare(o2.getDiceCount(), o1.getDiceCount()));
      ownedNodes.addAll(GameStateUtils.getPlayerNodes(playerInfo, gameState));

      HashMap<Integer, Integer> numberOfDices = new HashMap<>();

      for (PlayerInfo player : gameState.getPlayers()) {
         int num = 0;
         for (Node node : GameStateUtils.getPlayerNodes(playerInfo, gameState)) {
            num += node.getDiceCount();
         }
         numberOfDices.put(player.getId(), num);
      }

      int kamikaze = 1;
      int counter = 0;
      int toAttack = ownedNodes.size() / 2;
      for (Node node : ownedNodes) {
         if (counter >= toAttack) {
            continue;
         }

         if (node.getDiceCount() < 3) {
            continue;
         }

         Set<Node> adjacentNodes = new TreeSet<>((o1, o2) -> Integer.compare(o1.getDiceCount(), o2.getDiceCount()));
         adjacentNodes.addAll(node.getAdjacent());
/*
         if (gameState.getCurrentTurn() % 3 == 0 && !adjacentNodes.isEmpty() && kamikaze != 0) {
            kamikaze--;
            return new Move(node, adjacentNodes.iterator().next(), "Attack", MoveType.MOVE);
         }
*/
         HashSet<Node> visited = new HashSet<>();
         Queue<Node> queue = new LinkedList<>();
         queue.add(node);
         int sizeOfBlob = 0;
         while (!queue.isEmpty()) {
            Node adjacent = queue.poll();
            sizeOfBlob++;
            visited.add(adjacent);
            for (Node next : adjacent.getAdjacent()) {
               if (!visited.contains(next) && next.getOccupier().equals(playerInfo)) {
                  queue.add(next);
               }
            }
         }

         if (sizeOfBlob <= 2 && gameState.currentTurn > 2) {
            for (Node adjacent : adjacentNodes) {
               if (adjacent.getOccupier() == null || !adjacent.getOccupier().equals(playerInfo)) {
                  counter++;
                  //if (node.getDiceCount() >= adjacent.getDiceCount()) {
                  return new Move(node, adjacent, "Attack", MoveType.MOVE);
                  //}
               }
            }
         }

         for (Node adjacent : adjacentNodes) {
            if (adjacent.getOccupier() == null || !adjacent.getOccupier().equals(playerInfo)) {
               if (numberOfDices.get(adjacent.getOccupier().getId()) <= 8 && numberOfDices.get(playerInfo.getId()) >= 16) {
                  if (node.getDiceCount() > adjacent.getDiceCount()) {
                     counter++;
                     return new Move(node, adjacent, "Attack", MoveType.MOVE);
                  }
               }

               for (Node nextNode : adjacent.getAdjacent()) {
                  if (!adjacentNodes.contains(nextNode)) {
                     if (nextNode.getOccupier().equals(playerInfo)) {
                        if (node.getDiceCount() > adjacent.getDiceCount()) {
                           counter++;
                           return new Move(node, adjacent, "Attack", MoveType.MOVE);
                        }
                     }
                  }
               }

               if (node.getDiceCount() - adjacent.getDiceCount() >= 4) {
                  counter++;
                  return new Move(node, adjacent, "Attack", MoveType.MOVE);
               }
            }
         }
      }

      return new Move(null, null, "Can't move", MoveType.DONE);
   }
}
