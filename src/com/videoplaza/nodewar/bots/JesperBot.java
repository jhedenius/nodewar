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

        //our nodes Strongest first.
        ownedNodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
            }
        });

        Moove bestMove = getBestOneMoveConnect(playerInfo, ownedNodes);
        if (bestMove != null) {
            return new Move(bestMove.getFrom(), bestMove.getTo(), "Attack", MoveType.MOVE);  // try to make a move for each of my own nodes.
        }
        for (Node node : ownedNodes) {
            if (node.getDiceCount() == 8) {

                List<Node> attack = getAttackableNodesSorted(node,playerInfo);
                if(!attack.isEmpty()) return new Move(node, attack.get(0), "Attack", MoveType.MOVE);
            }

            if (node.getDiceCount() < 1) { // only move if we are stronger than 1
                continue;
            }


            // Attacks first adjacent node


            for (Node adjacent : getAttackableNodesSorted(node,playerInfo)) {
                if (!playerInfo.equals(adjacent.getOccupier()) && adjacent.getDiceCount() + 2 < node.getDiceCount()) {
                    return new Move(node, adjacent, "Attack", MoveType.MOVE);
                }
            }

        }
        return new Move(null, null, "Can't move", MoveType.DONE);
    }

    private int getNumberOfPlayers(Game gameState) {
        int counter = 0;
        for (PlayerInfo player : gameState.getPlayers()) {
            if (GameStateUtils.getPlayerNodes(player, gameState).size() > 0) counter++;
        }
        return counter;

    }


    private List<Node> getAttackableNodesSorted(Node node, PlayerInfo player) {

        List<Node> adjacentNodes = new ArrayList<>();
        for(Node other : node.getAdjacent()){
            if(!other.getOccupier().equals(player)) adjacentNodes.add(other);
        }
        adjacentNodes.sort(new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Integer.compare(o1.getDiceCount(), o2.getDiceCount());
            }
        });

        return adjacentNodes;
    }

    private Moove getBestOneMoveConnect(PlayerInfo player, List<Node> nodes) {
        ArrayList<Moove> mooves = new ArrayList<>();
        for (Node node : nodes) {
            if (node.getDiceCount() < 1) { // only move if we are stronger than 1
                continue;
            }

            Set<Node> connectedNodes = getConnectedNodes(player, node, new HashSet<Node>());
            ArrayList<Node> bestNodes = new ArrayList<>();


            for (Node adjacentNode : node.getAdjacent()) {
                if (adjacentNode.getOccupier().equals(player))
                    continue;
                for (Node farAwayNode : adjacentNode.getAdjacent()) {
                    if (farAwayNode.equals(node) && !connectedNodes.contains(farAwayNode))
                        continue;
                    if (farAwayNode.getOccupier().equals(player)) {
                        if (node.getDiceCount() + 2 >= adjacentNode.getDiceCount() || node.getDiceCount() == 8) {
                            mooves.add(new Moove(node, adjacentNode));
                        }
                    }
                }
            }

        }
        if (mooves.isEmpty()) return null;
        Collections.sort(mooves, new Comparator<Moove>() {
            @Override
            public int compare(Moove o1, Moove o2) {
                return Integer.compare(o1.getDiff(), o2.getDiff());
            }
        });
        return mooves.get(0);
    }

    private Set<Node> getConnectedNodes(PlayerInfo player, Node node, Set<Node> visited) {
        visited.add(node);
        for (Node adjacent : node.getAdjacent()) {
            if (adjacent.getOccupier() != null && adjacent.getOccupier().getId().equals(player.getId()) && !(visited.contains(adjacent))) {
                getConnectedNodes(player, adjacent, visited);
            }
        }
        return visited;
    }


    private static int countEnnemyNeighbors(Node node) {
        int count = 0;
        for (Node neighbor : node.getAdjacent()) {
            if (!node.getOccupier().equals(neighbor.getOccupier())) {
                count++;
            }
        }
        return count;
    }

    private class ValueComparator implements Comparator<Node> {
        Node fromNode;

        public ValueComparator(Node node) {
            this.fromNode = node;
        }


        @Override
        public int compare(Node o1, Node o2) {
            if (fromNode.getDiceCount() - o1.getDiceCount() > fromNode.getDiceCount() - o2.getDiceCount()) {
                return 1;
            } else if (fromNode.getDiceCount() - o1.getDiceCount() < fromNode.getDiceCount() - o2.getDiceCount()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private class Moove {
        private Node from;
        private Node to;
        private int diff;

        public Moove(Node from, Node to) {
            this.from = from;
            this.to = to;
            this.diff = from.getDiceCount() - to.getDiceCount();
        }


        public int getDiff() {
            return diff;
        }

        public Node getFrom() {
            return from;
        }

        public Node getTo() {
            return to;
        }
    }
}


