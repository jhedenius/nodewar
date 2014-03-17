package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.GameState;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimpleBot implements Player {

   @Override
   public Move getNextMove(GameState gameState) {

      PlayerInfo playerInfo = gameState.getCurrentPlayer();

      List<Node> ownedNodes = Arrays.asList(GameStateUtils.getPlayerNodes(playerInfo, gameState).toArray(new Node[] { }));

      Collections.sort(ownedNodes, new Comparator<Node>() {
         @Override
         public int compare(Node o1, Node o2) {
            return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
         }
      });

      for (Node node : ownedNodes) {
         if (node.getDiceCount() == 0) {
            continue;
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
               return new Move(node, adjacent, "Attack", MoveType.MOVE);
            }
         }
      }

      return new Move(null, null, "Can't move", MoveType.DONE);
   }

}
