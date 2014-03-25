package com.videoplaza.nodewar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoplaza.nodewar.mechanics.GameEngine;
import com.videoplaza.nodewar.state.Game;
import com.videoplaza.nodewar.state.GameMap;
import com.videoplaza.nodewar.state.MapParser;
import com.videoplaza.nodewar.state.PlayerInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Main {
   private static MapParser mapParser = new MapParser(new ObjectMapper());

   public static void main(String[] args) throws Exception {
      Random random = new SecureRandom();
      Tournament tournament = parseFile(args.length > 1 ? args[1] : "game_config.csv", random);

      Map<String, Integer> totals = new HashMap<String, Integer>();

      for (PlayerInfo player : tournament.players) {
         totals.put(player.name, 0);
      }

      for (int i = 0; i < tournament.games.size(); i++) {
         Map<String, Integer> score = singleGame(i, tournament.games.get(i), random);
         for (String playerName : score.keySet()) {
            totals.put(playerName, totals.get(playerName) + score.get(playerName));
         }
      }

      System.out.println("Tournament ended. Final results: " + GameEngine.formatScoresText(totals));
   }

   private static Map<String, Integer> singleGame(int gameNumber, Game game, Random random) throws Exception {
      game.setMaxTurns(10);
      game.setRandom(random);
      game.distributeInitialRegionOccupants();

      Game initial = Game.fromJson(game.toJson());
      Map<String, Integer> score = new GameEngine(game, 0).runGame();
      game.occupants = initial.occupants;

      System.out.println("Initial state was: " + initial.toJson());
      game.toJson(new File("viewer/replay" + gameNumber + ".json"));
      System.out.println("Tournament done, replay written to viewer/replay" + gameNumber + ".json");
      return score;
   }

   private static Tournament parseFile(String filePath, Random random) throws Exception {
      Tournament tournament = new Tournament();
      List<MapInfo> maps = new ArrayList<>();

      File file = new File(filePath);
      BufferedReader reader = new BufferedReader(new FileReader(file));

      for (String line; (line = reader.readLine()) != null; ) {
         String[] split = line.split(",");
         if (isInteger(split[1].trim())) {
            maps.add(parseMapInfo(line));
         } else {
            tournament.players.add(parsePlayerInfo(line));
         }
      }

      for (MapInfo map : maps) {
         for (int i = 0; i < map.numberOfGames; i++) {
            ArrayList<PlayerInfo> players = new ArrayList<>(tournament.players);
            Collections.shuffle(players, random);

            tournament.games.add(new Game(loadGameMap("mapeditor/" + map.map), players));
         }
      }

      return tournament;
   }

   private static MapInfo parseMapInfo(String line) {
      String[] elements = line.split(",");
      return new MapInfo(elements[0].trim(), Integer.parseInt(elements[1].trim()));
   }

   private static boolean isInteger(String s) {
      try {
         Integer.parseInt(s);
         return true;
      } catch (NumberFormatException e) {
         return false;
      }
   }

   private static GameMap loadGameMap(String name) throws IOException {
      return mapParser.loadFile(new File(name));
   }

   private static PlayerInfo parsePlayerInfo(String line) {
      String[] elements = line.split(",");
      return new PlayerInfo(elements[0].trim(), elements[1].trim(), elements.length > 2 ? elements[2].trim() : null);
   }

   private static class Tournament {

      List<Game> games = new ArrayList<Game>();
      List<PlayerInfo> players = new ArrayList<PlayerInfo>();
   }

   private static class MapInfo {
      String map;
      Integer numberOfGames;

      private MapInfo(String map, Integer numberOfGames) {
         this.map = map;
         this.numberOfGames = numberOfGames;
      }
   }
}
