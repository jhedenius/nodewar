package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.*;

public class WinBot implements PlayerController {
    int lastSize = 0;

    @Override
    public Move getNextMove(Game gameState) {
        Queue<CoolMove> moves = new PriorityQueue<CoolMove>();

        PlayerInfo playerInfo = gameState.getCurrentPlayer();

        List<Node> ownedNodes = new ArrayList<>(GameStateUtils.getPlayerNodes(playerInfo, gameState));
        List<List<Node>> regions = getRegions(ownedNodes);

        regions.sort(new Comparator<List<Node>>() {
            @Override
            public int compare(List<Node> o1, List<Node> o2) {
                return Integer.compare(o2.size(), o1.size());
            }
        });

        for (List<Node> region : regions) {
            for(Node node : region) {
                for(Node neighbour : node.getAdjacent()) {
                    if(neighbour.getOccupier().equals(playerInfo)) {
                        continue;
                    }
                    for(Node maybeNodeInOtherRegion : neighbour.getAdjacent()) {
                        if(!maybeNodeInOtherRegion.getOccupier().equals(playerInfo)) {
                            continue;
                        }
                        if(region.contains(maybeNodeInOtherRegion)) {
                            continue;
                        }

                        Move m = new Move(node, neighbour, "Attack", MoveType.MOVE);
                        moves.add(new CoolMove(15 + (node.getDiceCount() - neighbour.getDiceCount()), m));
                    }
                }
            }
        }

        for (List<Node> region : regions) {
            getBestMove(moves, region);
            /*if (move != null) {
                int size = region.size();
                if (lastSize <= 2 || size > lastSize / 2) {
                    lastSize = size;
                    return move;
                }
            }*/
        }
        CoolMove move = moves.poll();
        if(move != null) {
            return move.move;
        }

        lastSize = 0;
        return new Move(null, null, "Can't move", MoveType.DONE);
    }

    private void getBestMove(Queue<CoolMove> moves, List<Node> nodes) {
        PlayerInfo playerInfo = nodes.get(0).getOccupier();

        Collections.sort(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
            }
        });

        for (Node node : nodes) {
            if (node.getDiceCount() < 2) {
                continue;
            }

            List<Node> adjacentNodes = new ArrayList<>(node.getAdjacent());
            Collections.sort(adjacentNodes, new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return Integer.compare(o1.getDiceCount(), o2.getDiceCount());
                }
            });

            for (Node adjacent : adjacentNodes) {
                if (!playerInfo.equals(adjacent.getOccupier()) && adjacent.getDiceCount() + 2 <= node.getDiceCount()) {
                    Move m = new Move(node, adjacent, "Attack", MoveType.MOVE);
                    moves.add(new CoolMove(1 + (node.getDiceCount() - adjacent.getDiceCount()), m));
                }

                boolean allAreFriendly = true;
                for(Node adjacentNode : adjacent.getAdjacent()) {
                    allAreFriendly = allAreFriendly && adjacentNode.getOccupier().equals(playerInfo);
                }

                if(allAreFriendly) {
                    Move m = new Move(node, adjacent, "Attack", MoveType.MOVE);
                    moves.add(new CoolMove(-1000 + (node.getDiceCount() - adjacent.getDiceCount()), m));
                }
            }
        }
    }

    private List<List<Node>> getRegions(List<Node> nodes) {
        Set<Node> visited = new LinkedHashSet<>();
        List<List<Node>> regions = new ArrayList<>();

        for (Node node : nodes) {
            if (visited.contains(node)) continue;

            List<Node> region = new ArrayList<>();
            visit(node, region, visited);

            regions.add(region);
        }

        return regions;
    }

    private void visit(Node node, List<Node> region, Set<Node> visited) {
        if (visited.contains(node)) return;

        visited.add(node);
        region.add(node);

        for (Node adjacent : node.getAdjacent()) {
            if (adjacent.getOccupier().equals(node.getOccupier())) {
                visit(adjacent, region, visited);
            }
        }
    }

    private static final class CoolMove implements Comparable<CoolMove> {
        int score;
        Move move;

        private CoolMove(int score, Move move) {
            this.score = score;
            this.move = move;
        }

        @Override
        public int compareTo(CoolMove other) {
            return Integer.compare(other.score, score);
        }
    }
}
