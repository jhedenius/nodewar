package com.videoplaza.nodewar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.GameMap;
import com.videoplaza.nodewar.mechanics.GameEngine;
import com.videoplaza.nodewar.state.MapParser;
import com.videoplaza.nodewar.state.PlayerInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

   public static void main(String[] args) throws Exception {
      MapParser mapParser = new MapParser(new ObjectMapper());
      GameMap gameMap = null;

      try {
         gameMap = mapParser.loadFile(new File(args.length > 0 ? args[0] : "viewer/map3.json"));
      } catch (IOException e) {
         e.printStackTrace();
         System.exit(0);
      }

      List<PlayerInfo> players = parseFile(args.length > 1 ? args[1] : "game_config.csv");

      Game gameState = new Game(gameMap, players);
      gameState.setMaxTurns(100);
      gameState.distributeInitialRegionOccupants(System.currentTimeMillis());


      Game initial = Game.fromJson(gameState.toJson());
      new GameEngine(gameState, 0).startGame();
      gameState.occupants = initial.occupants;

      System.out.println("Initial state was: " + initial.toJson());

      gameState.toJson(new File("viewer/replay.json"));
      System.out.println("Tournament done, replay written to viewer/replay.json");
   }

    private static List<PlayerInfo> parseFile(String filePath) throws IOException {
      List<PlayerInfo> players = new ArrayList<>();

      File file = new File(filePath);
      BufferedReader reader = new BufferedReader(new FileReader(file));

      String line = reader.readLine();
      while(line != null){
         players.add(parsePlayerInfo(line));
         line = reader.readLine();
      }

      return players;
   }

   private static PlayerInfo parsePlayerInfo(String line) {
      String[] elements = line.split(",");
      return new PlayerInfo(elements[0].trim(), elements[1].trim(), elements.length > 2 ? elements[2].trim(): null);
   }

}
