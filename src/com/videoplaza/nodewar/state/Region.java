package com.videoplaza.nodewar.state;

import java.util.ArrayList;
import java.util.List;

public class Region {
   public int id;
   public int x;
   public int y;
   public String name;
   public List<Integer> neighbours = new ArrayList<Integer>();

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

}
