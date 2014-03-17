package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.GameState;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import org.junit.Test;

public class GameEngineTest {

   @Test
   public void testGame() throws Exception {
      GameState gameState = new GameState();

      gameState.setMaxTurns(100);

      PlayerInfo p1 = new PlayerInfo("p1", new SimpleBot());
      PlayerInfo p2 = new PlayerInfo("p2", new SimpleBot());
      PlayerInfo p3 = new PlayerInfo("p3", new SimpleBot());
      PlayerInfo p4 = new PlayerInfo("p4", new SimpleBot());
      PlayerInfo p5 = new PlayerInfo("p5", new SimpleBot());

      int width = 3;
      int height = 3;

      Node[][] nodes = createMatrixGraph(width, height, gameState);

      nodes[0][0].setOccupier(p1);
      nodes[0][0].setDiceCount(8);
      nodes[0][1].setOccupier(p5);
      nodes[0][1].setDiceCount(1);
      nodes[0][2].setOccupier(p2);
      nodes[0][2].setDiceCount(8);
      nodes[2][0].setOccupier(p3);
      nodes[2][0].setDiceCount(8);
      nodes[2][1].setOccupier(p5);
      nodes[2][1].setDiceCount(1);
      nodes[2][2].setOccupier(p4);
      nodes[2][2].setDiceCount(8);

      gameState.getPlayers().add(p1);
      gameState.getPlayers().add(p2);
      gameState.getPlayers().add(p3);
      gameState.getPlayers().add(p4);
      gameState.getPlayers().add(p5);

      new Thread(new DebugPrint(width, height, nodes)).start();

      new GameEngine(gameState, 0).startGame();

   }

   private Node[][] createMatrixGraph(int width, int height, GameState gameState) {
      Node[][] nodes = new Node[width][height];

      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            Node node = new Node();
            node.setName(String.valueOf("abcdefghijklmnopqrstuvw".toCharArray()[x] + String.valueOf(y)));
            nodes[x][y] = node;
            gameState.getNodes().put(node.getId(), node);
         }
      }

      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            Node node = nodes[x][y];
            if (x > 0) {
               node.getAdjacent().add(nodes[x - 1][y]);
            }
            if (x < width - 1) {
               node.getAdjacent().add(nodes[x + 1][y]);
            }
            if (y > 0) {
               node.getAdjacent().add(nodes[x][y - 1]);
            }
            if (y < height - 1) {
               node.getAdjacent().add(nodes[x][y + 1]);
            }
         }
      }

      return nodes;
   }

   private class DebugPrint implements Runnable {

      private final int width;
      private final int height;
      private Node[][] nodes;

      public DebugPrint(int width, int height, Node[][] nodes) {
         this.width = width;
         this.height = height;
         this.nodes = nodes;
      }

      @Override
      public void run() {
         while (true) {
            for (int x = 0; x < width; x++) {
               for (int y = 0; y < height; y++) {
                  System.out.print(nodes[x][y] + "\t");
               }
               System.out.println("");
            }
            System.out.println("\n\n\n");
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
         }
      }
   }
}
