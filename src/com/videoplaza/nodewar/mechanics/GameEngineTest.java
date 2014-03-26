package com.videoplaza.nodewar.mechanics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.GameMap;
import com.videoplaza.nodewar.state.Region;
import com.videoplaza.nodewar.bots.SimpleBot;
import com.videoplaza.nodewar.state.MapParser;
import com.videoplaza.nodewar.state.Node;
import com.videoplaza.nodewar.state.PlayerInfo;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

public class GameEngineTest {

   @Test
   public void testGameOnUkMap() throws Exception {
      MapParser mapParser = new MapParser(new ObjectMapper());
      GameMap gameMap = mapParser.loadFile(new File("mapeditor/uk.json"));

      PlayerInfo p1 = new PlayerInfo("p1", SimpleBot.class.getName(), null);
      PlayerInfo p2 = new PlayerInfo("p2", SimpleBot.class.getName(), null);
      PlayerInfo p3 = new PlayerInfo("p3", SimpleBot.class.getName(), null);
      PlayerInfo p4 = new PlayerInfo("p4", SimpleBot.class.getName(), null);
      PlayerInfo p5 = new PlayerInfo("p5", SimpleBot.class.getName(), null);

      Game gameState = new Game(gameMap, Arrays.asList(p1, p2, p3, p4, p5));
      gameState.setMaxTurns(100);

      gameState.distributeInitialRegionOccupants();

      Game initial = Game.fromJson(gameState.toJson());
      new GameEngine(gameState, new Random(0)).runGame();
      gameState.occupants = initial.occupants;
      gameState.toJson(new File("viewer/replay.json"));
      System.out.println("Tournament done, replay written to viewer/replay.json");
   }

   @Test
   public void testGameOnSimpleMatrix() throws Exception {
      GameMap gameMap = new GameMap();
      int width = 3;
      int height = 3;
      Region[][] nodes = createMatrixGraph(width, height, gameMap);
      PlayerInfo p1 = new PlayerInfo("p1", SimpleBot.class.getName(), null);
      PlayerInfo p2 = new PlayerInfo("p2", SimpleBot.class.getName(), null);
      PlayerInfo p3 = new PlayerInfo("p3", SimpleBot.class.getName(), null);
      PlayerInfo p4 = new PlayerInfo("p4", SimpleBot.class.getName(), null);
      PlayerInfo p5 = new PlayerInfo("p5", SimpleBot.class.getName(), null);

      Game gameState = new Game(gameMap, Arrays.asList(p1, p2, p3, p4, p5));

      gameState.setMaxTurns(100);

      new Node(gameState, nodes[0][0].id).setOccupier(p1);
      new Node(gameState, nodes[0][0].id).setDiceCount(8);
      new Node(gameState, nodes[0][1].id).setOccupier(p5);
      new Node(gameState, nodes[0][1].id).setDiceCount(1);
      new Node(gameState, nodes[0][2].id).setOccupier(p2);
      new Node(gameState, nodes[0][2].id).setDiceCount(8);
      new Node(gameState, nodes[2][0].id).setOccupier(p3);
      new Node(gameState, nodes[2][0].id).setDiceCount(8);
      new Node(gameState, nodes[2][1].id).setOccupier(p5);
      new Node(gameState, nodes[2][1].id).setDiceCount(1);
      new Node(gameState, nodes[2][2].id).setOccupier(p4);
      new Node(gameState, nodes[2][2].id).setDiceCount(8);

      //new Thread(new DebugPrint(width, height, nodes,gameState)).start();

      new GameEngine(gameState, new Random(0)).runGame();

   }

   private Region[][] createMatrixGraph(int width, int height, GameMap map) {
      Region[][] nodes = new Region[width][height];

      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            Region node = new Region();
            node.setName(String.valueOf("abcdefghijklmnopqrstuvw".toCharArray()[x] + String.valueOf(y)));
            nodes[x][y] = node;
            node.id = map.regions.size();
            node.x = x * 50 + 200;
            node.y = y * 50 + 200;
            map.regions.add(node);
         }
      }

      for (int x = 0; x < width; x++) {
         for (int y = 0; y < height; y++) {
            Region node = nodes[x][y];
            if (x > 0) {
               node.neighbours.add(nodes[x - 1][y].id);
            }
            if (x < width - 1) {
               node.neighbours.add(nodes[x + 1][y].id);
            }
            if (y > 0) {
               node.neighbours.add(nodes[x][y - 1].id);
            }
            if (y < height - 1) {
               node.neighbours.add(nodes[x][y + 1].id);
            }
         }
      }

      return nodes;
   }

   private class DebugPrint implements Runnable {

      private final int width;
      private final int height;
      private Region[][] nodes;
      private final Game game;

      public DebugPrint(int width, int height, Region[][] nodes, Game game) {
         this.width = width;
         this.height = height;
         this.nodes = nodes;
         this.game = game;
      }

      @Override
      public void run() {
         while (true) {
            for (int x = 0; x < width; x++) {
               for (int y = 0; y < height; y++) {
                  System.out.print(new Node(game, nodes[x][y].id) + "\t");
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
