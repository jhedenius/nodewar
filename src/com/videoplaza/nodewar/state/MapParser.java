package com.videoplaza.nodewar.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoplaza.nodewar.json.GameMap;
import com.videoplaza.nodewar.json.Region;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapParser {
   private final ObjectMapper objectMapper;

   public MapParser(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
   }
   public GameMap loadFile(File file) throws IOException {
      return objectMapper.readValue(file, GameMap.class);
   }

   public Map<String,Node> createNodes(GameMap map) {
      HashMap<String, Node> nodes = new HashMap<String, Node>();
      ArrayList<Node> nodeList = new ArrayList<>();
      for (Region region : map.regions) {
         Node node = new Node();
         node.setId(""+region.id);
         node.setName(region.name);
         nodes.put(node.getId(), node);
         nodeList.add(node);
      }
      for (Region region : map.regions) {
         Node node = nodeList.get(region.id);
         for (int neighbour : region.neighbours) {
            node.getAdjacent().add(nodeList.get(neighbour));
         }
      }
      return nodes;
   }

   public static void main(String[] args) throws IOException {
      MapParser mapParser = new MapParser(new ObjectMapper());
      GameMap map = mapParser.loadFile(new File("mapeditor/uk.json"));
      Map<String, Node> nodes = mapParser.createNodes(map);
      System.out.println(map.src);
   }

}
