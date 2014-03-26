package com.videoplaza.nodewar.bots;

import com.videoplaza.nodewar.mechanics.Move;
import com.videoplaza.nodewar.mechanics.MoveType;
import com.videoplaza.nodewar.mechanics.PlayerController;
import com.videoplaza.nodewar.state.Game;

public class PassiveBot implements PlayerController {
   @Override
   public Move getNextMove(Game gameState) {
      return new Move(null, null, "", MoveType.DONE);
   }
}
