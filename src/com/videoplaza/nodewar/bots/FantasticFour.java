package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.*;

public class FantasticFour implements PlayerController {

   public class MoveInternal {
      public static final int UNSET = -4711;
      public Node source;
      public Node target;
      private int _score;

      public MoveInternal(Node _source, Node _target) {
         source = _source;
         target = _target;
         _score = UNSET;
      }

      public int getScore() {
         if (_score != UNSET) return _score;
         final int EXTRA_DICE_SCORE = 20;
         final int EXTRA_ENEMY_RISK = -2;
         final int EXTRA_NEIGHBOUR_RISK = -1;

         int score = 20;

         int countAttackingDice = source.getDiceCount() - 1;
         int countDefendingDice = target.getDiceCount();

//            boolean targetIsAlone = true;
//            for (Node nn : target.getAdjacent())
//            {
//                if (nn.getOccupier() != null && nn.getOccupier() != source.getOccupier())
//                    targetIsAlone = false;
//            }

         if (countAttackingDice < countDefendingDice-1)
            return -1;
         if (countDefendingDice-1 == countAttackingDice &&
            countAttackingDice >= 3)
            return -1;

         score += (countAttackingDice - countDefendingDice) * EXTRA_DICE_SCORE;

         int enemies = 0, neighbours = 0;
         for (Node n : target.getAdjacent())
         {
            if (n.getOccupier() != null && n.getOccupier() != source.getOccupier())
               ++enemies;
            ++neighbours;
         }

         score += enemies * EXTRA_ENEMY_RISK;
         score += neighbours * EXTRA_NEIGHBOUR_RISK;

         _score = score;
         return score;
      }

      public Move toMove() {
         return new Move(source, target, "studs14yolo", MoveType.MOVE);
      }
   }

   public class Island
   {
      public Set<Node> zones = new HashSet<>();
      public int color;
      public int size;
      public int tension; // Own border troops - enemy border troops.

   }

   @Override
   public Move getNextMove(Game gameState) {

      PlayerInfo self = gameState.getCurrentPlayer();
      List<Node> ownedNodes = Arrays.asList(GameStateUtils.getPlayerNodes(self, gameState).toArray(new Node[]{}));

      List<MoveInternal> moves = new ArrayList<MoveInternal>();

      //
      // Find largest island.
      //
      ArrayList<Island> islands = new ArrayList<>();
      int curColor = 0;
      HashMap<Node, Integer> map = new HashMap<>();
      for (Node start : ownedNodes)
      {
         if (map.containsKey(start)) continue;
         Island isl = new Island();
         map.put(start, curColor);
         isl.color = curColor;
         isl.size = 1;
         Stack<Node> dfs = new Stack<>();
         dfs.add(start);
         while (!dfs.empty())
         {
            Node n = dfs.pop();
            isl.zones.add(n);
            for (Node nn : n.getAdjacent())
            {
               if (map.containsKey(nn) || nn.getOccupier() != self) continue;
               map.put(nn, curColor);
               dfs.push(nn);
               ++isl.size;
            }
         }
         islands.add(isl);
         ++curColor;
      }
      int maxsize = 0;
      int maxsize_arg = 0;
      int i = 0;
      for (Island isl : islands)
      {
         int size = isl.size;
         if (size > maxsize)
         {
            maxsize = size;
            maxsize_arg = i;
         }
         ++i;
      }

      //
      // Calculate tension.
      //
      for (Island isl : islands)
      {
         isl.tension = 0;
         Set<Node> vis = new HashSet<>();
         for (Node n : isl.zones)
         {
            int enemies = 0;
            for (Node nn : n.getAdjacent())
            {
               if (nn.getOccupier() != null && nn.getOccupier() != self)
               {
                  ++enemies;
                  if (!vis.contains(nn))
                  {
                     vis.add(nn);
                     isl.tension -= nn.getDiceCount()-1;
                  }
               }
            }
            if (enemies > 0)
               isl.tension += n.getDiceCount();
         }
      }
      int maxtension = Integer.MIN_VALUE;
      int maxtension_arg = 0;
      i = 0;
      for (Island isl : islands)
      {
         if (isl.tension > maxtension)
         {
            maxtension = isl.tension;
            maxtension_arg = i;
         }
         ++i;
      }

      //
      // Find possible moves.
      //
      for (Node node : ownedNodes) {
         if (node.getDiceCount() < 2) continue;

         // Turtle island?
         int color = map.get(node);
         int island_size = islands.get(color).size;
//            if (color != maxsize_arg) continue;
         if (island_size < (int)Math.round(0.9 * maxsize)) continue;
         if (maxsize - island_size >= 3) continue;
//            if (color != maxtension_arg) continue;

         // GO GO GO
         List<Node> adjacentNodes = Arrays.asList(node.getAdjacent().toArray(new Node[]{}));
         for (Node adjacent : adjacentNodes) {
            if (adjacent.getOccupier() == null || !adjacent.getOccupier().equals(self)) {
               moves.add(new MoveInternal(node, adjacent));
            }
         }
      }

      //
      // Sort possible moves and grab best one.
      //
      Collections.sort(moves, new Comparator<MoveInternal>() {
         @Override
         public int compare(MoveInternal o1, MoveInternal o2) {
            return Integer.compare(o2.getScore(), o1.getScore());
         }
      });
      if (moves.size() > 0 && moves.get(0).getScore() >= 0) {
         return moves.get(0).toMove();
      }
      return new Move(null, null, "", MoveType.DONE);
   }
}
