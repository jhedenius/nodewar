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
import java.util.List;

public class RageBot implements PlayerController {
   @Override
   public Move getNextMove(Game gameState) {
      PlayerInfo playerInfo = gameState.getCurrentPlayer();

      for (int i = 0; i < 20; i++) {
         List<Node> ownedNodes = getOwnedNodes(gameState, playerInfo);

         Collections.sort(ownedNodes, new DiceComparator());

         for (Node node : ownedNodes) {
            if (node.getDiceCount() < 2) {
               continue;
            }

            List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[] { }));
            Collections.sort(adjacentNodes, new DiceComparator());

            for (Node adjacent : adjacentNodes) {
               if (!adjacent.getOccupier().equals(playerInfo)) {
                  return new Move(node, adjacent, "Attack", MoveType.MOVE);
               }
            }
         }
      }

      return new Move(null, null, "Can't move", MoveType.DONE);
   }

   private List<Node> getOwnedNodes(Game gameState, PlayerInfo playerInfo) {
      return Arrays.asList(
         GameStateUtils.getPlayerNodes(playerInfo, gameState).toArray(new Node[] { }));
   }

   private class DiceComparator implements Comparator<Node> {
      @Override
      public int compare(Node o1, Node o2) {
         return Integer.compare(o1.getDiceCount(), o2.getDiceCount());
      }
   }
}
