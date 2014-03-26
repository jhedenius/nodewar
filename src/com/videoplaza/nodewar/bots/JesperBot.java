package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.*;

public class JesperBot implements PlayerController {

    @Override
    public Move getNextMove(Game gameState) {

        PlayerInfo playerInfo = gameState.getCurrentPlayer();

        List<Node> ownedNodes = new ArrayList<>(GameStateUtils.getPlayerNodes(playerInfo, gameState));
        ownedNodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
            }
        });


        // try to make a move for each of my own nodes.
        for (Node node : ownedNodes) {
            if (node.getDiceCount() < 1) { // only move if we are stronger than 1
                continue;
            }


            //the neighbors, weakest first.
            List<Node> adjacentNodes = new ArrayList<>(node.getAdjacent());
            adjacentNodes.sort(new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return Integer.compare(o1.getDiceCount(), o2.getDiceCount());
                }
            });

            // Attacks first adjacent node
            for (Node adjacent : adjacentNodes) {
                if (!playerInfo.equals(adjacent.getOccupier()) && adjacent.getDiceCount() + 3 < node.getDiceCount() ){
                    return new Move(node, adjacent, "Attack", MoveType.MOVE);
                }
            }
        }
        return new Move(null, null, "Can't move", MoveType.DONE);
    }

    private static int countEnnemyNeighbors(Node node){
        int count = 0;
        for (Node neighbor : node.getAdjacent()){
            if (!node.getOccupier().equals(neighbor.getOccupier())){
                count++;
            }
        }
        return count;
    }
}


