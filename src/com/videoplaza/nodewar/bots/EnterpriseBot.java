package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class EnterpriseBot implements PlayerController{

   public static void main (String... args) {
      new Dice().printMatrix();
   }

   private static final Dice diceRollHelper = new Dice();

   @Override
   public Move getNextMove(Game gameState) {
      /*
      För varje possible Move parent:
         Beräkna children vinst/förlust som GameState, v/l
         Mät värdet för både v och l:
            Hur många noder har vi?
            Hur stort är vårt största territorium?
            Vad är den totala differensen mellan tärningar i vår yttre kant?
            Hur “runt” är vårt territorium? (antal grannar per nod?)

         Ge move parent score = score(v) - score(l)

         Sortera alla moves
         returnera det bästa OM det är större än 0
       */

      List<Move> possibleMoves = getPossibleMoves(gameState);

      List<WeightedMove> weightedMoves = new ArrayList<>();

      for (Move move : possibleMoves) {
         WinLossPair winLossPair = new WinLossPair(gameState, move);
         double moveScore = winLossPair.getScoreDifference();
         weightedMoves.add(new WeightedMove(move, moveScore));
      }

      weightedMoves.sort(null);
§
      boolean hasProfitableMove = !weightedMoves.isEmpty() && weightedMoves.get(0).score >= measureScore(gameState);
      if (hasProfitableMove)
         return weightedMoves.get(0).move;

      return new Move(null, null, "Can't move", MoveType.DONE);
   }

   private List<Move> getPossibleMoves(Game gameState) {
      PlayerInfo selfPlayer = gameState.getCurrentPlayer();
      List<Node> ownedNodes = getOurNodes(gameState);
      List<Move> possibleMoves = new ArrayList<>();

      for (Node owned : ownedNodes) {
         Set<Node> adjacentNodes = owned.getAdjacent();
         for (Node adjacent : adjacentNodes) {
            boolean isEnemyNode = !adjacent.getOccupier().equals(selfPlayer);
            if (isEnemyNode)
               possibleMoves.add(new Move(owned, adjacent, "Attack!", MoveType.MOVE));
         }
      }
      return possibleMoves;
   }

   private static class WinLossPair {
      final Game lossState;
      final Game winState;
      final int ourDices;
      final int opponentDices;

      WinLossPair (Game parent, Move move) {
         Game loss = parent.copy();
         {
            Node from = new Node(loss, move.getFrom());
            from.setDiceCount(1);
         }

         Game win = parent.copy();
         {
            Node from = new Node(win, move.getFrom());
            Node to = new Node(win, move.getTo());
            ourDices = from.getDiceCount();
            opponentDices = to.getDiceCount();
            to.setDiceCount(from.getDiceCount() - 1);
            to.setOccupier(parent.getCurrentPlayer());
            from.setDiceCount(1);
         }
         lossState = loss;
         winState = win;
      }

      double getScoreDifference () {
         double winChance = diceRollHelper.getChanceOfWinning(ourDices, opponentDices);
         return measureScore (winState) * winChance + measureScore(lossState) * (1.0 - winChance);
      }

   }

   private static double measureScore(Game state) {
      int outerRimDiceDifference = 0;
      PlayerInfo selfPlayer = state.getCurrentPlayer();
      for (Node outerRimNode : getNodesWithAdjacentOpponent(state)) {
         outerRimDiceDifference += outerRimNode.getDiceCount();

         int maxOpponentDiceCount = 0;
         for (Node adjacent : outerRimNode.getAdjacent()) {
            boolean isEnemyNode = !adjacent.getOccupier().equals(selfPlayer);
            if (isEnemyNode && adjacent.getDiceCount() > maxOpponentDiceCount)
               maxOpponentDiceCount = adjacent.getDiceCount();
         }
         outerRimDiceDifference -= maxOpponentDiceCount;
      }

      return outerRimDiceDifference + getOurNodes(state).size() * 20 + new Random().nextInt(10); // en första primitiv
   }

   private static List<Node> getNodesWithAdjacentOpponent (Game state) {
      List<Node> nodesWithAdjacentOpponents = new ArrayList<>();
      List<Node> ownedNodes = getOurNodes(state);
      PlayerInfo selfPlayer = state.getCurrentPlayer();

      for (Node owned : ownedNodes) {
         Set<Node> adjacentNodes = owned.getAdjacent();
         for (Node adjacent : adjacentNodes) {
            boolean isEnemyNode = !adjacent.getOccupier().equals(selfPlayer);
            if (isEnemyNode) {
               adjacentNodes.add(owned);
               break;
            }
         }
      }
      return nodesWithAdjacentOpponents;
   }

   private static List<Node> getOurNodes (Game gameState) {
      PlayerInfo selfPlayer = gameState.getCurrentPlayer();
      return new ArrayList<>(GameStateUtils.getPlayerNodes(selfPlayer, gameState));
   }

   private static List<Node> getAdjacentOpponentNodes (Game state) {
      List<Node> adjacentOpponentNodes = new ArrayList<>();
      PlayerInfo selfPlayer = state.getCurrentPlayer();
      List<Node> ownedNodes = getOurNodes(state);

      for (Node owned : ownedNodes) {
         Set<Node> adjacentNodes = owned.getAdjacent();
         for (Node adjacent : adjacentNodes) {
            boolean isEnemyNode = !adjacent.getOccupier().equals(selfPlayer);
            if (isEnemyNode)
               adjacentOpponentNodes.add(adjacent);
         }
      }
      return adjacentOpponentNodes;
   }

   private static class Dice {
      final int dices = 8;
      final int sums = 48;
      final double[][] diceMatrix = new double[dices][sums];

      Dice() {
         for (int dice = 1; dice <= 6; dice++) {
            diceMatrix[0][dice-1] = 1.0/6;
         }

         // mega magic loops, Lilkaer has got this :D
         for (int dice = 2; dice <= dices; dice++) {
            for (int scoreSum = dice; scoreSum <= sums; scoreSum++) {
               for (int roll = 1; roll <= 6; roll++) {
                  if(scoreSum - roll > 0) {
                     double previous = diceMatrix[dice - 2][scoreSum - roll - 1];
                     diceMatrix[dice - 1][scoreSum - 1] += previous / 6;
                  }
               }
            }
         }
      }

      void printMatrix () {
         StringBuilder s = new StringBuilder();
         for (int dice = 0; dice < dices; dice++) {
            for (int sum = 0; sum < sums; sum++) {
               s.append(diceMatrix[dice][sum]).append(' ');
            }
            s.append('\n');
         }

         System.out.print(s);
      }

      double getChanceOfWinning (int ourDices, int opponentDices) {
         double slk = 0, sum = 0;
         for( int ourscore = 2; ourscore <= 6*ourDices; ++ourscore){
            sum += diceMatrix[opponentDices-1][ourscore-1];
            slk += diceMatrix[ourDices-1][ourscore-1] * sum;
         }
         return slk;
      }
   }

   private static class WeightedMove implements Comparable<WeightedMove> {

      final Move move;
      final double score;

      WeightedMove(Move move, double score) {
         this.move = move;
         this.score = score;
      }

      @Override
      public int compareTo(WeightedMove o) {
         return -Double.compare(score, o.score);
      }
   }
}
