package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.GameState;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import com.videoplaza.nodewar.utils.GameStateUtils;

import java.util.Random;

public class GameEngine {
   private GameState gameState;
   private Random random;

   public GameEngine(GameState gameState, long randomSeed) {
      this.gameState = gameState;
      random = new Random(randomSeed);
   }

   public void startGame() {
      say("Starting game", null);
      while (!isGameOver()) {
         doRound();
      }
      say("Game over. Winner is " + getWinner().getName(), null);
   }

   private PlayerInfo getWinner() {
      if (!isGameOver())
         return null;

      for (PlayerInfo player : gameState.getPlayers()) {
         if (canMove(player)) {
            return player;
         }
      }

      return null;
   }

   private void doRound() {
      for (PlayerInfo player : gameState.getPlayers()) {
         gameState.setCurrentPlayer(player);
         //System.out.println("Player " + player.getName() + " move.");
         Move playerMove = player.getPlayerImplementation().getNextMove(gameState);
         applyMove(player, playerMove);
      }
   }

   private void applyMove(PlayerInfo player, Move move) {
      if (!validMove(player, move))
         return;

      say(move.getComment(), player);

      if (winBattle(move.getFromNode().getDiceCount(), move.getToNode().getDiceCount())) {
         applyWin(move);
      }

   }

   private void applyWin(Move move) {
      int diceCountFrom = move.getFromNode().getDiceCount();
      move.getToNode().setDiceCount(diceCountFrom > 1 ? diceCountFrom - 1 : 1);
      move.getToNode().setOccupier(move.getFromNode().getOccupier());
      move.getFromNode().setDiceCount(diceCountFrom > 1 ? 1 : 0);
      say("Player " + move.getFromNode().getOccupier().getName() + " occupies " + move.getToNode().getName(), null);
   }

   private void say(String message, PlayerInfo playerInfo) {
      try {
         Thread.sleep(1000);
      } catch (Exception e) {

      }
      if (playerInfo == null)
         ;
      //System.out.println("Game: " + message);
      //else
      //System.out.println(playerInfo.getName() + ": " + message);
   }

   private boolean winBattle(int diceCount1, int diceCount2) {
      int diceSum1 = rollDice(diceCount1);
      int diceSum2 = rollDice(diceCount2);

      return diceSum1 > diceSum2;
   }

   private int rollDice(int diceCount) {
      int sum = 0;
      for (int i = 0; i < diceCount; i++) {
         sum += random.nextInt(6) + 1;
      }
      return sum;
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

      // player must have at least one dice on fromNode
      if (fromNode.getDiceCount() <= 0)
         return false;

      return true;
   }

   private boolean isGameOver() {
      int canMoveCount = 0;
      // 2 or more players are able to move
      for (PlayerInfo player : gameState.getPlayers()) {
         if (canMove(player)) {
            canMoveCount++;
         }
      }
      return canMoveCount < 2;
   }

   private boolean canMove(PlayerInfo player) {
      return GameStateUtils.getPlayerNodes(player, gameState).size() > 0;
   }
}
