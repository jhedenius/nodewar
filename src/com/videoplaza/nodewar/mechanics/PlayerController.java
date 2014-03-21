package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.Game;

public interface PlayerController {

   public Move getNextMove(Game gameState);

}
