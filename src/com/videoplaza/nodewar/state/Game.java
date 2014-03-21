package com.videoplaza.nodewar.state;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.PlayerController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {


   public Game() {}

   public Game(GameMap map, List<PlayerInfo> players) throws Exception {

      List<PlayerController> controllers = new ArrayList<>();
      for(PlayerInfo player:players){

         if(player.getArgument() != null){
            Constructor constructor = Class.forName(player.getImplementation()).getConstructor(String.class);
            controllers.add((PlayerController) constructor.newInstance(player.getArgument()));
         } else{
            Constructor constructor = Class.forName(player.getImplementation()).getConstructor();
            controllers.add((PlayerController) constructor.newInstance());
         }

      }

      this.map = map;
      this.players = players;
      for (int i = 0; i < players.size(); i++) {
         players.get(i).id = i;
         players.get(i).game = this;
      }
      this.controllers = controllers;
      for (Region region : map.regions) {
         occupants.put(region.id, new Occupant());
      }

   }

   public GameMap map;
   public List<PlayerInfo> players;
   @JsonIgnore
   public List<PlayerController> controllers;
   public Map<Integer, Occupant> occupants = new HashMap<Integer, Occupant>();
   public List<Move> moves = new ArrayList<>();
   public Integer currentPlayer = 0;
   public Integer currentTurn = 0;

   public Integer maxTurns = 100;

   public Integer getCurrentTurn() {
      return currentTurn;
   }

   public void setCurrentTurn(Integer currentTurn) {
      this.currentTurn = currentTurn;
   }

   public Game apply(Move move) {
      move.setPlayer(currentPlayer);
      moves.add(move);
      return this;
   }

   public List<PlayerInfo> getPlayers() {
      return players;
   }

   public List<Node> getNodes() {
      ArrayList<Node> nodes = new ArrayList<>();
      for (Region region : map.regions) {
         nodes.add(new Node(this, region.id));
      }
      return nodes;
   }

   public String toJson() {
      try {
         ObjectMapper objectMapper = getObjectMapper();
         return objectMapper.writeValueAsString(this);
      } catch (JsonProcessingException e) {
         throw new RuntimeException(e);
      }
   }

   public void toJson(File file) {
      try {
         ObjectMapper objectMapper = getObjectMapper();
         objectMapper.writeValue(file, this);
      } catch (JsonProcessingException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private static ObjectMapper getObjectMapper() {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setVisibilityChecker(
         objectMapper.getSerializationConfig().getDefaultVisibilityChecker().
            withFieldVisibility(JsonAutoDetect.Visibility.ANY).
            withGetterVisibility(JsonAutoDetect.Visibility.NONE).
            withSetterVisibility(JsonAutoDetect.Visibility.NONE).
            withCreatorVisibility(JsonAutoDetect.Visibility.NONE));
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      return objectMapper;
   }

   public static Game fromJson(String json) {
      try {
         return getObjectMapper().readValue(json, Game.class);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public Integer getMaxTurns() {
      return maxTurns;
   }

   public void setMaxTurns(Integer maxTurns) {
      this.maxTurns = maxTurns;
   }

   public void setCurrentPlayer(PlayerInfo player) {
      currentPlayer = player.getId();
   }

   public PlayerInfo getCurrentPlayer() {
      return players.get(currentPlayer);
   }

   public void distributeInitialRegionOccupants(long randomSeed){
      Random random = new Random(randomSeed);
      List<Region> nonTakenRegions = new ArrayList<>();

      nonTakenRegions.addAll(map.regions);

      while(nonTakenRegions.size() > 0){
         int strength = random.nextInt(6)+1;
         for(PlayerInfo player:players){
            Region randomRegion = nonTakenRegions.remove(random.nextInt(nonTakenRegions.size()));
            occupants.get(randomRegion.id).player = player.getId();
            occupants.get(randomRegion.id).strength = strength;
            if(nonTakenRegions.size() == 0){
               break;
            }
         }
      }
   }
}
