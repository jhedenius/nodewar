package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class GameEngine {
   private Game gameState;
   private final Random random;

   public GameEngine(Game gameState, long randomSeed) {
      this.gameState = gameState;
      random = new Random(randomSeed);
   }

   public Map<String, Integer> runGame() {
      say("Starting game", null);
      Set<String> eliminatedPlayers = new HashSet<>();
      HashMap<String, Integer> scores = new HashMap<>();

      int nextScore = 0;
      while (!isGameOver()) {
         doTurn();
         boolean elim = false;
         for (PlayerInfo player : gameState.getPlayers()) {
            if (!eliminatedPlayers.contains(player.name) && !playerRemains(player)) {
               eliminatedPlayers.add(player.name);
               scores.put(player.name, nextScore);
               elim = true;
            }
         }
         if (elim) nextScore++;
         gameState.setCurrentTurn(gameState.getCurrentTurn() + 1);
      }

      List<Pair> remainingPlayers = new ArrayList<>();
      for (PlayerInfo player : gameState.getPlayers()) {
         if (playerRemains(player)) {
            remainingPlayers.add(new Pair(player.name, GameStateUtils.getPlayerNodes(player, gameState).size()));
         }
      }
      Collections.sort(remainingPlayers, new Comparator<Pair>() {
         @Override
         public int compare(Pair o1, Pair o2) {
            return o1.territories - o2.territories;
         }
      });
      int prev = -1;
      nextScore--;
      for (Pair pair : remainingPlayers) {
         scores.put(pair.name, pair.territories == prev ? nextScore : ++nextScore);
         prev = pair.territories;
      }

      say("Game over. " + formatScoresHtml(scores), null);
      return scores;
   }

   public static String formatScoresHtml(Map<String, Integer> scores) {
      List<Pair> list = sortScores(scores);
      StringBuilder sb = new StringBuilder();
      sb.append("<ol>");
      for (Pair pair : list) {
         sb.append("<li>").append(pair.name).append(": ").append(pair.territories).append(" pts</li>");
      }
      sb.append("</ol>");

      return sb.toString();
   }

   public static String formatScoresText(Map<String, Integer> scores) {
      List<Pair> list = sortScores(scores);
      StringBuilder sb = new StringBuilder("\n");
      for (int i = 1; i <= list.size(); i++) {
         Pair pair = list.get(i - 1);
         sb.append(i + ". ").append(pair.name).append(": ").append(pair.territories).append(" pts\n");
      }

      return sb.toString();
   }

   public static List<Pair> sortScores(Map<String, Integer> scores) {
      List<Pair> list = new ArrayList<>();
      for (Map.Entry<String, Integer> entry : scores.entrySet()) {
         list.add(new Pair(entry.getKey(), entry.getValue()));
      }
      Collections.sort(list, new Comparator<Pair>() {
         @Override
         public int compare(Pair o1, Pair o2) {
            return o2.territories - o1.territories;
         }
      });
      return list;
   }

   private PlayerInfo getWinner() {
      if (!isGameOver()) {
         return null;
      }

      Score winningScore = GameStateUtils.getLeader(gameState);

      System.out.println("Winning score: " + winningScore);

      return winningScore.getPlayerInfo();
   }

   private void doTurn() {
      for (PlayerInfo player : gameState.getPlayers()) {
         if (!canMove(player)) {
            continue;
         }
         gameState.setCurrentPlayer(player);
         Move playerMove = player.getPlayerImplementation().getNextMove(gameState);
         int i = 0;
         while (playerMove != null && playerMove.getMoveType() != MoveType.DONE && i++ < 1000) {
            gameState.apply(playerMove);
            applyMove(player, playerMove);
            playerMove = player.getPlayerImplementation().getNextMove(gameState);
         }
         playerMove.setReinforcements(GameStateUtils.reinforce(player, gameState, random));
         gameState.apply(playerMove);
         say(playerMove.getComment(), player);
      }
   }

   private void applyMove(PlayerInfo player, Move move) {
      if (!validMove(player, move)) {
         return;
      }

      move.setAttackerRoll(rollDice(move.getFromNode().getDiceCount()));
      move.setDefenderRoll(rollDice(move.getToNode().getDiceCount()));
      if (winBattle(move)) {
         applyWin(move);
      } else {
         applyLoss(move);
      }

   }

   private boolean winBattle(Move move) {
      return sum(move.getAttackerRoll()) > sum(move.getDefenderRoll());
   }

   private void applyWin(Move move) {
      int diceCountFrom = move.getFromNode().getDiceCount();
      move.getToNode().setDiceCount(diceCountFrom - 1);
      move.getToNode().setOccupier(move.getFromNode().getOccupier());
      move.getFromNode().setDiceCount(1);
      //say("PlayerController " + move.getFromNode().getOccupier().getName() + " occupies " + move.getToNode().getName(), null);
   }

   private void applyLoss(Move move) {
      move.getFromNode().setDiceCount(1);
      //say("PlayerController " + move.getFromNode().getOccupier().getName() + " failed to occupy " + move.getToNode().getName(), null);
   }

   private void say(String message, PlayerInfo playerInfo) {
      if (message == null || message.length() == 0) {
         return;
      }

      gameState.apply(new Move(null, null, playerInfo == null ? message : playerInfo + ": " + message, MoveType.COMMENT));

      try {
         //Thread.sleep(500);
      } catch (Exception e) {

      }
      if (playerInfo == null) {
         System.out.println("Game host: " + message);
      } else {
         //System.out.println(playerInfo.getName() + ": " + message);
      }
   }

   private int sum(List<Integer> is) {
      int sum = 0;
      for (int i : is) {
         sum += i;
      }
      return sum;
   }

   private List<Integer> rollDice(int diceCount) {
      List<Integer> result = new ArrayList<Integer>();
      for (int i = 0; i < diceCount; i++) {
         result.add(random.nextInt(6) + 1);
      }
      return result;
   }

   private boolean validMove(PlayerInfo player, Move move) {
      if (move == null ||
         move.getFromNode() == null ||
         move.getToNode() == null) {
         return false;
      }

      Node fromNode = move.getFromNode();
      Node toNode = move.getToNode();

      // player must own fromNode
      if ((fromNode.getOccupier() == null) ||
         !fromNode.getOccupier().getId().equals(player.getId())) {
         return false;
      }

      // player must not own toNode already
      if (toNode.getOccupier() != null && toNode.getOccupier().getId().equals(player.getId())) {
         return false;
      }

      // player must have at least two dice on fromNode
      if (fromNode.getDiceCount() < 2) {
         return false;
      }

      return true;
   }

   private boolean isGameOver() {

      if (gameState.getCurrentTurn() > gameState.getMaxTurns()) {
         System.out.println("Max turns reached, game over");
         return true;
      }

      int players = 0;
      for (PlayerInfo player : gameState.getPlayers()) {
         if (playerRemains(player)) {
            players++;
         }
      }
      return players <= 1;
   }

   private boolean playerRemains(PlayerInfo player) {
      return !GameStateUtils.getPlayerNodes(player, gameState).isEmpty();
   }

   private boolean canMove(PlayerInfo player) {
      Set<Node> nodes = GameStateUtils.getPlayerNodes(player, gameState);
      for (Node node : nodes) {
         if (node.getDiceCount() > 1) {
            for (Node adjacent : node.getAdjacent()) {
               if (adjacent.getOccupier() != player) {
                  return true;
               }
            }
         }
      }
      return false;
   }

   private static class Pair {
      String name;
      int territories;

      private Pair(String name, int territories) {
         this.name = name;
         this.territories = territories;
      }
   }
}
