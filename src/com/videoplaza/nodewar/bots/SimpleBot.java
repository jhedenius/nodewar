package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimpleBot implements PlayerController {

   @Override
   public Move getNextMove(Game gameState) {

      PlayerInfo playerInfo = gameState.getCurrentPlayer();

      List<Node> ownedNodes = Arrays.asList(GameStateUtils.getPlayerNodes(playerInfo, gameState).toArray(new Node[] { }));

      Collections.sort(ownedNodes, (o1, o2) -> Integer.compare(o2.getDiceCount(), o1.getDiceCount()));

      for (Node node : ownedNodes) {
         if (node.getDiceCount() < 2) {
            continue;
         }

         List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[] { }));
         Collections.sort(adjacentNodes, (o1, o2) -> Integer.compare(o1.getDiceCount(), o2.getDiceCount()));

         for (Node adjacent : adjacentNodes) {
            if (adjacent.getOccupier() == null || !adjacent.getOccupier().equals(playerInfo)) {
               return new Move(node, adjacent, "Attack", MoveType.MOVE);
            }
         }
      }

      return new Move(null, null, "Can't move", MoveType.DONE);
   }

}
