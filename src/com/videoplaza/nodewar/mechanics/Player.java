package com.videoplaza.nodewar.mechanics;

import com.videoplaza.nodewar.state.GameState;

public interface Player {

   public Move getNextMove(GameState gameState);

}
