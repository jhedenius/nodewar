package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameEngine {
   private Game gameState;
   private final Random random;

   public GameEngine(Game gameState, long randomSeed) {
      this.gameState = gameState;
      random = new Random(randomSeed);
   }

   public void startGame() {
      say("Starting game", null);
      while (!isGameOver()) {
         doTurn();
         gameState.setCurrentTurn(gameState.getCurrentTurn() + 1);
      }
      say("Game over. Winner is " + getWinner().getName(), null);
   }

   private PlayerInfo getWinner() {
      if (!isGameOver())
         return null;

      Score winningScore = GameStateUtils.getLeader(gameState);

      System.out.println("Winning score: " + winningScore);

      return winningScore.getPlayerInfo();
   }

   private void doTurn() {
      for (PlayerInfo player : gameState.getPlayers()) {
         if(!canMove(player))
            continue;
         gameState.setCurrentPlayer(player);
         Move playerMove = player.getPlayerImplementation().getNextMove(gameState);
         while (playerMove != null && playerMove.getMoveType() != MoveType.DONE) {
            gameState.apply(playerMove);
            applyMove(player, playerMove);
            playerMove = player.getPlayerImplementation().getNextMove(gameState);
         }
         playerMove.setReinforcements(GameStateUtils.reinforce(player, gameState, random));
         gameState.apply(playerMove);

      }
   }

   private void applyMove(PlayerInfo player, Move move) {
      if (!validMove(player, move))
         return;

      say(move.getComment(), player);


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
      say("PlayerController " + move.getFromNode().getOccupier().getName() + " occupies " + move.getToNode().getName(), null);
   }

   private void applyLoss(Move move) {
      move.getFromNode().setDiceCount(1);
      say("PlayerController " + move.getFromNode().getOccupier().getName() + " failed to occupy " + move.getToNode().getName(), null);
   }

   private void say(String message, PlayerInfo playerInfo) {
      try {
         //Thread.sleep(500);
      } catch (Exception e) {

      }
      if (playerInfo == null)
         System.out.println("Game host: " + message);
      else
         System.out.println(playerInfo.getName() + ": " + message);
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
         move.getToNode() == null)
         return false;

      Node fromNode = move.getFromNode();
      Node toNode = move.getToNode();

      // player must own fromNode
      if ((fromNode.getOccupier() == null) ||
         !fromNode.getOccupier().getId().equals(player.getId()))
         return false;

      // player must not own toNode already
      if (toNode.getOccupier() != null && toNode.getOccupier().getId().equals(player.getId()))
         return false;

      // player must have at least two dice on fromNode
      if (fromNode.getDiceCount() < 2)
         return false;

      return true;
   }

   private boolean isGameOver() {

      if (gameState.getCurrentTurn() > gameState.getMaxTurns()) {
         System.out.println("Max turns reached, game over");
         return true;
      }

      for (PlayerInfo player : gameState.getPlayers()) {
         if (canMove(player)) {
            return false;
         }
      }
      System.out.println("No players can move, game over");
      return true;
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
}
