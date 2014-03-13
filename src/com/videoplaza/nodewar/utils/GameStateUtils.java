package com.videoplaza.nodewar.utils;

import com.videoplaza.nodewar.state.GameState;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;

import java.util.HashSet;
import java.util.Set;

public class GameStateUtils {

   public static Set<Node> getPlayerNodes(PlayerInfo player, GameState gameState) {
      Set<Node> ownedNodes = new HashSet<Node>();
      for (Node node : gameState.getNodes().values()) {
         if (node.getOccupier() != null && node.getOccupier().equals(player))
            ownedNodes.add(node);
      }
      return ownedNodes;
   }

}
