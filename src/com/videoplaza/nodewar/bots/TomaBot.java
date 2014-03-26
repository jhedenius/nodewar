package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TomaBot implements PlayerController {

   boolean print = true;
   int waitLimit = 4;
   int waitCounter = 0;



   @Override
   public Move getNextMove(Game gameState) {
      waitCounter = gameState.getCurrentTurn();

      PlayerInfo playerInfo = gameState.getCurrentPlayer();

      List<Node> ownedNodes = Arrays.asList(GameStateUtils.getPlayerNodes(playerInfo, gameState).toArray(new Node[] { }));

      Map<PlayerInfo, List<Node>> playerNodes = new HashMap<PlayerInfo, List<Node>>();
      for(PlayerInfo other : gameState.getPlayers()){
         List<Node> otherNode = Arrays.asList(GameStateUtils.getPlayerNodes(other, gameState).toArray(new Node[] { }));
         playerNodes.put(other, otherNode);
      }

      getChunks(playerInfo, ownedNodes);


      Collections.sort(ownedNodes, new Comparator<Node>() {
         @Override
         public int compare(Node o1, Node o2) {
            return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
         }
      });


           /*
      NodeWrapper best = new NodeWrapper(null, Integer.MAX_VALUE);
      Node start = null;
      best.weight = Integer.MAX_VALUE;
      List<Set<Node>> chunks = getChunks(playerInfo, ownedNodes);
      if(chunks.size() >= 2){
         for(Set<Node> chunk : chunks){
            for(Node node : chunk){
               for(Set<Node> otherChunk : chunks){
                  if(otherChunk.containsAll(chunk)) continue;
                  NodeWrapper plan = getPath(playerInfo, node, chunks.get(1));
                  if(plan.weight < best.weight){
                     best = plan;
                     start = null;
                  }
               }
            }
         }
      }

      if(best.parent != null){
         if(start.getDiceCount() - best.length > best.weight){
            return new Move(start, best.parent, "CONNECTING", MoveType.MOVE);
         }
      }

           */



      for (Node node : ownedNodes) {




         if (node.getDiceCount() < 2) {
            continue;
         }

         if(waitCounter > waitLimit) {

            if (node.getDiceCount() >= 2) {
               List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[] { }));
               Collections.sort(adjacentNodes, new Comparator<Node>() {
                  @Override
                  public int compare(Node o1, Node o2) {
                     return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
                  }
               });

               for (Node adjacent : adjacentNodes) {
                  if ((adjacent.getOccupier() == null || !adjacent.getOccupier().equals(playerInfo)) && adjacent.getDiceCount() <= node.getDiceCount()) {
                     return new Move(node, adjacent, "Attack", MoveType.MOVE);
                  }
               }
            }
         } else {


            if(node.getDiceCount() >= 7){
                  List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[] { }));
                  Collections.sort(adjacentNodes, new Comparator<Node>() {
                     @Override
                     public int compare(Node o1, Node o2) {
                        return Integer.compare(o2.getDiceCount(), o1.getDiceCount());
                     }
                  });

                  for (Node adjacent : adjacentNodes) {
                     if ((adjacent.getOccupier() == null || !adjacent.getOccupier().equals(playerInfo)) && adjacent.getDiceCount() <= node.getDiceCount()) {
                        return new Move(node, adjacent, "Attack", MoveType.MOVE);
                     }
                  }
               }
         }
      }

      return new Move(null, null, "Can't move", MoveType.DONE);
   }

   public List<Set<Node>> getChunks(PlayerInfo player, List<Node> nodes){
      Set<Node> visited = new HashSet<Node>();
      List<Set<Node>> chunks = new ArrayList<Set<Node>>();

      ArrayDeque<Node> queue = new ArrayDeque<Node>();

      for(int i = 0; i < nodes.size(); i++){
         if(visited.contains(nodes.get(i))) continue;
         Set<Node> chunk = new HashSet<Node>();
         queue.add(nodes.get(i));

         while(!queue.isEmpty()){

            Node node = queue.removeFirst();

            if(visited.contains(node)) continue;

            visited.add(node);
            for(Node neighbour : node.getAdjacent()){
               if(node.getOccupier() != null && node.getOccupier().equals(player))
                  queue.add(neighbour);
            }
         }

         chunks.add(chunk);

      }

      // Sort biggest to smallest
      Collections.sort(chunks, new Comparator<Set<Node>>() {
         @Override
         public int compare(Set<Node> o1, Set<Node> o2) {
            return Integer.compare(o2.size(), o1.size());
         }
      });

      return chunks;
   }

   public NodeWrapper getPath(PlayerInfo player, Node start, Set<Node> chunk){


         ArrayDeque<NodeWrapper> path = new ArrayDeque<NodeWrapper>();

         ArrayDeque<Node> queue = new ArrayDeque<Node>();
         queue.add(start);

         Set<Node> visited = new HashSet<Node>();
         visited.add(start);
         while(!queue.isEmpty()){
            Node next = queue.removeFirst();
            if(visited.contains(next)) continue;
            visited.add(next);

            if(next.equals(start)){
               path.addLast(new NodeWrapper(null, 0));
            } else {
               path.addLast(new NodeWrapper(next, path.getLast().length));
            }

            if(path.getLast().length > 3){
               return null;
            }

            if(chunk.contains(next)){
               break;
            }

            for(Node neighbour : next.getAdjacent()) {
               if(neighbour.getOccupier() == null || neighbour.getOccupier().equals(player)) continue;
               queue.addLast(neighbour);
            }

         }
      Node node = null;
      int weight = 0;
      int length = 0;
      if(path.size() > 0){
         length = path.getLast().length;
      }

         while(path.getLast().parent != null){
            node = path.removeLast().parent;
            weight += node.getDiceCount();

         }
         NodeWrapper result = new NodeWrapper(node, length);
         result.weight = weight;
         return result;


   }

   class NodeWrapper {

      Node parent;
      int length = 0;
      int weight = 0;

      public NodeWrapper(Node parent, int length){
         this.parent = parent;
         this.length = length + 1;
      }

   }

}
