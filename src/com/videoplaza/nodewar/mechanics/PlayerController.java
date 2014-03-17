package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.json.Game;

public interface PlayerController {

   public Move getNextMove(Game gameState);

}
