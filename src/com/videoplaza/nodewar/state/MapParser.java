package com.videoplaza.nodewar.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videoplaza.nodewar.json.GameMap;

import java.io.File;
import java.io.IOException;

public class MapParser {
   private final ObjectMapper objectMapper;

   public MapParser(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
   }
   public GameMap loadFile(File file) throws IOException {
      return objectMapper.readValue(file, GameMap.class);
   }

   public static void main(String[] args) throws IOException {
      MapParser mapParser = new MapParser(new ObjectMapper());
      GameMap map = mapParser.loadFile(new File("mapeditor/uk.json"));
      System.out.println(map.src);
   }

}
