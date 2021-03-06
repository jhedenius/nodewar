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

/**
 * Created by legeryd on 25/03/14.
 */
public class PaBot implements PlayerController {

    @Override
    public Move getNextMove(Game gameState) {
        PlayerInfo playerInfo = gameState.getCurrentPlayer();
        List<Node> ownedNodes = Arrays.asList(GameStateUtils.getPlayerNodes(playerInfo, gameState).toArray(new Node[]{}));

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

            List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[]{}));
            Collections.sort(adjacentNodes, new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return Integer.compare(o1.getDiceCount(), o2.getDiceCount());
                }
            });

            for (Node adjacent : adjacentNodes) {
                if (!adjacent.getOccupier().equals(playerInfo) &&adjacent.getDiceCount() < node.getDiceCount()) {
                    return new Move(node, adjacent, "Attack", MoveType.MOVE);
                }
            }
        }

        return new Move(null, null, "I'm done!", MoveType.DONE);
    }
}